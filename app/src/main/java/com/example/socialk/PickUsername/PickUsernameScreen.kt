package com.example.socialk.PickUsername

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.di.UserViewModel
import com.example.socialk.home.HomeEvent
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.*
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class PickUserEvent {
    object GoToHome : PickUserEvent()
    object NavigateBack : PickUserEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUsernameScreen(userViewModel: UserViewModel?,authViewModel:AuthViewModel?,onEvent: (PickUserEvent) -> Unit){
    val usernameFlow = userViewModel?.isUsernameAddedFlow?.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
    Surface(modifier = Modifier.fillMaxSize(), color = SocialTheme.colors.uiBackground) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            Text(text= "Select your social username to help others identify you"
                , color = SocialTheme.colors.textPrimary, style = TextStyle(fontFamily = Inter,
                    fontSize = 24 .sp,
                    fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(48.dp))
            androidx.compose.material3.OutlinedTextField(
                value = usernameState.text,
                onValueChange = {
                    if (it.length <= 25)   usernameState.text = it
                },
                label = {
                    androidx.compose.material3.Text(
                        text = "Username",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        usernameState.onFocusChange(focusState.isFocused)
                        if (!focusState.isFocused) {
                            usernameState.enableShowErrors()
                        }
                    },
                textStyle = MaterialTheme.typography.bodyMedium,
                isError = usernameState.showErrors(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction =  ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusRequester.requestFocus()
                    }
                ),
            )
            usernameState.getError()?.let { error -> TextFieldError(textError = error) }
            Spacer(modifier = Modifier.height(24.dp))
            Button(shape = RoundedCornerShape(12.dp),
                onClick = {
                    userViewModel?.addUsernameToUser(authViewModel!!.currentUser!!.uid,usernameState.text.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = usernameState.isValid
            ) {
               Text(text = stringResource(id = R.string.set_username),color=SocialTheme.colors.textSecondary, style = TextStyle(fontFamily = Inter,
               fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
               )
            }

        }

    }
    usernameFlow?.value?.let {
            databaseResponse ->
        when(databaseResponse){
            is Response.Success->{
                Toast.makeText(LocalContext.current,"username set", Toast.LENGTH_LONG).show()
                onEvent(PickUserEvent.GoToHome)
            }
            is Response.Failure->{
            }
        }
    }
}



@Preview
@Composable
fun previewPickUsernameScreen(){
    PickUsernameScreen(onEvent = {}, userViewModel = null, authViewModel = null)
}