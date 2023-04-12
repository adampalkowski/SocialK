package com.example.socialk

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.socialk.camera.CameraEvent
import com.example.socialk.camera.CameraView
import com.example.socialk.camera.ImageDisplay
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.components.ScreenHeading
import com.example.socialk.create.*
import com.example.socialk.create.EditTextField
import com.example.socialk.create.FriendPicker.FriendsPickerEvent
import com.example.socialk.create.FriendPicker.IconSocialButton
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.TextFieldError
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File
import java.util.concurrent.Executor

sealed class CreateGroupEvent() {
    object GoBack : CreateGroupEvent()
    object OpenCamera : CreateGroupEvent()
    object OpenGallery : CreateGroupEvent()
    class DisplayPicture(val photo_url: String) : CreateGroupEvent()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateGroupScreen(
    viewModel: CreateGroupViewModel,
    onEvent: (CreateGroupEvent) -> Unit,
    onSubmit: (group_name: String) -> Unit, outputDirectory: File?,
    executor: Executor?,
    onImageCaptured: (Uri) -> Unit,
) {
    var image_uri by rememberSaveable{ mutableStateOf("".toUri()) }
    var uri_flow= viewModel.photo_uri.collectAsState()
    uri_flow.value.let {
        if (it != null ){
            image_uri=it

        }
    }
    var enabledButton by rememberSaveable {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier.background(color = SocialTheme.colors.uiBackground)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SocialTheme.colors.uiBackground)
        ) {

            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            var hideKeyboard by remember { mutableStateOf(false) }
            val activityTextState by rememberSaveable(stateSaver = ActivityTextStateSaver) {
                mutableStateOf(ActivityTextFieldState())
            }
            if (activityTextState.text.trim().isNotEmpty()) {
                enabledButton = true
            } else {
                enabledButton = false
            }
            Column (Modifier.verticalScroll(rememberScrollState())){
                HomeScreenHeading(
                    onEvent = { onEvent(CreateGroupEvent.GoBack) },
                    title = "Create group"
                )

                com.example.socialk.chat.ChatComponents.Divider()
                /*    ScreenHeading(onClick = {}, title = "Create group" )*/
                Spacer(modifier = Modifier.height(12.dp))
                /*SocialTextField(hint ="group name" ,
                    onImeAction = { if(activityTextState.text.trim().length>0){onSubmit(activityTextState.text)} }, textState = activityTextState )
              */  EditTextField(hint = "Group name",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                textState = activityTextState,
                modifier = Modifier, title = "Text",
                icon = R.drawable.ic_edit, focusManager = focusManager, onClick = {})
                ImageField(
                    modifier = Modifier,
                    onClick = {},
                    title = "Image",
                    description = "Set group picture",
                    icon = R.drawable.ic_add_photo,
                    openCamera = {onEvent(CreateGroupEvent.OpenCamera)},
                    openGallery = {onEvent(CreateGroupEvent.OpenGallery)},image_uri=image_uri,displayPicture={onEvent(CreateGroupEvent.DisplayPicture(image_uri.toString()))}
                )
                Spacer(modifier = Modifier.weight(1f))

            }

                IconSocialButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .focusRequester(focusRequester)
                        .padding(end = 24.dp)
                        .padding(bottom = 24.dp),
                    enabled = enabledButton,
                    text = "Pick friends",
                    backGroundColor = SocialTheme.colors.iconInteractive,
                    onEvent = {
                        onSubmit(activityTextState.text)
                    },onError={                        activityTextState.enableShowErrors()
                    },
                    textColor = SocialTheme.colors.textSecondary,
                    elevation = 2.dp,
                    icon = R.drawable.ic_start,
                    iconTint = SocialTheme.colors.textSecondary,
                    borderColor = SocialTheme.colors.uiFloated,

                )

        }


        AnimatedVisibility(
            visible = viewModel.shouldShowCamera.value,
            enter = scaleIn(
                animationSpec = tween(800),
                transformOrigin = TransformOrigin(1f, 0.2f)
            ),
            exit = scaleOut(animationSpec = tween(800), transformOrigin = TransformOrigin(1f, 0.2f))

        ) {

            CameraView(
                onEvent = { event ->
                    when (event) {
                        is CameraEvent.BackPressed -> {
                            if (viewModel.shouldShowCamera.value) {
                                Log.d("CameraEvent","backpressed")
                                viewModel.shouldShowCamera.value = false
                            }
                            if (viewModel.shouldShowPhoto.value) {
                                viewModel.shouldShowPhoto.value = false
                            }
                        }
                        is CameraEvent.SavePhoto -> {
                            Log.d("CameraEvent","save photo")

                            viewModel.shouldShowCamera.value = false
                            viewModel.shouldShowPhoto.value = false
                        }
                        else -> {}
                    }
                },
                outputDirectory = outputDirectory!!,
                executor = executor!!,
                onImageCaptured = onImageCaptured,
                onError = { Log.e("kilo", "View error:", it) }
            )

            if (viewModel.shouldShowPhoto.value) {
                if (uri_flow.value == null) {
                    viewModel.shouldShowPhoto.value = false
                } else {
                    ImageDisplay(
                        modifier = Modifier.fillMaxSize(),
                        uri_flow.value!!, onEvent = { event ->
                            when (event) {
                                is CameraEvent.RemovePhoto -> {
                                    Log.d("CameraEvent","remove photo")

                                    val photoFile =
                                        uri_flow.value!!.lastPathSegment?.let {
                                            File(outputDirectory,
                                                it
                                            )
                                        }
                                    photoFile!!.delete()
                                    viewModel.photo_uri.value="".toUri()
                                    viewModel.shouldShowPhoto.value = false
                                    viewModel.shouldShowCamera.value = true
                                }
                                is CameraEvent.BackPressed -> {
                                    Log.d("CameraEvent","back pressed2 ")



                                    viewModel.shouldShowPhoto.value = false
                                    viewModel.shouldShowCamera.value = false


                                }
                                is CameraEvent.SetPicture -> {
                                    Log.d("CameraEvent","set pciture ")

                                        Log.d("CreateGroupScreen","settingsi mage")
                                        image_uri=event.image_url

                                    viewModel.shouldShowPhoto.value = false
                                    viewModel.shouldShowCamera.value = false

                                }
                                is CameraEvent.SavePhoto -> {
                                    Log.d("CameraEvent","save pciture 2")

                                    viewModel.shouldShowCamera.value = false
                                    viewModel.shouldShowPhoto.value = false
                                }
                                is CameraEvent.ImageSent -> {
                                    Log.d("CameraEvent","image  sent 2")

                                    viewModel.shouldShowPhoto.value = false
                                    viewModel.shouldShowCamera.value = false
                                }
                                is CameraEvent.DeletePhoto -> {
                                    Log.d("CameraEvent","delete  photo 2")
                                    val photoFile =
                                        uri_flow.value!!.lastPathSegment?.let {
                                            File(outputDirectory,
                                                it
                                            )
                                        }
                                    photoFile!!.delete()
                                    /*
                                    activityViewModel?.removeParticipantImage(
                                        viewModel.camera_activity_id.value,
                                        viewModel?.currentUser!!.uid
                                    )*/
                                    viewModel.photo_uri.value="".toUri()
                                    viewModel.shouldShowPhoto.value = false
                                    viewModel.shouldShowCamera.value = false
                                    viewModel.displayPhoto.value = false
                                }
                                else -> {}
                            }

                        }, null, viewModel.displayPhoto.value
                    )
                }

            }
        }

    }
}

