package com.example.socialk.signinsignup


import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.socialk.model.Response

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.ui.theme.*
import com.example.socialk.ui.theme.Typography
import com.example.socialk.util.supportWideScreen
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialk.ProfileEvent
import com.example.socialk.di.UserViewModel
import com.google.android.gms.auth.api.identity.SignInClient

sealed class WelcomeEvent {
    object GoToRegister : WelcomeEvent()
    object GoToSignIn : WelcomeEvent()
    object ContinueWithGoogle : WelcomeEvent()
    object ContinueWithFacebook : WelcomeEvent()
    object GoToHome : WelcomeEvent()
}

@Composable
fun WelcomeScreen(userViewModel:UserViewModel?,authViewModel: AuthViewModel,onEvent: (WelcomeEvent) -> Unit, navigateToHome:() ->Unit,
                  viewModel: AuthViewModel = hiltViewModel()) {
    val userFlow= userViewModel?.userValidation?.collectAsState()
    if(authViewModel.isUserAuthenticated){
        userViewModel?.validateUser(authViewModel?.currentUser!!)

        userFlow?.value?.let {
                validationResponse ->
            when(validationResponse){
                is Response.Success->{

                    Toast.makeText(LocalContext.current,"authenticated",Toast.LENGTH_LONG).show()
                    onEvent(WelcomeEvent.GoToHome)
                }
                is Response.Failure->{
                }
            }
        }
    }
    Surface(modifier = Modifier
        .fillMaxSize()
        .supportWideScreen()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                color = SocialTheme.colors.uiBackground
            )
        ) {
            Text(text = "Welcome to Social", textAlign = TextAlign.Center,
                fontSize =24.sp,
                color=SocialTheme.colors.textPrimary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 48.dp) )

            SingInWays(onEvent=onEvent, modifier = Modifier.align(Alignment.BottomCenter) )
        }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credentials = viewModel?.oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials?.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                    viewModel?.signInWithGoogle(googleCredentials)
                } catch (it: ApiException) {
                    print(it)
                }
            }
        }

        fun launch(signInResult: BeginSignInResult) {
            val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
            launcher.launch(intent)
        }
        /*OneTapSignIn(
            launch = {
                launch(it)

            }
        )*/

      /*  SignInWithGoogle(
            navigateToHomeScreen = { signedIn ->
                if (signedIn) {
                    navigateToHome()
                }
            }
        )*/
    }
}

/*
@Composable
fun SignInWithGoogle(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when(val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
    }
}
*/
@Composable
fun ProgressBar() {
    Box(

        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        androidx.compose.material.CircularProgressIndicator()

    }
}/*
@Composable
fun OneTapSignIn(
    viewModel: AuthViewModel = hiltViewModel(),
    launch: (result: BeginSignInResult) -> Unit
) {
    when(val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> oneTapSignInResponse.data?.let {

            LaunchedEffect(it) {
                launch(it)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(oneTapSignInResponse.e)
        }
    }
}*/

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = "Sds",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_person_waving
    } else {
        R.drawable.ic_person_waving
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun SingInWays(
    onEvent: (WelcomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier= modifier
        .padding(horizontal = 24.dp, vertical = 48.dp)
        .verticalScroll(rememberScrollState())) {

        ContinueWithButton(R.drawable.ic_google_icon,"Continue with Google",onClick={onEvent(WelcomeEvent.ContinueWithGoogle)})
        Spacer(modifier = modifier.height(24.dp))

        ContinueWithButton(R.drawable.facebookicon,"Continue with Facebook", onClick = {onEvent(WelcomeEvent.ContinueWithFacebook)})
        Spacer(modifier = modifier.height(16.dp))

        orBox(modifier = modifier
            .height(54.dp)
            .fillMaxWidth())
        RegisterForFreeButton(modifier,onClick={onEvent(WelcomeEvent.GoToRegister)})
        Spacer(modifier = modifier.height(24.dp))

        Box(modifier =modifier.fillMaxWidth(), contentAlignment = Alignment.Center ){
            ClickableText(text = AnnotatedString("Sign in")
                , style = Typography.button,
                onClick ={onEvent(WelcomeEvent.GoToSignIn)} )
        }

    }
}

@Composable
fun orBox(modifier: Modifier){
    Box(modifier = modifier.fillMaxWidth()){
        Row(modifier =Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically ) {
            Box(modifier = Modifier
                .width(120.dp)
                .height(1.dp)
                .background(color = Color(0xFFCFAEFA)))
            Spacer(modifier = Modifier.width(24.dp))
            Text(text ="Or" , style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp
                , color =Color(0xFF6A6A6A) )
            Spacer(modifier = Modifier.width(24.dp))
            Box(modifier = Modifier
                .width(120.dp)
                .height(1.dp)
                .background(color = Color(0xFFCFAEFA)))

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterForFreeButton(modifier: Modifier,onClick: () -> Unit) {
    val isDark= isSystemInDarkTheme()
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .wrapContentSize(Alignment.Center)

    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(if (isDark) Color.DarkGray else Color.White)
                .border(
                    1.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color.LightGray else Color(0xFFCCCCCC)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.register_button),
                textAlign = TextAlign.Center,
                style = Typography.body2,
                color=SocialTheme.colors.textPrimary
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinueWithButton(icon:Int,text:String,onClick:()->Unit){
    Card(
        onClick=onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
          ,
        shape = RoundedCornerShape(12.dp),
    ) {

        val isDark = isSystemInDarkTheme()
        Box(modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.DarkGray else Gray2)
            .border(
                1.dp,
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Gray3 else Color.White
            )){
            Row(modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = icon),
                    contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text, fontSize = 14.sp
                    ,color=SocialTheme.colors.textPrimary,
                    style = Typography.body2,
                    textAlign = TextAlign.Center )
            }

        }

    }
}
@Composable
fun ColorScheme.isLight() = this.background.luminance() > 0.5

@Preview(name = "Welcome light theme", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Welcome dark theme", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    SocialTheme() {
    }
}
