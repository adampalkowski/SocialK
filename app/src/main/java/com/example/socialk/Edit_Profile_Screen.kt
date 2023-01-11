package com.example.socialk

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.components.ScreenHeading
import com.example.socialk.create.CreateActivityButton
import com.example.socialk.signinsignup.EmailState
import com.example.socialk.signinsignup.TextFieldError
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class EditProfileEvent {
    object GoToProfile : EditProfileEvent()
    object LogOut : EditProfileEvent()
    object GoToSettings : EditProfileEvent()
    object GoToEditProfile : EditProfileEvent()
    object GoToHome : EditProfileEvent()
    object ConfirmChanges : EditProfileEvent()
}

@Composable
fun EditProfileScreen(onEvent: (EditProfileEvent) -> Unit,profileUrl:String){
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeading(onClick = {onEvent(EditProfileEvent.GoToProfile)}, title = "Edit profile")
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier =Modifier.height(24.dp) )
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = "profile image", modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            ClickableText(text = AnnotatedString("Change profile picture")
                , onClick ={}, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = Inter) )
            Spacer(modifier = Modifier.height(24.dp))
            editField(label="First and last name" )
            Spacer(modifier = Modifier.height(12.dp))
            editField(label="Username" )
            Spacer(modifier = Modifier.height(12.dp))
            editField(label="Profile description" )
            Spacer(modifier = Modifier.height(12.dp))
            editField(label="Email" )
            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = { onEvent(EditProfileEvent.ConfirmChanges)}, text = "Confirm changes")
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class) // OutlinedTextField is experimental in m3
@Composable
fun editField(label:String,
    emailState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            Text(
                text =label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = emailState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
    )

    emailState.getError()?.let { error -> TextFieldError(textError = error) }
}


@Preview
@Composable
fun EditProfileScreenPreview(){
    SocialTheme() {
        EditProfileScreen(onEvent = {}, profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab")

    }
}