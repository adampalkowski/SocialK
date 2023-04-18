package com.example.socialk.create

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.camera.CameraEvent
import com.example.socialk.camera.CameraView
import com.example.socialk.camera.ImageDisplay
import com.example.socialk.components.Category
import com.example.socialk.components.CustomSocialDialog
import com.example.socialk.components.PrivacyOption
import com.example.socialk.components.TagLabelItem
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.map.loadIcon
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.marosseleng.compose.material3.datetimepickers.date.domain.DatePickerShapes
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.domain.TimePickerDefaults
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.Executor

sealed class CreateEvent {
    object GoToProfile : CreateEvent()
    object GoBack : CreateEvent()
    object AllFriendsSelected : CreateEvent()
    object LogOut : CreateEvent()
    object GoToSettings : CreateEvent()
    object GoToHome : CreateEvent()
    object GoToLive : CreateEvent()
    object GoToEvent : CreateEvent()
    object GoToActivity : CreateEvent()
    object OpenCamera : CreateEvent()
    class DisplayPicture(val photo_url: String) : CreateEvent()
    object OpenGallery : CreateEvent()
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
        val invited_users: List<User>,
        val enableActivitySharing: Boolean,
        val disablePictures: Boolean,
        val disableNotification: Boolean,
        val privateChat: Boolean,
        val disableChat: Boolean,
        val selectedPrivacy: String,
        val awaitConfirmation: Boolean,
        val tags: ArrayList<String>,
    ) : CreateEvent()
}

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class
)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel,
    location: String?,
    userViewModel: UserViewModel,
    activityViewModel: ActivityViewModel?,
    onEvent: (CreateEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit, outputDirectory: File?,
    executor: Executor?,
    onImageCaptured: (Uri) -> Unit,
) {
    var image_uri by rememberSaveable { mutableStateOf("".toUri()) }
    var uri_flow = viewModel.photo_uri.collectAsState()
    uri_flow.value.let {
        if (it != null) {
            image_uri = it

        }
    }
    Log.d("createscreen", "init" + location.toString())
    val openDialog = remember { mutableStateOf(false) }
    val displayAdvancedOptions = remember { mutableStateOf(false) }
    var location = remember { mutableStateOf(location) }
    var latlng = remember { mutableStateOf("") }

    val activityTextState by rememberSaveable(stateSaver = ActivityTextStateSaver) {
        mutableStateOf(ActivityTextFieldState())
    }

    var enableActivitySharing by rememberSaveable {
        mutableStateOf(false)
    }
    var disablePictures by rememberSaveable {
        mutableStateOf(false)
    }
    var disableNotification by rememberSaveable {
        mutableStateOf(false)
    }
    var awaitConfirmation by rememberSaveable {
        mutableStateOf(false)
    }
    var privateChat by rememberSaveable {
        mutableStateOf(false)
    }
    var disableChat by rememberSaveable {
        mutableStateOf(false)
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
        mutableStateOf(LocalTime.now().noSeconds().plusHours(1).toString())
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
    val context = LocalContext.current
    if (isDateDialogShown) {
        DatePicker(onDismissRequest = { isDateDialogShown = false },
            onDateChange = { selectedDate ->
                if (selectedDate < LocalDate.now()) {
                    Toast.makeText(
                        context, "Pick future date", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    date = selectedDate
                    isDateDialogShown = false
                    dateState = date.toString()
                    viewModel.date.value = date.toString()
                }


            })
    }


    if (isTimeDialogShown) {
        TimePickerDialog(
            onDismissRequest = { isTimeDialogShown = false },
            initialTime = selectedTime,
            onTimeChange = {

                setSelectedTime(it)
                isTimeDialogShown = false
                timeState = it.toString()
                viewModel.start_time.value = it.toString()
            },
            title = { Text(text = "Select time") }, shape = RoundedCornerShape(8.dp),
            colors = TimePickerDefaults.colors(dialBackgroundColor = SocialTheme.colors.uiFloated)
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
                viewModel.duration.value = it.toString()
            },
            title = { Text(text = "Select time") }
        )
    }

    var isMapLoaded by remember { mutableStateOf(false) }
    var selectedPrivacy by remember { mutableStateOf(PrivacyOption.FRIENDS_ONLY) }
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
    var tags = ArrayList<String>()
    var latLng = LatLng(0.0, 0.0)
    var matchResult: MatchResult? = null
    if (location.value != null) {
        matchResult = pattern.find(location.value!!)
    } else {

    }
    var saved = remember { mutableStateOf(false) }
    if (matchResult != null) {
        val lat = matchResult.groupValues[1].toDouble()
        val lng = matchResult.groupValues[2].toDouble()
        latLng = LatLng(lat, lng)
        latlng.value = lat.toString() + "/" + lng.toString()
        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 13f)
    } else {
    }
    Log.d("CREATEFRAGMENT", viewModel.name.value)
    if (!activityTextState.isFocused && viewModel.name.value.isNotEmpty()) {
        activityTextState.text = viewModel.name.value
    }
    if (!descriptionTextState.isFocused && viewModel.description.value.isNotEmpty()) {
        descriptionTextState.text = viewModel.description.value
    }
    if (!isDateDialogShown && viewModel.date.value.isNotEmpty()) {
        dateState = viewModel.date.value
    }
    if (!isTimeDialogShown && viewModel.start_time.value.isNotEmpty()) {
        timeState = viewModel.start_time.value
    }
    if (!minTextState.isFocused && viewModel.min.value.isNotEmpty()) {
        minTextState.text = viewModel.min.value
    }
    if (!maxTextState.isFocused && viewModel.max.value.isNotEmpty()) {
        maxTextState.text = viewModel.max.value
    }
    if (!customLocationTextState.isFocused && viewModel.custom_location.value.isNotEmpty()) {
        customLocationTextState.text = viewModel.custom_location.value
    }
    if (viewModel.latlng.value.isNotEmpty()) {
        location.value = viewModel.latlng.value
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            activityPickerCreate(isSystemInDarkTheme(), onEvent = onEvent)
            Spacer(modifier = Modifier.height(12.dp))
            // todo finish saving the values of all the picks thorufh shared preferences
            EditTextField(hint = "Enter a name for your activity",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                textState = activityTextState,
                modifier = Modifier,
                title = "Name",
                icon = R.drawable.ic_edit,
                focusManager = focusManager,
                onClick = {},
                maxLetters = 150,
                onSaveValueCall = { focused ->
                    if (focused) {

                    } else {
                        Log.d("CreateScreen", "UNFOCUSED")
                        Log.d("CreateScreen", activityTextState.text)
                        viewModel.name.value = activityTextState.text
                    }
                })

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
                text = timeState,
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


            PrivacyField(
                modifier = Modifier,
                title = "Privacy",
                description = "Select who you intend to show the activity",
                onClick = {},
                icon = R.drawable.ic_privacy,
                selectedPrivacy = selectedPrivacy,
                onPrivacySelected = { privacyOption -> selectedPrivacy = privacyOption })
            EditTextField(hint = "Additional information",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                textState = descriptionTextState,
                modifier = Modifier,
                title = "Description",
                icon = R.drawable.ic_description,
                focusManager = focusManager,
                onClick = {},
                maxLetters = 500,
                onSaveValueCall = {
                    if (it) {

                    } else {
                        viewModel.description.value = descriptionTextState.text
                    }
                })


            AdvancedOptions(onClick = {
                displayAdvancedOptions.value = !displayAdvancedOptions.value
            }, displayAdvancedOptions.value)



            AnimatedVisibility(
                visible = displayAdvancedOptions.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column() {
                    if (location.value != null) {
                        /*//LOCATON FIELD
                        LocationField(
                            modifier = Modifier,
                            onClick = {
                                focusManager.clearFocus()
                                //open dialog to change or remove location
                                openDialog.value = true
                            },
                            title = "Location",
                            value = "Already Selected",
                            icon = R.drawable.ic_location_24
                        )
                        Log.d("createscreen", "val" + latlng.value)*/
                    } else {
                        //CUSTOM LOCATION FIELD
                        CustomLocationField(hint = "Describe the location",
                            hideKeyboard = hideKeyboard,
                            onFocusClear = { hideKeyboard = false },
                            textState = customLocationTextState,
                            modifier = Modifier,
                            title = "Custom location",
                            icon = R.drawable.ic_edit_location,
                            focusManager = focusManager,
                            onClick = {},
                            onSaveValueCall = {
                                if (!it) {
                                    viewModel.custom_location.value = customLocationTextState.text
                                }
                            })

                    }
                    CustomField(
                        modifier = Modifier,
                        onClick = {},
                        title = "Tags",
                        description = "Add tags to easier with the group of people that you are searching for.",
                        icon = R.drawable.ic_tag
                    ) {
                        var selectedFitness by remember { mutableStateOf(false) }
                        var selectedCreative by remember { mutableStateOf(false) }
                        var selectedMusic by remember { mutableStateOf(false) }
                        var selectedGames by remember { mutableStateOf(false) }
                        var selectedSocial by remember { mutableStateOf(false) }
                        var selectedEducation by remember { mutableStateOf(false) }
                        var selectedVolunteer by remember { mutableStateOf(false) }
                        var selectedTravel by remember { mutableStateOf(false) }
                        var selectedFood by remember { mutableStateOf(false) }
                        var selectedWellness by remember { mutableStateOf(false) }

                        Column(Modifier.padding(horizontal = 24.dp)) {

                            TagLabelItem(
                                title =  Category.SPORTS.label,
                                icon = R.drawable.ic_fitness,
                                selected = selectedFitness,
                                checkedChange = { selectedFitness = !selectedFitness })
                            Spacer(Modifier.height(6.dp))
                            TagLabelItem(
                                title =  Category.CREATIVE.label,
                                icon = R.drawable.ic_creative,
                                selected = selectedCreative,
                                checkedChange = { selectedCreative = !selectedCreative })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.MUSIC.label,
                                icon = R.drawable.ic_piano,
                                selected = selectedMusic,
                                checkedChange = { selectedMusic = !selectedMusic })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.GAMES.label,
                                icon = R.drawable.ic_games,
                                selected = selectedGames,
                                checkedChange = { selectedGames = !selectedGames })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.SOCIAL.label,
                                icon = R.drawable.ic_celebration,
                                selected = selectedSocial,
                                checkedChange = { selectedSocial = !selectedSocial })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.EDUCATION.label,
                                icon = R.drawable.ic_book,
                                selected = selectedEducation,
                                checkedChange = { selectedEducation = !selectedEducation })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.VOLUNTEER.label,
                                icon = R.drawable.ic_volounteer,
                                selected = selectedVolunteer,
                                checkedChange = { selectedVolunteer = !selectedVolunteer })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.TRAVEL.label,
                                icon = R.drawable.ic_travel,
                                selected = selectedTravel,
                                checkedChange = { selectedTravel = !selectedTravel })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.FOOD.label,
                                icon = R.drawable.ic_food,
                                selected = selectedFood,
                                checkedChange = { selectedFood = !selectedFood })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.WELLNESS.label,
                                icon = R.drawable.ic_wellness,
                                selected = selectedWellness,
                                checkedChange = { selectedWellness = !selectedWellness })

                        }
                        if (selectedFitness && !tags.contains(Category.SPORTS.label)) tags.add(Category.SPORTS.label) else tags.remove(Category.SPORTS.label)
                        if (selectedFood && !tags.contains(Category.FOOD.label)) tags.add(Category.FOOD.label) else tags.remove(Category.FOOD.label)
                        if (selectedMusic && !tags.contains(Category.MUSIC.label)) tags.add(Category.MUSIC.label) else tags.remove(Category.MUSIC.label)
                        if (selectedWellness && !tags.contains(Category.WELLNESS.label)) tags.add(Category.WELLNESS.label) else tags.remove(Category.WELLNESS.label)
                        if (selectedTravel && !tags.contains(Category.TRAVEL.label)) tags.add(Category.TRAVEL.label) else tags.remove(Category.TRAVEL.label)

                        if (selectedCreative && !tags.contains(Category.CREATIVE.label)) tags.add(Category.CREATIVE.label) else tags.remove(Category.CREATIVE.label)

                        if (selectedGames && !tags.contains(Category.GAMES.label)) tags.add(Category.GAMES.label) else tags.remove(Category.GAMES.label)

                        if (selectedSocial && !tags.contains(Category.SOCIAL.label)) tags.add(Category.SOCIAL.label) else tags.remove(Category.SOCIAL.label)

                        if (selectedEducation && !tags.contains(Category.EDUCATION.label)) tags.add(Category.EDUCATION.label) else tags.remove(Category.EDUCATION.label)

                        if (selectedVolunteer && !tags.contains(Category.VOLUNTEER.label)) tags.add(Category.VOLUNTEER.label) else tags.remove(Category.VOLUNTEER.label)
                    }
                    CustomField(
                        text = "Additional information",
                        modifier = Modifier,
                        title = "Participants limits",
                        icon = R.drawable.ic_checklist,
                        onClick = {},
                        description = "Set maximum and minimum users limit for activity"
                    ) {
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
                                RequirementsNumberField(
                                    "Min",
                                    numberState = minTextState,
                                    focusManager = focusManager,
                                    onSaveValueCall = {
                                        if (!it) {
                                            viewModel.min.value = minTextState.text

                                        }
                                    })
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(6.dp))
                                RequirementsNumberField(
                                    "Max",
                                    numberState = maxTextState,
                                    focusManager = focusManager,
                                    onSaveValueCall = {
                                        if (!it) {
                                            viewModel.max.value = maxTextState.text
                                        }

                                    })
                            }
                        }
                    }
                    ImageField(modifier = Modifier,
                        onClick = {},
                        title = "Image",
                        description = "Set activity image",
                        icon = R.drawable.ic_add_photo, image_uri = image_uri,
                        openCamera = { onEvent(CreateEvent.OpenCamera) },
                        openGallery = { onEvent(CreateEvent.OpenGallery) }, displayPicture = {
                            onEvent(
                                CreateEvent.DisplayPicture(image_uri.toString())
                            )
                        })
                    CustomField(
                        text = "Privacy",
                        modifier = Modifier,
                        title = "Activity settings",
                        icon = R.drawable.ic_tune,
                        onClick = {},
                        description = "",
                        disableDescription = true
                    ) {
                        ActivitySettingsBox(
                            "Activity sharing",
                            "Allow invited users to invite others to the activity.",
                            onSwitch = { enableActivitySharing = it })
                        /*  ActivitySettingsBox(
                              "Pictures",
                              "Disable attach pictures to the activity.",
                              onSwitch = { disablePictures = it })*/
                        ActivitySettingsBox(
                            "Notification",
                            "Don't notify invited users.",
                            onSwitch = { disableNotification = it })

                        ActivitySettingsChatBox("Chat",
                            "Make chat visible only for activity participants.",
                            secondDescription = "Disable chat ",
                            onSwitch = { privateChat = it },
                            secondOnSwitch = { disableChat = it })
                        ActivitySettingsBox(
                            "Participant confirmation",
                            "Confirm or decline users request to join the activity.",
                            onSwitch = { awaitConfirmation = it })


                    }
                }

            }

            Spacer(modifier = Modifier.height(48.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                GoToMapButton(
                    borderColor = if (location.value != null) {
                        Color(0xFF52b69a)
                    } else {
                        SocialTheme.colors.iconInteractive
                    },
                    textColor = if (location.value != null) {
                        SocialTheme.colors.textSecondary
                    } else {
                        SocialTheme.colors.iconInteractive
                    }, backgroundColor = if (location.value != null) {
                        Color(0xFF52b69a)
                    } else {
                        Color.Transparent
                    },
                    onClick = {
                        if (location.value != null) {
                            focusManager.clearFocus()
                            //open dialog to change or remove location
                            openDialog.value = true
                        } else {
                            onEvent(CreateEvent.GoToMap)
                        }

                    }, modifier = Modifier.width(200.dp), text = if (location.value != null) {
                        "Preview"
                    } else {
                        "Visit map"
                    }
                )
                Spacer(Modifier.width(24.dp))
                CreateActivityButton(onClick = {

                    if (minTextState.text.isNotEmpty() && maxTextState.text.isNotEmpty() && minTextState.text.toInt() > maxTextState.text.toInt()) {
                        Toast.makeText(
                            context,
                            "Min participant limit greater than max",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
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
                                custom_location = customLocationTextState.text,
                                enableActivitySharing = enableActivitySharing,
                                disablePictures = disablePictures,
                                disableNotification = disableNotification,
                                privateChat = privateChat,
                                disableChat = disableChat,
                                selectedPrivacy = selectedPrivacy.label,
                                awaitConfirmation = awaitConfirmation,
                                tags=tags
                            )
                        )
                    }


                }, text = "Ready", modifier = Modifier.width(200.dp)){
                    androidx.compose.material3.Text(
                        text = "Ready",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = Inter,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }


        /*  if (!  activityTextState.isFocused &&!descriptionTextState.isFocused && !customLocationTextState.isFocused && !maxTextState.isFocused&& !minTextState.isFocused  ) {
              BottomBar(
                  onTabSelected = { screen -> bottomNavEvent(screen) },
                  currentScreen = Create
              )
          }*/
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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    ClickableText(text = AnnotatedString("Dismiss"), style = TextStyle(
                        color = SocialTheme.colors.textPrimary.copy(0.6f),
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ), onClick = { openDialog.value = false })

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fix),
                        null,
                        tint = SocialTheme.colors.iconInteractive
                    )
                    ClickableText(text = AnnotatedString("Change"), style = TextStyle(
                        color = SocialTheme.colors.iconInteractive,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ), onClick = {
                        openDialog.value = false
                        onEvent(CreateEvent.GoToMap)
                    })
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        null,
                        tint = SocialTheme.colors.error
                    )
                    ClickableText(text = AnnotatedString("Remove"), style = TextStyle(
                        color = SocialTheme.colors.error,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ), onClick = {
                        openDialog.value = false
                        location.value = null
                        viewModel.latlng.value = ""
                    })


                }


            }


        }


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
                            Log.d("CameraEvent", "backpressed")
                            viewModel.shouldShowCamera.value = false
                        }
                        if (viewModel.shouldShowPhoto.value) {
                            viewModel.shouldShowPhoto.value = false
                        }
                    }
                    is CameraEvent.SavePhoto -> {
                        Log.d("CameraEvent", "save photo")

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
                                Log.d("CameraEvent", "remove photo")

                                val photoFile =
                                    uri_flow.value!!.lastPathSegment?.let {
                                        File(
                                            outputDirectory,
                                            it
                                        )
                                    }
                                photoFile!!.delete()
                                viewModel.photo_uri.value = "".toUri()
                                viewModel.shouldShowPhoto.value = false
                                viewModel.shouldShowCamera.value = true
                            }
                            is CameraEvent.BackPressed -> {
                                Log.d("CameraEvent", "back pressed2 ")



                                viewModel.shouldShowPhoto.value = false
                                viewModel.shouldShowCamera.value = false


                            }
                            is CameraEvent.SetPicture -> {
                                Log.d("CameraEvent", "set pciture ")

                                Log.d("CreateGroupScreen", "settingsi mage")
                                image_uri = event.image_url

                                viewModel.shouldShowPhoto.value = false
                                viewModel.shouldShowCamera.value = false

                            }
                            is CameraEvent.SavePhoto -> {
                                Log.d("CameraEvent", "save pciture 2")

                                viewModel.shouldShowCamera.value = false
                                viewModel.shouldShowPhoto.value = false
                            }
                            is CameraEvent.ImageSent -> {
                                Log.d("CameraEvent", "image  sent 2")

                                viewModel.shouldShowPhoto.value = false
                                viewModel.shouldShowCamera.value = false
                            }
                            is CameraEvent.DeletePhoto -> {
                                Log.d("CameraEvent", "delete  photo 2")
                                val photoFile =
                                    uri_flow.value!!.lastPathSegment?.let {
                                        File(
                                            outputDirectory,
                                            it
                                        )
                                    }
                                photoFile!!.delete()
                                /*
                                activityViewModel?.removeParticipantImage(
                                    viewModel.camera_activity_id.value,
                                    viewModel?.currentUser!!.uid
                                )*/
                                viewModel.photo_uri.value = "".toUri()
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

@Composable
fun ActivitySettingsChatBox(
    title: String,
    description: String,
    secondDescription: String,
    onSwitch: (Boolean) -> Unit,
    secondOnSwitch: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = SocialTheme.colors.textPrimary
            )

            Row() {

                Text(
                    text = description,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = SocialTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch2(onCheckedChange = { it -> onSwitch(it) })
            }
            Row() {

                Text(
                    text = secondDescription,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = SocialTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch2(onCheckedChange = { it -> secondOnSwitch(it) })
            }
        }

    }

}

