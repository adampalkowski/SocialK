package com.example.socialk.signinsignup


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialk.R
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.util.supportWideScreen

sealed class SignUpEvent {
    object SignIn : SignUpEvent()
    object SignUp : SignUpEvent()
    object SignInAsGuest : SignUpEvent()
    object NavigateBack : SignUpEvent()
    object PickUsername : SignUpEvent()
}

@OptIn(ExperimentalMaterial3Api::class) // Scaffold is experimental in m3
@Composable
fun SignUp(userViewModel:UserViewModel,viewModel: AuthViewModel?,onNavigationEvent: (SignUpEvent) -> Unit) {
    val singupFlow = viewModel?.signupFlow?.collectAsState()
    val userFlow = userViewModel?.userValidation?.collectAsState()
    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.create_account),
                onBackPressed = { onNavigationEvent(SignUpEvent.NavigateBack) }
            )
        },
        content = { contentPadding ->
            SignInSignUpScreen(
                onSignedInAsGuest = { onNavigationEvent(SignUpEvent.SignInAsGuest) },
                contentPadding = contentPadding,
                modifier = Modifier.supportWideScreen()
            ) {
                Column {
                    SignUpContent(
                        onSignUpSubmitted = { email,name, password ->
                        viewModel?.signup(name=name.trim(),email=email,password=password)
                        }
                    )
                }
            }
        }
    )
    singupFlow?.value.let {
        when(it){
            is Response.Success->{
                userViewModel?.validateUser(it.data)
                userFlow?.value?.let {
                        validationResponse ->
                    when(validationResponse){
                        is Response.Success->{
                            if(validationResponse.data){
                                LaunchedEffect(Unit){
                                    onNavigationEvent(SignUpEvent.SignIn)
                                }
                            }else{
                                LaunchedEffect(Unit){
                                    onNavigationEvent(SignUpEvent.PickUsername)
                                }
                            }

                        }
                        is Response.Failure->{
                            val context = LocalContext.current
                            Toast.makeText(context,"Failed to validate user code 102",Toast.LENGTH_LONG).show()
                        }
                        else->{}

                    }
                }
            }
            is Response.Loading ->{
                CircularProgressIndicator()
            }
            is Response.Failure ->{
                val context = LocalContext.current
                Toast.makeText(context,it.e.message,Toast.LENGTH_LONG).show()
            }
            else->{}

        }
    }
}

@Composable
fun SignUpContent(
    onSignUpSubmitted: (email: String,name:String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val passwordFocusRequest = remember { FocusRequester() }
        val emailFocusRequester = remember { FocusRequester() }
        val confirmationPasswordFocusRequest = remember { FocusRequester() }
        val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
            mutableStateOf(EmailState())
        }
        val nameState by rememberSaveable(stateSaver = NameStateSaver) {
            mutableStateOf(NameState())
        }

        Name(nameState, onImeAction = {emailFocusRequester.requestFocus()})
        Spacer(modifier = Modifier.height(16.dp))
        Email(emailState, onImeAction = { passwordFocusRequest.requestFocus() })

        Spacer(modifier = Modifier.height(16.dp))
        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { confirmationPasswordFocusRequest.requestFocus() },
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )

        Spacer(modifier = Modifier.height(16.dp))
        val confirmPasswordState = remember { ConfirmPasswordState(passwordState = passwordState) }
        Password(
            label = stringResource(id = R.string.confirm_password),
            passwordState = confirmPasswordState,
            onImeAction = { onSignUpSubmitted(emailState.text,nameState.text, passwordState.text) },
            modifier = Modifier.focusRequester(confirmationPasswordFocusRequest)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.terms_and_conditions),
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSignUpSubmitted(emailState.text,nameState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailState.isValid &&
                    passwordState.isValid && confirmPasswordState.isValid
        ) {
            Text(text = stringResource(id = R.string.create_account))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // OutlinedTextField is experimental in m3
@Composable
fun Name(
    nameState: TextFieldState = remember { NameState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
)
{
    OutlinedTextField(
        value = nameState.text,
        onValueChange = {
            nameState.text = it
        },
        label = {
            Text(
                text ="Full name",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                nameState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    nameState.enableShowErrors()
                }
            },
        isError = nameState.showErrors(),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),

    )

    nameState.getError()?.let { error -> TextFieldError(textError = error) }
}


