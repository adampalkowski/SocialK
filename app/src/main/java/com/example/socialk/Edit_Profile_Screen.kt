package com.example.socialk

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.PickUsername.UsernameState
import com.example.socialk.PickUsername.UsernameStateSaver
import com.example.socialk.UserProfile.DescriptionState
import com.example.socialk.UserProfile.DescriptionStateSaver
import com.example.socialk.components.ScreenHeading
import com.example.socialk.create.ActivityTextStateSaver
import com.example.socialk.create.CreateActivityButton
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.*
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class EditProfileEvent {
    object GoToProfile : EditProfileEvent()
    object LogOut : EditProfileEvent()
    object GoToSettings : EditProfileEvent()
    object GoToEditProfile : EditProfileEvent()
    object GoToHome : EditProfileEvent()
    object ConfirmChanges : EditProfileEvent()
    object PickImage : EditProfileEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel?,
    onEvent: (EditProfileEvent) -> Unit,
    profileUrl: String
) {
    var user: User = UserData.user!!
    val nameFocusRequester = remember { FocusRequester() }
    val usernameFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }


    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
    val nameState by rememberSaveable(stateSaver = NameStateSaver) {
        mutableStateOf(UsernameState())
    }

    val descriptionState by rememberSaveable(stateSaver = DescriptionStateSaver) {
        mutableStateOf(DescriptionState())
    }


    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeading(onClick = { onEvent(EditProfileEvent.GoToProfile) }, title = "Edit profile")
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(1000.dp),
                onClick = { onEvent(EditProfileEvent.PickImage) }) {
                Image(
                    painter = rememberAsyncImagePainter(user.pictureUrl),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile image", modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(text = AnnotatedString("Change profile picture"),
                onClick = { onEvent(EditProfileEvent.PickImage) },
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Inter
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            editField(
                label = "First and last name",
                maxLetters = 30,
                onImeAction = { nameFocusRequester.requestFocus() },
                editTextState = nameState,
                modifier = Modifier.focusRequester(nameFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))

            editField(
                label = "Username",
                maxLetters = 20,
                onImeAction = { usernameFocusRequester.requestFocus() },
                editTextState = usernameState,
                modifier = Modifier.focusRequester(usernameFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))

            editField(
                label = "Profile description",
                onImeAction = { descriptionFocusRequester.requestFocus() },
                maxLetters = 150,
                editTextState = descriptionState,
                modifier = Modifier.focusRequester(descriptionFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onEvent(EditProfileEvent.ConfirmChanges) },
                modifier = Modifier.fillMaxWidth(),
                enabled = descriptionState.isValid &&
                        usernameState.isValid && nameState.isValid
            ) {
                Text(text = "Confirm changes")
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class) // OutlinedTextField is experimental in m3
@Composable
fun editField(
    label: String, maxLetters: Int, modifier: Modifier = Modifier,
    editTextState: TextFieldState = remember { TextFieldState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = editTextState.text,
        onValueChange = {
            if (it.length <= maxLetters) {
                editTextState.text = it
            }

        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                editTextState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    editTextState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = editTextState.showErrors(),
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
    Text(
        text = "${editTextState.text.length} / $maxLetters",
        textAlign = TextAlign.End,
        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.Light),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
    )
    editTextState.getError()?.let { error -> TextFieldError(textError = error) }
}


@Preview
@Composable
fun EditProfileScreenPreview() {
    SocialTheme() {

    }
}