@Composable
fun ActivitySettingsBox(title: String, description: String, onSwitch: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = SocialTheme.colors.textPrimary
            )
            Text(
                text = description,
                fontFamily = Inter,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                color = SocialTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch2(onCheckedChange = { it -> onSwitch(it) })
    }

}


@Composable
fun AdvancedOptions(onClick: () -> Unit, displayed: Boolean) {
    val icons = if (!displayed) {
        painterResource(id = R.drawable.ic_down)
    } else {
        painterResource(id = R.drawable.ic_up)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column() {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp), contentAlignment = Center
            ) {
                Row(modifier = Modifier.align(Center)) {
                    Icon(
                        painter = icons,
                        contentDescription = null,
                        tint = SocialTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Additional options",
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
    imeAction: ImeAction = ImeAction.Done,
    onSaveValueCall: (Boolean) -> Unit
) {
    val regex = Regex("^[0-9]*$")
    val descriptionFocusRequester = remember { FocusRequester() }
    editNumberField(
        label = hint,
        editTextState = numberState,
        maxLetters = 3,
        onImeAction = { imeAction },
        regex = regex,
        modifier = Modifier
            .widthIn(50.dp, 100.dp)
            .height(56.dp)
            .focusRequester(descriptionFocusRequester),
        onSaveValueCall = onSaveValueCall
    )
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


@Composable
fun Switch2(
    scale: Float = 1.5f,
    width: Dp = 36.dp,
    height: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
    checkedTrackColor: Color = SocialTheme.colors.iconInteractive,
    uncheckedTrackColor: Color = SocialTheme.colors.iconPrimary.copy(alpha = 0.75f),
    gapBetweenThumbAndTrackEdge: Dp = 1.dp,
    onCheckedChange: ((Boolean) -> Unit)?
) {

    val switchON = remember {
        mutableStateOf(false) // Initially the switch is ON
    }
    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge

    // To move thumb, we need to calculate the position (along x axis)
    val animatePosition = animateFloatAsState(
        targetValue = if (switchON.value)
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
    )
    Box(modifier = Modifier.width(48.dp)) {
        Canvas(
            modifier = Modifier
                .align(Center)
                .size(width = width, height = height)
                .scale(scale = scale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (switchON.value) {
                                onCheckedChange?.invoke(false)

                            } else {
                                onCheckedChange?.invoke(true)

                            }
                            // This is called when the user taps on the canvas
                            switchON.value = !switchON.value
                        }
                    )
                }
        ) {

            // Track
            drawRoundRect(
                color = if (switchON.value) checkedTrackColor else uncheckedTrackColor,
                cornerRadius = CornerRadius(x = 24.dp.toPx(), y = 24.dp.toPx()),
            )

            // Thumb
            drawCircle(
                color = Color.White,
                radius = thumbRadius.toPx(),
                center = Offset(
                    x = animatePosition.value,
                    y = size.height / 2
                )
            )
        }

    }

    Spacer(modifier = Modifier.height(18.dp))

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(onDismissRequest: () -> Unit, onDateChange: (LocalDate) -> Unit) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        onDateChange = onDateChange,
        // Optional but recommended parameter to provide the title for the dialog
        title = { Text(text = "Select date") },
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
        },
        shape = RoundedCornerShape(8.dp)
    )

}
