package com.example.socialk.create

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.components.CustomSocialDialog
import com.example.socialk.components.UserPicker
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.map.loadIcon
import com.example.socialk.model.Chat
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.TextFieldError
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.marosseleng.compose.material3.datetimepickers.date.domain.DatePickerShapes
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.domain.TimePickerColors
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

sealed class CreateEvent {
    object GoToProfile : CreateEvent()
    object AllFriendsSelected : CreateEvent()
    object LogOut : CreateEvent()
    object GoToSettings : CreateEvent()
    object GoToHome : CreateEvent()
    object GoToLive : CreateEvent()
    object GoToEvent : CreateEvent()
    object GoToActivity : CreateEvent()
    object GoToMap : CreateEvent()
    object ClearState : CreateEvent()
    class UserSelected(user: User) : CreateEvent() {
        val user = user
    }

    class UserUnSelected(user: User) : CreateEvent() {
        val user = user
    }

    class GroupSelected(chat: Chat) : CreateEvent() {
        val chat = chat
    }

    class GroupUnSelected(chat: Chat) : CreateEvent() {
        val chat = chat
    }

    data class CreateActivity(
        val title: String,
        val date: String,
        val start_time: String,
        val time_length: String,
        val description: String,
        val min: String,
        val max: String,
        val custom_location: String,
        val location: String,
        val invited_users: List<User>
    ) : CreateEvent()
}

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun CreateScreen(
    location: String?,
    userViewModel: UserViewModel,
    activityViewModel: ActivityViewModel?,
    onEvent: (CreateEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit
) {
    Log.d("createscreen", "init" + location.toString())
    val openDialog = remember { mutableStateOf(false) }
    var location = remember { mutableStateOf(location) }
    var latlng = remember { mutableStateOf("") }

    val activityTextState by rememberSaveable(stateSaver = ActivityTextStateSaver) {
        mutableStateOf(ActivityTextFieldState())
    }
    val descriptionTextState by rememberSaveable(stateSaver = BasicTextFieldStateSaver) {
        mutableStateOf(BasicTextFieldState())
    }
    val customLocationTextState by rememberSaveable(stateSaver = BasicTextFieldStateSaver) {
        mutableStateOf(BasicTextFieldState())
    }
    val minTextState by rememberSaveable(stateSaver = NumberTextFieldStateSaver) {
        mutableStateOf(NumberTextFieldState())
    }
    val maxTextState by rememberSaveable(stateSaver = NumberTextFieldStateSaver) {
        mutableStateOf(NumberTextFieldState())
    }
    var timeState by rememberSaveable {
        mutableStateOf(LocalTime.now().noSeconds().plusHours(1))
    }
    var dateState by rememberSaveable {
        mutableStateOf(LocalDate.now().toString())
    }
    var locationState by rememberSaveable {
        mutableStateOf("")
    }
    var timeLengthState by rememberSaveable {
        mutableStateOf("01:00")
    }
    var isDateDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var isTimeDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var isTimeLengthDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val isKeyboardOpen by keyboardAsState() // true or false
    var date: LocalDate? by remember {
        mutableStateOf(null)
    }
    // Initializing a Calendar
    val (selectedTime, setSelectedTime) = rememberSaveable {
        mutableStateOf(LocalTime.now().noSeconds())
    }
    if (isDateDialogShown) {
        DatePicker(onDismissRequest = { isDateDialogShown = false },
            onDateChange = {
                date = it
                isDateDialogShown = false
                dateState = date.toString()

            })
    }

    if (isTimeDialogShown) {
        TimePickerDialog(
            onDismissRequest = { isTimeDialogShown = false },
            initialTime = selectedTime,
            onTimeChange = {

                setSelectedTime(it)
                isTimeDialogShown = false
                timeState = it
            },
            title = { Text(text = "Select time") }
        )
    }
    var hideKeyboard by remember { mutableStateOf(false) }
    if (isTimeLengthDialogShown) {
        TimePickerDialog(
            onDismissRequest = { isTimeLengthDialogShown = false },
            initialTime = selectedTime,
            onTimeChange = {

                setSelectedTime(it)
                isTimeLengthDialogShown = false
                timeLengthState = it.toString()
            },
            title = { Text(text = "Select time") }
        )
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 11f)
    }
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true
            )
        )
    }
    var bitmap: BitmapDescriptor? =
        loadIcon(LocalContext.current, UserData.user?.pictureUrl!!, R.drawable.ic_person)
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val pattern = "\\((-?\\d+\\.\\d+),(-?\\d+\\.\\d+)\\)".toRegex()
    var latLng = LatLng(0.0, 0.0)
    var matchResult: MatchResult? = null
    if (location.value != null) {
        matchResult = pattern.find(location.value!!)
    } else {

    }

    if (matchResult != null) {
        val lat = matchResult.groupValues[1].toDouble()
        val lng = matchResult.groupValues[2].toDouble()
        latLng = LatLng(lat, lng)
        latlng.value = lat.toString() + "/" + lng.toString()
        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 13f)
    } else {
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .clickable { hideKeyboard = true },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            activityPickerCreate(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))
            EditTextField(hint = "Enter a name for your activity",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                textState = activityTextState,
                modifier = Modifier, title = "Name",
                icon = R.drawable.ic_edit, focusManager = focusManager, onClick = {})

            //DATE FIELD
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                    isDateDialogShown = true
                },
                title = "Date",
                text = dateState,
                icon = R.drawable.ic_calendar,
                description = "Select the date for your activity"
            )
            //START TIME FIELD
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                    isTimeDialogShown = true
                },
                title = "Start time",
                text = timeState.toString(),
                icon = R.drawable.ic_schedule,
                description = "Select the time for your activity"
            )
            //TIME LENGATH FIELD
            CreateClickableTextField(
                onClick = {
                    focusManager.clearFocus()
                    isTimeLengthDialogShown = true
                },
                modifier = Modifier,
                title = "Duration",
                text =
                if (!timeLengthState.split(":")[0].equals("00")) {
                    if (timeLengthState.split(":")[0].equals("01")) {
                        timeLengthState.split(":")[0].toInt()
                            .toString() + " h " + " " + timeLengthState.split(":")[1].toInt()
                            .toString() + " min"

                    } else {
                        timeLengthState.split(":")[0].toInt()
                            .toString() + " h " + " " + timeLengthState.split(":")[1].toInt()
                            .toString() + " min"
                    }
                } else {

                    timeLengthState.split(":")[1].toInt().toString() + " " + " min"
                },

                description = "Select duration of your activity",
                icon = R.drawable.ic_hourglass
            )

            AdvancedOptions(onClick = {})
