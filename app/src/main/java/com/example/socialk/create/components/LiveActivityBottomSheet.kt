package com.example.socialk.create.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.example.socialk.create.LiveEvent
import com.example.socialk.create.LiveScreenContent
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun BottomDialogLiveActivity(state: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
                 onEvent:(LiveEvent)->Unit, activeUsersViewModel: ActiveUsersViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false)  }
    val displayParticipants = rememberSaveable { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val flow= activeUsersViewModel.isActiveUsersAddedState .collectAsState()
    BackHandler(state.isVisible) {
        coroutineScope.launch { state.hide() }
    }
    SocialTheme() {
        ModalBottomSheetLayout(sheetShape = RoundedCornerShape(12.dp),
            sheetState = state, sheetBackgroundColor = SocialTheme.colors.uiFloated,
            sheetContentColor = SocialTheme.colors.textPrimary, scrimColor = Color(0x7E313131),
            sheetContent = {
                LiveScreenContent(activeUsersViewModel =activeUsersViewModel , onEvent =onEvent )
                flow.value.let {
                    when(it){
                        is Response.Success->{
                                    onEvent(LiveEvent.SendLiveMessage)
                                    onEvent(LiveEvent.CloseDialog)
                        }
                        is Response.Loading->{
                            Log.d("BOTTOMDIALOGLIVE","LOADINg")
                            CircularProgressIndicator()
                        }
                        is Response.Failure->{
                            Log.d("BOTTOMDIALOGLIVE","NOFILUR")
                        }
                    }
                }

            }
        ){
        }

    }

}
