package com.example.socialk.camera

import android.app.LocalActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import coil.compose.rememberImagePainter
import com.example.socialk.home.HomeEvent
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.socialk.R

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.getActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}

sealed class CameraEvent {
    object BackPressed : CameraEvent()
}

@Composable
fun CameraView(
    onEvent: (CameraEvent) -> Unit,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    //set status bar TRANSPARENT

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
        systemUiController.setNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    }
    //lock screen orientation
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    BackPressHandler(onBackPressed = { onEvent(CameraEvent.BackPressed) })

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    // create a mutable state to keep track of whether the icon should be shown or hidden
    val isIconVisible = remember { mutableStateOf(false) }
    var iconPosition by remember { mutableStateOf(Offset.Zero) }


    // 3
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = {
                it.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isIconVisible.value = true
                        val factory = previewView.meteringPointFactory
                        val point = factory.createPoint(event.x, event.y)
                        val action = FocusMeteringAction.Builder(point!!).build()
                        Log.d("CAMERAVIEW",event.x.toString()+" "+event.y.toString())
                        iconPosition = Offset(event.x, event.y)

                        previewView.controller?.cameraControl?.startFocusAndMetering(action)
                        previewView.controller?.cameraControl?.setZoomRatio(2f)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        previewView.controller?.cameraControl?.cancelFocusAndMetering()
                        previewView.controller?.cameraControl?.setZoomRatio(1.5f)
                    }
                    true
                }
            }
        )
        // add an IconButton on top of the AndroidView using the Box's contentAlignment parameter
        if (isIconVisible.value) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .offset(x = iconPosition.x.dp, y = iconPosition.y.dp)
                    )
            Log.d("CAMERAVIEW", iconPosition.x.toString()+" "+iconPosition.y.toString())
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            IconButton(

                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    Log.i("kilo", "ON CLICK")
                    takePhoto(
                        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                        imageCapture = imageCapture,
                        outputDirectory = outputDirectory,
                        executor = executor,
                        onImageCaptured = onImageCaptured,
                        onError = onError
                    )

                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Take picture",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier
                            .size(88.dp)
                            .padding(1.dp)
                            .border(
                                1.dp,
                                color = androidx.compose.ui.graphics.Color.White,
                                CircleShape
                            )
                    )
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
        }

    }
}

@Composable
fun ImageDisplay(modifier: Modifier, photoUri: Uri) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = androidx.compose.ui.graphics.Color.Black)
    ) {
        Image(
            painter = rememberImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(
            filenameFormat,
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("kilo", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri)
        }
    })
}