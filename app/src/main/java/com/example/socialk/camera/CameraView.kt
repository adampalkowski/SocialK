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
import android.view.ScaleGestureDetector
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
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
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import kotlin.math.pow

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CameraView(
    onEvent: (CameraEvent) -> Unit,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    //set status bar TRANSPARENT

    val flash_on = remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
        systemUiController.setNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    }
    //lock screen orientation
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    // 1

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    BackPressHandler(onBackPressed = { onEvent(CameraEvent.BackPressed) })
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<CameraInfo?>(null) }


    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        cameraControl = camera.cameraControl
        cameraInfo = camera.cameraInfo
        preview.setSurfaceProvider(previewView.surfaceProvider)

    }
    flash_on.value.let {
        if (it) {
            Log.d("cameraview", "enable torhc")
            cameraControl?.enableTorch(true)
        } else {
            Log.d("cameraview", "dsiable torhc")
            cameraControl?.enableTorch(false)
        }
    }


    // create a mutable state to keep track of whether the icon should be shown or hidden
    val isIconVisible = remember { mutableStateOf(false) }
    var iconPosition by remember { mutableStateOf(Offset.Zero) }
    val zoomSensitivity = 0.6f
    var currentZoom by remember { mutableStateOf(1f) }
    val scaleGestureDetector = remember {
        ScaleGestureDetector(
            previewView.context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scale = cameraInfo?.zoomState?.value?.zoomRatio!! * detector.scaleFactor

                    // Update camera zoom level
                    cameraControl?.setZoomRatio(scale)

                    return true
                }
            }
        )
    }
    // 3
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = {
                it.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isIconVisible.value = true
                        val factory = previewView.meteringPointFactory
                        val point = factory.createPoint(event.x, event.y)
                        val action = FocusMeteringAction.Builder(point!!).build()

                        cameraControl?.startFocusAndMetering(action)
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
            Log.d("CAMERAVIEW", iconPosition.x.toString() + " " + iconPosition.y.toString())
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(bottom = 20.dp)
                .padding(end = 12.dp)
        ) {

            IconButton(
                onClick = {
                    flash_on.value = !flash_on.value
                    Log.d("camerview", "button clicked" + flash_on.value)
                },
                content = {
                    Icon(
                        painter =
                        if (flash_on.value) {
                            painterResource(id = R.drawable.ic_bolt_filled)

                        } else {
                            painterResource(id = R.drawable.ic_bolt)

                        },
                        contentDescription = "Take picture",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier
                            .size(36.dp),
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            IconButton(

                onClick = {
                    if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        lensFacing = CameraSelector.LENS_FACING_FRONT
                    } else {
                        lensFacing = CameraSelector.LENS_FACING_BACK
                    }
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flip_camera),
                        contentDescription = "Take picture",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier
                            .size(36.dp),
                    )
                }
            )
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter))
        {
            IconButton(

                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
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
                        painter =   painterResource(id = R.drawable.ic_panorama_fish_eye),
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

@OptIn(ExperimentalMaterialApi::class)
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
        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 48.dp, end = 24.dp)
                ){
            Card(modifier=Modifier,onClick = { /*TODO*/ }, border = BorderStroke(2.dp,
                androidx.compose.ui.graphics.Color.White), shape = RoundedCornerShape(12.dp), backgroundColor = androidx.compose.ui.graphics.Color.Transparent, elevation = 0.dp) {
                Row(modifier=Modifier.padding(vertical = 6.dp, horizontal = 12.dp),verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier=Modifier.size(36.dp),
                        tint = androidx.compose.ui.graphics.Color.White,
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription ="send picture" )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Set",color= androidx.compose.ui.graphics.Color.White,style= TextStyle(
                        fontFamily = Inter,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ))

                }
            }
        }
        Box(modifier = Modifier
            .align(Alignment.TopStart)
            .padding(horizontal = 16.dp, vertical = 48.dp)){
            IconButton(onClick = { /*TODO*/ }) {
                Icon(modifier=Modifier.size(36.dp),
                    tint = androidx.compose.ui.graphics.Color.White,
                    painter = painterResource(id = R.drawable.ic_x), contentDescription = "sd")
            }

        }
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