@Composable
fun SocialTextField(
    hint: String,
    textState: TextFieldState = remember { ActivityTextFieldState() },
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val maxLetters = 30

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                tint = SocialTheme.colors.iconSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Group name",
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = SocialTheme.colors.iconSecondary
            )
            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp), elevation = 0.dp,
            backgroundColor = SocialTheme.colors.uiBackground,
            border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
        ) {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            textState.onFocusChange(focusState.isFocused)
                            if (!focusState.isFocused) {
                                textState.enableShowErrors()
                            }
                        },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        color = SocialTheme.colors.textPrimary
                    ),
                    value = textState.text,
                    onValueChange = {
                        if (it.length <= maxLetters) {
                            textState.text = it.take(maxLetters)
                        }


                    },
                    isError = textState.showErrors(),
                    placeholder = {
                        Text(
                            color = Color(0xff757575),
                            text = hint
                        )

                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = SocialTheme.colors.textPrimary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onImeAction()
                            textState.enableShowErrors()
                        }
                    ),
                )
            }
        }
        Text(
            text = "${textState.text.length} / $maxLetters",
            textAlign = TextAlign.End,
            style = TextStyle(fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.Light),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
        textState.getError()?.let { error ->
            Row() {
                Spacer(modifier = Modifier.width(24.dp))
                TextFieldError(textError = error)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Divider()
    }


}


@Composable
fun SocialOutlinedTextField(
    hint: String = "hint",
    hideKeyboard: Boolean = false,
    onFocusClear: () -> Unit = {},
    textState: TextFieldState = remember { BasicTextFieldState() },
    onClick: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier,
    title: String,
    icon: Int
) {


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = SocialTheme.colors.iconSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = SocialTheme.colors.iconSecondary
            )
            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp), elevation = 0.dp,
            backgroundColor = SocialTheme.colors.uiBackground,
            border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
        ) {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                SocialTextField(
                    hint,
                    textState, modifier = modifier,
                )
            }
        }
        textState.getError()?.let { error ->
            Row() {
                Spacer(modifier = Modifier.width(24.dp))
                TextFieldError(textError = error)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Divider()
    }

    if (hideKeyboard) {

        focusManager.clearFocus()
        // Call onFocusClear to reset hideKeyboard state to false
        onFocusClear()
    }

}