/*
            if(location.value!=null){
                //LOCATON FIELD

                LocationField(  modifier = Modifier,
                    onClick = {
                        focusManager.clearFocus()
                        //open dialog to change or remove location
                        openDialog.value=true
                    },
                    title = "Location",
                    value = "Already Selected",
                    icon = R.drawable.ic_location_24)
                Log.d("createscreen","val"+latlng.value)
            }else{
                //CUSTOM LOCATION FIELD
                EditTextField(hint = "Describe the location",
                    hideKeyboard = hideKeyboard,
                    onFocusClear = { hideKeyboard = false },textState=customLocationTextState,
                    modifier = Modifier, title = "Custom location",
                    icon = R.drawable.ic_edit_location, focusManager = focusManager, onClick = {})

            }

            //DESCRIPTION FIELD
            EditTextField(hint = "Additional information",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },textState=descriptionTextState,
                modifier = Modifier, title = "Description",
                icon = R.drawable.ic_description, focusManager = focusManager, onClick = {})

            RequirementsField(
                modifier = Modifier,        hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                onClick = {    }, minState =minTextState, maxState =maxTextState ,
                title = "Participants limits",
                value = locationState,
                icon = R.drawable.ic_checklist ,focusManager = focusManager
            )
*/

            Spacer(modifier = Modifier.height(48.dp))


            CreateActivityButton(onClick = {
                Log.d("Ready", "button" + latlng.value)
                onEvent(
                    CreateEvent.CreateActivity(
                        title = activityTextState.text,
                        date = dateState.toString(),
                        start_time = timeState.toString(),
                        time_length = timeLengthState.toString(),
                        invited_users = arrayListOf(),
                        description = descriptionTextState.text,
                        location = latlng.value,
                        min = minTextState.text,
                        max = maxTextState.text,
                        custom_location = customLocationTextState.text
                    )
                )

            }, text = "Ready", modifier = Modifier)
            Spacer(modifier = Modifier.height(64.dp))
        }


        /*  if (!  activityTextState.isFocused &&!descriptionTextState.isFocused && !customLocationTextState.isFocused && !maxTextState.isFocused&& !minTextState.isFocused  ) {
              BottomBar(
                  onTabSelected = { screen -> bottomNavEvent(screen) },
                  currentScreen = Create
              )
          }*/
    }
    activityViewModel?.isActivityAddedState?.value.let {
        when (it) {
            is Response.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Center
            ) {
                CircularProgressIndicator()
            }
            is Response.Success -> {
                onEvent(CreateEvent.GoToHome)
                onEvent(CreateEvent.ClearState)
            }
            is Response.Failure -> Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "FAILURE", fontSize = 50.sp)
            }
            else -> {}
        }
    }
    if (openDialog.value) {
        CustomSocialDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = {

            },
            onCancel = { openDialog.value = false },
            title = "Location selected",
            info = "Location was picked from map, if current location isn't correct change or remove it",
            icon = R.drawable.ic_location_24,
            actionButtonText = "Delete"
        ) {

            Column() {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp), shape = RoundedCornerShape(8.dp)
                ) {
                    GoogleMap(
                        Modifier.fillMaxSize(),
                        cameraPositionState,
                        properties = properties, onMapLoaded = {
                            isMapLoaded = true
                        },
                        uiSettings = uiSettings
                    ) {
                        MarkerInfoWindow(
                            state = MarkerState(
                                position = latLng
                            )
                        ) {
                            Column() {
                                Card(shape = RoundedCornerShape(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(color = SocialTheme.colors.uiBackground)
                                            .padding(6.dp)
                                    ) {
                                        androidx.compose.material.Text(
                                            text = "Current location",
                                            style = TextStyle(
                                                fontFamily = Inter,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp
                                            ),
                                            color = SocialTheme.colors.textPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                        onClick = { openDialog.value = false }) {
                        Box(
                            modifier = Modifier
                                .background(color = SocialTheme.colors.uiBackground)
                                .padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            ClickableText(text = AnnotatedString("Dismiss"), style = TextStyle(
                                color = SocialTheme.colors.textPrimary,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ), onClick = { openDialog.value = false })
                        }

                    }


                    Card(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                        onClick = {
                            openDialog.value = false
                            onEvent(CreateEvent.GoToMap)
                        }) {
                        Box(
                            modifier = Modifier
                                .background(color = SocialTheme.colors.uiBackground)
                                .padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            ClickableText(text = AnnotatedString("Change"), style = TextStyle(
                                color = Color.Green,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ), onClick = {
                                openDialog.value = false
                                onEvent(CreateEvent.GoToMap)
                            })
                        }

                    }
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                        onClick = {
                            openDialog.value = false
                            location.value = null
                        }) {
                        Box(
                            modifier = Modifier
                                .background(color = SocialTheme.colors.uiBackground)
                                .padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            ClickableText(text = AnnotatedString("Remove"), style = TextStyle(
                                color = Color.Red,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ), onClick = {
                                openDialog.value = false
                                location.value = null
                            })
                        }
                    }

                }

            }


        }
    }

}

