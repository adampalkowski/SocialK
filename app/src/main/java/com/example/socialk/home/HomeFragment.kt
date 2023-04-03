package com.example.socialk.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.ZoneId
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by activityViewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val activityViewModel by activityViewModels<ActivityViewModel>()
    private val activeUsersViewModel by viewModels<ActiveUsersViewModel>()
    private  var outputDirectory: File? =null
    private var cameraExecutor: ExecutorService?=null

    private val TAG = "HOMEFRAGMENT"
    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        viewModel.shouldShowCamera.value = true
        viewModel.setPhotoUri(uri)
        viewModel.shouldShowPhoto.value = true

    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs
            .firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    override fun onStart() {
        super.onStart()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        Log.d("CHATFRAGMENT",    ZoneId.getAvailableZoneIds().toString())

    }

    override fun onStop() {

        super.onStop()
        Log.d(TAG,"on stop")
        if (outputDirectory!=null){
            Log.d(TAG,"on destroy +output")
            if (viewModel.shouldShowPhoto.value){
                if (viewModel.photo_uri.value!=null ){
                    Log.d(TAG,"on destroy +uri")
                    val photoFile= File(outputDirectory,viewModel.photo_uri.value?.lastPathSegment)
                    photoFile.delete()
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor?.shutdown()
        if (outputDirectory!=null){
            Log.d(TAG,"on destroy +output")

            if (viewModel.photo_uri.value!=null){
                if (viewModel.photo_uri.value?.lastPathSegment!=null){
                    Log.d(TAG,"on destroy +uri")
                    val photoFile= File(outputDirectory,viewModel.photo_uri.value?.lastPathSegment)
                    photoFile.delete()
                }

            }
        }
    }
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        } else {
            Log.i("kilo", "Permission denied")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"on destroy view")

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)

    }
    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo == Screen.Chat) {
                    val bundle = Bundle()
                    bundle.putSerializable("activity", viewModel.clicked_chat_activity.value)
                    navigate(navigateTo, Screen.Home, bundle)
                } else if (navigateTo == Screen.FriendsPicker) {
                    val bundle = Bundle()
                    bundle.putSerializable("activity", viewModel.clicked_chat_activity.value)
                    navigate(navigateTo, Screen.Home, bundle)
                }else if (navigateTo == Screen.Map) {
                    val bundle = Bundle()
                    bundle.putSerializable("latlng", viewModel.clicked_location_activity.value)
                    navigate(navigateTo, Screen.Home, bundle)
                } else if (navigateTo == Screen.UserProfile) {
                    val bundle = Bundle()
                    bundle.putSerializable("user_id", viewModel.clicked_profile.value)
                    navigate(navigateTo, Screen.Home, bundle)
                } else {
                    navigate(navigateTo, Screen.Home)

                }
            }
        }
        activityViewModel?.getActivitiesForUser(authViewModel?.currentUser?.uid)
        activeUsersViewModel?.getActiveUsersForUser(authViewModel?.currentUser?.uid)
        viewModel.activity_link.value.let {
            if (it != null) {
                Log.d("HomeFragment", it.toString())
                activityViewModel.getActivity(it)
                viewModel.resetLink()
            }


            requestCameraPermission()

            outputDirectory = getOutputDirectory()
            cameraExecutor = Executors.newSingleThreadExecutor()

            return ComposeView(requireContext()).apply {
                setContent {
                        val systemUiController = rememberSystemUiController()
                        SocialTheme {

                            HomeScreen(systemUiController, activeUsersViewModel,
                                activityViewModel,
                                chatViewModel = chatViewModel,
                                authViewModel,
                                homeViewModel = viewModel,
                                onEvent = { event ->
                                    when (event) {
                                        is HomeEvent.GoToProfile -> viewModel.handleGoToProfile()
                                        is HomeEvent.BackPressed -> {
                                            if (viewModel.shouldShowCamera.value) {
                                                viewModel.shouldShowCamera.value = false
                                            } else {
                                                activity?.onBackPressedDispatcher?.onBackPressed()
                                            }
                                        }
                                        is HomeEvent.LogOut -> viewModel.handleLogOut()
                                        is HomeEvent.GoToSettings -> viewModel.handleGoToSettings()
                                        is HomeEvent.GoToMemories -> viewModel.handleGoToMemories()
                                        is HomeEvent.GoToMap -> {
                                            viewModel.handleGoToMapActivity(event.latlng)
                                        }
                                        is HomeEvent.GoToProfileWithID -> {
                                            if (event.user_id.equals(UserData.user!!.id)){
                                                viewModel.handleGoToProfile()

                                            }else{
                                                viewModel.handleGoToUserProfile(event.user_id)

                                            }
                                        }
                                        is HomeEvent.GoToChat -> {
                                            viewModel.handleGoToChat(event.activity)
                                        }
                                        is HomeEvent.OpenCamera -> {
                                            viewModel.shouldShowCamera.value = true
                                                viewModel.camera_activity_id.value= event.activity_id
                                        }
                                        is HomeEvent.ActivityLiked -> {
                                            activityViewModel.likeActivity(
                                                event.activity.id,
                                                UserData.user!!
                                            )
                                        }
                                        is HomeEvent.ActivityUnLiked -> {
                                            activityViewModel.unlikeActivity(
                                                event.activity.id,
                                                UserData.user!!
                                            )
                                        }

                                        is HomeEvent.DisplayPicture->{
                                            viewModel.camera_activity_id.value= event.activity_id
                                            viewModel.photo_uri.value=event.photo_url.toUri()
                                            viewModel.shouldShowCamera.value=true
                                            viewModel.shouldShowPhoto.value=true
                                            viewModel.displayPhoto.value=true

                                        }
                                        is HomeEvent.RemovePhotoFromGallery->{
                                            if (outputDirectory!=null ){
                                                if (viewModel.photo_uri.value!=null){
                                                    val photoFile= File(outputDirectory,viewModel.photo_uri.value?.lastPathSegment)
                                                    photoFile.delete()
                                                    activityViewModel.addImageToActivityState.value=null
                                                }
                                            }

                                        }
                                        is HomeEvent.LeaveLiveActivity->{
                                            Log.d("HOMESCREEN","here")

                                            activeUsersViewModel.leaveLiveActivity(event.activity_id,event.user_id)
                                        }
                                        is HomeEvent.DestroyLiveActivity->{
                                            activeUsersViewModel.deleteActiveUser(event.id)
                                        }
                                        is HomeEvent.GoToFriendsPicker->{
                                            Log.d("HOmesCreen","FRIENDSPICKER")
                                            viewModel.handleGoToFriendsPicker(event.activity)
                                        }
                                    }
                                },
                                bottomNavEvent = { screen ->
                                    when (screen) {
                                        is Home -> viewModel.handleGoToHome()
                                        is com.example.socialk.Map -> viewModel.handleGoToMap()
                                        is Chats -> viewModel.handleGoToChats()
                                        is Profile -> viewModel.handleGoToProfile()
                                        is Create -> viewModel.handleGoToCreate()
                                    }
                                },outputDirectory,cameraExecutor, onImageCaptured =::handleImageCapture
                            )
                        }
                    }

                }
            }
        }

}

