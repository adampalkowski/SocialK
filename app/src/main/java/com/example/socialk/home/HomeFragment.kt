package com.example.socialk.home

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.rememberImagePainter
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.camera.CameraEvent
import com.example.socialk.camera.CameraView
import com.example.socialk.camera.ImageDisplay
import com.example.socialk.camera.getActivity
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
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
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = true
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs
            .firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }


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
                } else if (navigateTo == Screen.Map) {
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
                    if (shouldShowCamera.value) {
                        CameraView(
                            onEvent = { event ->
                                when (event) {
                                    is CameraEvent.BackPressed -> {
                                        if (shouldShowCamera.value) {
                                            shouldShowCamera.value = false
                                        } else {
                                            activity?.onBackPressedDispatcher?.onBackPressed()
                                        }
                                    }
                                }
                            },
                            outputDirectory = outputDirectory,
                            executor = cameraExecutor,
                            onImageCaptured = ::handleImageCapture,
                            onError = { Log.e("kilo", "View error:", it) }
                        )
                        if (shouldShowPhoto.value) {
                            ImageDisplay(modifier = Modifier.fillMaxSize(), photoUri)
                        }
                    } else {
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
                                            if (shouldShowCamera.value) {
                                                shouldShowCamera.value = false
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
                                            viewModel.handleGoToUserProfile(event.user_id)
                                        }
                                        is HomeEvent.GoToChat -> {
                                            viewModel.handleGoToChat(event.activity)
                                        }
                                        is HomeEvent.OpenCamera -> {
                                            shouldShowCamera.value = true
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
                                }
                            )
                        }
                    }

                }
            }
        }

    }
}