@Composable
fun AdvancedOptions(onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick }) {
        Column() {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp), contentAlignment = Center) {
                Row(modifier = Modifier.align(Center)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_down),
                        contentDescription = null,
                        tint = SocialTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Advanced options",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Light,
                            color = SocialTheme.colors.textPrimary,
                            fontSize = 14.sp
                        )
                    )
                }
            }
            Divider()
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LocationField(
    modifier: Modifier.Companion,
    onClick: () -> Unit,
    title: String,
    value: String,
    icon: Int
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
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
            Card(
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                onClick = onClick
            ) {
                Box(
                    modifier = Modifier
                        .background(color = SocialTheme.colors.uiBackground)
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                ) {
                    ClickableText(text = AnnotatedString(value), style = TextStyle(
                        fontSize = 16.sp, fontFamily = Inter,
                        fontWeight = FontWeight.Normal
                    ), onClick = { onClick() })
                }


            }

        }

        Divider()
    }

}

@Composable
fun RequirementsField(
    onClick: (Int) -> Unit,
    modifier: Modifier,
    hideKeyboard: Boolean = false,
    onFocusClear: () -> Unit = {},
    focusManager: FocusManager,
    title: String,
    value: String = "value", minState: TextFieldState, maxState: TextFieldState,
    icon: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
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

        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                RequirementsNumberField("Min", numberState = minState, focusManager = focusManager)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                RequirementsNumberField("Max", numberState = maxState, focusManager = focusManager)
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


@Composable
fun ConfigureSwitchItem() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Disable chat",
            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Light),
            color = SocialTheme.colors.iconPrimary
        )
        val checkedState = remember { mutableStateOf(false) }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checkedState.value, colors = SwitchDefaults.colors(
                checkedThumbColor = SocialTheme.colors.textPrimary,
                checkedTrackColor = SocialTheme.colors.textPrimary,
                disabledCheckedTrackColor = SocialTheme.colors.uiFloated,
                disabledCheckedThumbColor = SocialTheme.colors.uiFloated,
                disabledUncheckedThumbColor = SocialTheme.colors.uiFloated,
                disabledUncheckedTrackColor = SocialTheme.colors.uiFloated,
                uncheckedThumbColor = SocialTheme.colors.uiFloated,
                uncheckedTrackColor = SocialTheme.colors.uiFloated
            ),

            onCheckedChange = { checkedState.value = it }
        )
    }
}

