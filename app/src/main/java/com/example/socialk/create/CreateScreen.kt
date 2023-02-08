package com.example.socialk.create

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.model.Response
import com.example.socialk.signinsignup.EmailState
import com.example.socialk.signinsignup.SignUpEvent
import com.example.socialk.signinsignup.TextFieldError
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.marosseleng.compose.material3.datetimepickers.date.domain.DatePickerShapes
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

sealed class CreateEvent {
    object GoToProfile : CreateEvent()
    object LogOut : CreateEvent()
    object GoToSettings : CreateEvent()
    object GoToHome : CreateEvent()
    object GoToLive : CreateEvent()
    object GoToEvent : CreateEvent()
    object GoToActivity : CreateEvent()
    object ClearState : CreateEvent()
    data class CreateActivity(
        val title: String,
        val date: String,
        val start_time: String,
        val time_length: String
    ) : CreateEvent()
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(activityViewModel: ActivityViewModel?, onEvent: (CreateEvent) -> Unit, bottomNavEvent: (Destinations) -> Unit) {



    val activityTextState by rememberSaveable(stateSaver = ActivityTextStateSaver) {
        mutableStateOf(ActivityTextFieldState())
    }

    var timeState by rememberSaveable {
        mutableStateOf(LocalTime.now().noSeconds())
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

            EditTextField(hint = "What are you planning?",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                textState = activityTextState,
                modifier = Modifier, title = "Text",
                icon = R.drawable.ic_edit, focusManager = focusManager, onClick = {})

            //DATE FIELD
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                    isDateDialogShown = true
                },
                title = "Date",
                value = dateState,
                icon = R.drawable.ic_calendar
            )
            //START TIME FIELD
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                    isTimeDialogShown = true
                },
                title = "Start time",
                value = timeState.toString(),
                icon = R.drawable.ic_schedule
            )
            //TIME LENGATH FIELD
            CreateClickableTextField(
                onClick = {
                    focusManager.clearFocus()
                    isTimeLengthDialogShown = true
                },
                modifier = Modifier,
                title = "Time length",
                value =
                if( !timeLengthState.split(":")[0].equals("00")){
                    if(  timeLengthState.split(":")[0].equals("01")){
                        timeLengthState.split(":")[0].toInt().toString()+" hour "+" "+timeLengthState.split(":")[1].toInt().toString()+" minutes"

                    }else{
                        timeLengthState.split(":")[0].toInt().toString()+" hours "+" "+timeLengthState.split(":")[1].toInt().toString()+" minutes"
                    }
                }else{

                    timeLengthState.split(":")[1].toInt().toString()+" "+" minutes"
                },


                icon = R.drawable.ic_hourglass
            )

            //LOCATON FIELD
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                },
                title = "Location",
                value = locationState,
                icon = R.drawable.ic_location_24
            )
            //DESCRIPTION FIELD
            EditTextField(hint = "Additional information",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                modifier = Modifier, title = "Description",
                icon = R.drawable.ic_description, focusManager = focusManager, onClick = {})

            //CUSTOM LOCATION FIELD
            EditTextField(hint = "Describe the location",
                hideKeyboard = hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                modifier = Modifier, title = "Custom location",
                icon = R.drawable.ic_edit_location, focusManager = focusManager, onClick = {})


            RequirementsField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                },
                title = "Participants limits",
                value = locationState,
                icon = R.drawable.ic_checklist
            )
            ConfigureField(
                modifier = Modifier,
                onClick = {
                    focusManager.clearFocus()
                },
                title = "Configure",
                value = locationState,
                icon = R.drawable.ic_settings
            )
            Spacer(modifier = Modifier.height(48.dp))

            CreateActivityButton(onClick = {
                onEvent(
                    CreateEvent.CreateActivity(
                        title = activityTextState.text,
                        date = dateState.toString(),
                        start_time = timeState.toString(),
                        time_length = timeLengthState.toString()
                    )
                )

            }, text = "Create activity")

            Spacer(modifier = Modifier.height(64.dp))

        }
        if (!isKeyboardOpen) {
            BottomBar(
                onTabSelected = { screen -> bottomNavEvent(screen) },
                currentScreen = Create
            )
        }
    }
    activityViewModel?.isActivityAddedState?.value.let {
        when(it){
            is Response.Loading-> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center){
                CircularProgressIndicator()
            }
            is Response.Success->  {onEvent(CreateEvent.GoToHome)
            onEvent(CreateEvent.ClearState)}
            is Response.Failure-> Box(modifier = Modifier.fillMaxSize()){
                Text(text = "FAILURE", fontSize = 50.sp)
            }
        }
    }



}
@Composable
fun RequirementsField(
    onClick: (Int) -> Unit,
    modifier: Modifier,
    title: String,
    value:String="value",
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

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)){
            Column( modifier=Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(6.dp))
                RequirementsNumberField("Min")
            }
            Column(modifier=Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(6.dp))
                RequirementsNumberField("Max")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
    }
}
@Composable
fun ConfigureSwitchItem(){
    Row (modifier = Modifier.fillMaxWidth()){
        Text(text = "Disable chat",style= TextStyle(fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.iconPrimary)
        val checkedState = remember { mutableStateOf(false) }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checkedState.value, colors = SwitchDefaults.colors(checkedThumbColor = SocialTheme.colors.textPrimary, checkedTrackColor =SocialTheme.colors.textPrimary , disabledCheckedTrackColor = SocialTheme.colors.uiFloated
                , disabledCheckedThumbColor = SocialTheme.colors.uiFloated, disabledUncheckedThumbColor = SocialTheme.colors.uiFloated , disabledUncheckedTrackColor =  SocialTheme.colors.uiFloated,
                uncheckedThumbColor = SocialTheme.colors.uiFloated, uncheckedTrackColor = SocialTheme.colors.uiFloated),

            onCheckedChange = { checkedState.value = it }
        )
    }
}
@Composable
fun ConfigureField(
    onClick: (Int) -> Unit,
    modifier: Modifier,
    title: String,
    value:String="value",
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
        Column(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)) {
            ConfigureSwitchItem()
            ConfigureSwitchItem()
            ConfigureSwitchItem()

        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
    }
}

@Composable
fun RequirementsNumberField(hint:String) {
    var textState = remember { NumberTextFieldState()}
    Card(modifier = Modifier.widthIn(50.dp,100.dp).height(48.dp).padding(0.dp),shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp,color=SocialTheme.colors.uiFloated), elevation = 0.dp, backgroundColor = SocialTheme.colors.uiBackground) {
        Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
            TextField(
                modifier = Modifier,
                textStyle = TextStyle(fontSize = 14.sp),
                value = textState.text,
                onValueChange = {
                    if(it.length<4){
                        textState.text =it
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
                    errorIndicatorColor = Color.Transparent
                ),
                keyboardOptions =  KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
        }
    }



}
@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


@Composable
fun EditTextField(
    hint: String = "What are you planning",
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
            shape = RoundedCornerShape(12.dp),elevation=0.dp,
            backgroundColor = SocialTheme.colors.uiBackground,
            border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
        ) {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                ActivityTextField(hint,
                    textState,
                    focusManager = focusManager
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(onDismissRequest: () -> Unit, onDateChange: (LocalDate) -> Unit) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        onDateChange = onDateChange,
        // Optional but recommended parameter to provide the title for the dialog
        title = { Text(text = "Select date") },
        textContentColor = SocialTheme.colors.textPrimary,
        containerColor = SocialTheme.colors.uiBackground,
        titleContentColor = SocialTheme.colors.textPrimary,
        iconContentColor = SocialTheme.colors.iconPrimary,
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
