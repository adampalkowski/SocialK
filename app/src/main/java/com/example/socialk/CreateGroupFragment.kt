package com.example.socialk

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class CreateGroupFragment:Fragment() {

    private val viewModel by viewModels<CreateGroupViewModel>()
    private  var outputDirectory: File? =null
    private var cameraExecutor: ExecutorService?=null
    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs
            .firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }
    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
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
    private fun handleImageCapture(uri: Uri) {
        viewModel.shouldShowCamera.value = true
        viewModel.setPhotoUri(uri)
        viewModel.shouldShowPhoto.value = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("ImageFromGallery", "image received"+uri.toString())
                viewModel.setPhotoUri(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if(navigateTo==Screen.FriendsPicker){
                    val bundle =Bundle()
                    bundle.putString("group_name",viewModel.group_name.value)
                    bundle.putString("group_picture",viewModel.photo_uri.value.toString())
                    navigate(navigateTo, Screen.CreateGroup,bundle)
                }else{
                    navigate(navigateTo, Screen.CreateGroup)

                }
            }
        }
        requestCameraPermission()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateGroupScreen(viewModel,onEvent = {event->
                        when(event){
                            is CreateGroupEvent.GoBack->activity?.onBackPressedDispatcher?.onBackPressed()
                            is CreateGroupEvent.OpenCamera->{      viewModel.shouldShowCamera.value = true
                             }

                            is CreateGroupEvent.DisplayPicture->{
                                viewModel.photo_uri.value=event.photo_url.toUri()
                                viewModel.shouldShowCamera.value=true
                                viewModel.shouldShowPhoto.value=true
                                viewModel.displayPhoto.value=true
                            }
                            is CreateGroupEvent.OpenGallery -> {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                    }, onSubmit = {group_name ->
                        Toast.makeText(activity,"submit"+group_name, Toast.LENGTH_LONG).show()
                        viewModel.handleGoToFriendPicker(group_name = group_name)
                    },outputDirectory=outputDirectory, executor = cameraExecutor, onImageCaptured = ::handleImageCapture)
                }
            }
        }
    }
}