@Composable
fun ConfigureField(
    onClick: (Int) -> Unit,
    modifier: Modifier,

    title: String,
    value: String = "value",
    icon: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
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

        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            ConfigureSwitchItem()
            ConfigureSwitchItem()
            ConfigureSwitchItem()

        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
    }
}

@Composable
fun RequirementsNumberField(
    hint: String, numberState: TextFieldState,
    focusManager: FocusManager,
    imeAction: ImeAction = ImeAction.Done
) {
    val regex = Regex("^[0-9]+$")
    Card(
        modifier = Modifier
            .widthIn(50.dp, 100.dp)
            .height(48.dp)
            .padding(0.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
        elevation = 0.dp,
        backgroundColor = SocialTheme.colors.uiBackground
    ) {
        Box(modifier = Modifier.background(color = SocialTheme.colors.uiBackground)) {
            TextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    numberState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        numberState.enableShowErrors()
                    }
                },
                textStyle = TextStyle(fontSize = 14.sp),
                value = numberState.text,
                onValueChange = {

                    if (it.length < 4) {
                        if (regex.containsMatchIn(it)) {
                            numberState.text = it
                        }
                    }
                },
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
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
        }
    }


}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(onDismissRequest: () -> Unit, onDateChange: (LocalDate) -> Unit) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        onDateChange = onDateChange,
        // Optional but recommended parameter to provide the title for the dialog
        title = { Text(text = "Select date") },
        containerColor = SocialTheme.colors.uiBackground,
        titleContentColor = SocialTheme.colors.textPrimary,
        shapes = object : DatePickerShapes {
            override val currentMonthDaySelected: Shape
                get() = RoundedCornerShape(8.dp)
            override val currentMonthDayToday: Shape
                get() = RoundedCornerShape(4.dp)
            override val currentMonthDayUnselected: Shape
                get() = RoundedCornerShape(8.dp)
            override val month: Shape
                get() = RoundedCornerShape(4.dp)
            override val nextMonthDay: Shape
                get() = RoundedCornerShape(4.dp)
            override val previousMonthDay: Shape
                get() = RoundedCornerShape(4.dp)
            override val year: Shape
                get() = RoundedCornerShape(4.dp)
        }
    )

}
