package com.example.socialk

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.socialk.PickUsername.UsernameState
import com.example.socialk.PickUsername.UsernameStateSaver
import com.example.socialk.UserProfile.DescriptionState
import com.example.socialk.UserProfile.DescriptionStateSaver
import com.example.socialk.components.ScreenHeading
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
    class ConfirmChanges(val firstAndLastName:String,val description:String) : EditProfileEvent()
    object PickImage : EditProfileEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel?,
    onEvent: (EditProfileEvent) -> Unit,
    profileUrl: String
) {
    var user_flow= userViewModel?.userState?.collectAsState()
    var user_flow2= userViewModel?.currentUserProfile?.collectAsState()
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


    Log.d("Edit_profile_screen",user.pictureUrl.toString())
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

                SubcomposeAsyncImage(
                    model =  ImageRequest.Builder(LocalContext.current)
                        .data(user.pictureUrl)
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(128.dp)
                )



                }


            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(text = AnnotatedString("Change profile picture"),
                onClick = {
                    onEvent(EditProfileEvent.PickImage) },
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
                label = "Profile description",
                onImeAction = { descriptionFocusRequester.requestFocus() },
                maxLetters = 150,
                editTextState = descriptionState,
                modifier = Modifier.focusRequester(descriptionFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onEvent(EditProfileEvent.ConfirmChanges(nameState.text,descriptionState.text)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = descriptionState.isValid && nameState.isValid
            ) {
                Text(text = "Confirm changes")
            }
        }

    }
    user_flow?.value.let {
        when(it){
            is Response.Success->{
                Log.d("Edit_profile_screen",it.data.toString())
                user=it.data
                userViewModel?.resetUserValue()

                Toast.makeText(LocalContext.current,it.data.pictureUrl.toString(),Toast.LENGTH_LONG).show()
            }
            is Response.Loading->{

            }
            is Response.Failure->{}
            else->{}
        }
    }
    user_flow2?.value.let {
        if(it!=null){
            Log.d("Edit_profile_screen",user.toString())
            user=it
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // OutlinedTextField is experimental in m3
@Composable
fun editField(
    label: String, maxLetters: Int, modifier: Modifier = Modifier,
    editTextState: TextFieldState = remember { TextFieldState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    onSaveValueCall:(Boolean) -> Unit = {}
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
                color=SocialTheme.colors.textPrimary.copy(alpha = 0.75f)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                editTextState.onFocusChange(focusState.isFocused)
                onSaveValueCall(focusState.isFocused)
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
        ), colors = TextFieldDefaults.outlinedTextFieldColors(textColor = SocialTheme.colors.textPrimary)
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


@OptIn(ExperimentalMaterial3Api::class) // OutlinedTextField is experimental in m3
@Composable
fun editNumberField(
    label: String, maxLetters: Int, modifier: Modifier = Modifier,
    editTextState: TextFieldState = remember { TextFieldState() },
    imeAction: ImeAction = ImeAction.Next,regex:Regex,
    onImeAction: () -> Unit = {},
    onSaveValueCall:(Boolean) -> Unit = {},max:Int=999
) {
    OutlinedTextField(
        value = editTextState.text,
        onValueChange = {
            Log.d("CREATESCREEN",it.length.toString())
            if (regex.containsMatchIn(it)) {
                    if (it.toIntOrNull() ?: 0 <= max) {
                        editTextState.text = it
                    }
            }


        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                editTextState.onFocusChange(focusState.isFocused)
                onSaveValueCall(focusState.isFocused)
                if (!focusState.isFocused) {
                    editTextState.enableShowErrors()

                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = editTextState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

    editTextState.getError()?.let { error -> TextFieldError(textError = error) }
}
