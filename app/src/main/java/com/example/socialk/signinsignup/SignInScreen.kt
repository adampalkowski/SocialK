package com.example.socialk.signinsignup

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialk.R
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.util.supportWideScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SignInEvent {
    object SignIn: SignInEvent()
    object SignUp : SignInEvent()
    object SignInAsGuest : SignInEvent()
    object NavigateBack : SignInEvent()
    object PickUsername : SignInEvent()
}

@OptIn(ExperimentalMaterial3Api::class) // Scaffold is experimental in m3
@Composable
 fun SignIn(userViewModel: UserViewModel?, viewModel: AuthViewModel?, onNavigationEvent: (SignInEvent) -> Unit) {
    val loginFLow = viewModel?.loginFlow?.collectAsState()
    val userFlow = userViewModel?.userValidation?.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarErrorText = stringResource(id = R.string.feature_not_available)
    val snackbarActionLabel = stringResource(id = R.string.dismiss)

    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.sign_in),
                onBackPressed = { onNavigationEvent(SignInEvent.NavigateBack) }
            )
        },
        content = { contentPadding ->
            SignInSignUpScreen(
                modifier = Modifier.supportWideScreen(),
                contentPadding = contentPadding,
                onSignedInAsGuest = { onNavigationEvent(SignInEvent.SignInAsGuest) }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SignInContent(
                        onSignInSubmitted = { email,password->
                            viewModel?.signin(email.trim(),password.trim())
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = snackbarErrorText,
                                    actionLabel = snackbarActionLabel
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.forgot_password))
                    }
                }
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ErrorSnackbar(
            snackbarHostState = snackbarHostState,
            onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    loginFLow?.value?.let {
        when(it){
            is Response.Success->{
                userViewModel?.validateUser(it.data)
                userFlow?.value?.let {
                    validationResponse ->
                    when(validationResponse){
                        is Response.Success->{
                            if(validationResponse.data){
                                LaunchedEffect(Unit){
                                    onNavigationEvent(SignInEvent.SignIn)
                                }
                            }else{
                                LaunchedEffect(Unit){
                                    onNavigationEvent(SignInEvent.PickUsername)
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
                Toast.makeText(context,"Failed to login, probably wrong email or password",Toast.LENGTH_LONG).show()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInContent(
    onSignInSubmitted: (email: String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }
        val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
            mutableStateOf(EmailState())
        }
        Email(emailState, onImeAction = { focusRequester.requestFocus() })
        Spacer(modifier = Modifier.height(16.dp))

        val passwordState = remember { PasswordState() }
        val enabled=    emailState.isValid && passwordState.isValid
        val onSubmit = {
            if (emailState.isValid && passwordState.isValid) {
                onSignInSubmitted(emailState.text, passwordState.text)
            }
        }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { onSubmit() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .background(
                    Color(0xFF505F95),
                    shape = RoundedCornerShape(12.dp)
                )
                .height(54.dp)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, Color.White),
            onClick = { onSubmit() },
            enabled = emailState.isValid && passwordState.isValid,

            ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (enabled) Color(0xFF4763C9) else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in),
                    color = Color.White,
                textAlign = TextAlign.Center)
            }

        }

    }
}

@Composable
fun ErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { }
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                content = {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                action = {
                    data.visuals.actionLabel?.let {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = stringResource(id = R.string.dismiss),
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}


