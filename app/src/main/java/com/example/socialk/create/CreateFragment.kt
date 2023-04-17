package com.example.socialk.create

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()


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
    override fun onStart() {
        super.onStart()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        val sharedPref = requireContext().getSharedPreferences("create_data",Context.MODE_PRIVATE)
        // Get a reference to the SharedPreferences editor
        val editor = sharedPref.edit()
        // Put the data you want to save in the editor
        Log.d("CREATEFRAGMENT",viewModel.name.value.toString())
        editor.putString("name",viewModel.name.value.toString())
        editor.putString("description", viewModel.description.value.toString())
        editor.putString("date", viewModel.date.value.toString())
        editor.putString("start_time", viewModel.start_time.value.toString())
        editor.putString("duration",  viewModel.duration.value.toString())
        editor.putString("custom_location", viewModel.custom_location.value.toString())
        editor.putString("max",  viewModel.max.value.toString())
        editor.putString("min",  viewModel.min.value.toString())
        editor.putString("latlng",  viewModel.latlng.value.toString())
        // Save the changes
        editor.apply()
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
        activityViewModel.activityAdded()
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo==Screen.FriendsPicker){
                    val bundle=Bundle()
                    bundle.putSerializable("activity",viewModel.created_activity.value)
                    bundle.putString("group_picture",viewModel.photo_uri.value.toString())
                    navigate(navigateTo, Screen.Create,bundle)

                }else{
                    navigate(navigateTo, Screen.Create)

                }
            }

        }
        requestCameraPermission()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        val sharedPreferences = requireContext().getSharedPreferences("create_data", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")

        if (name!!.isNotEmpty()){
            viewModel.name.value=name}
        val description = sharedPreferences.getString("description", "")
        if (description!!.isNotEmpty()){viewModel.description.value=description}
        val date = sharedPreferences.getString("date", "")
        if (date!!.isNotEmpty()){viewModel.date.value=date}
        val start_time = sharedPreferences.getString("start_time", "")
        if (start_time!!.isNotEmpty()){viewModel.start_time.value=start_time}
        val duration = sharedPreferences.getString("duration", "")
        if (duration!!.isNotEmpty()){viewModel.duration.value=duration}
        val custom_location = sharedPreferences.getString("custom_location", "")
        if (custom_location!!.isNotEmpty()){viewModel.custom_location.value=custom_location}
        val max = sharedPreferences.getString("max", "")
        if (max!!.isNotEmpty()){viewModel.max.value=max}
        val min = sharedPreferences.getString("min", "")
        if (min!!.isNotEmpty()){viewModel.min.value=min}
        val latlng = sharedPreferences.getString("latlng", "")
        if (latlng!!.isNotEmpty()){viewModel.latlng.value=latlng}

        var location:String?=arguments?.getString("location")
        if(location!=null && location.isNotEmpty()){
            viewModel.latlng.value=location
        }
        userViewModel.getFriends(authViewModel.currentUser!!.uid)

        Log.d("mapscreen",location.toString())



        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateScreen(viewModel,location,userViewModel,activityViewModel, onEvent = { event ->
                        when (event) {
                            is CreateEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is CreateEvent.GoToHome -> {

                                viewModel.handleGoToHome()
                            }
                            is CreateEvent.LogOut -> viewModel.handleLogOut()
                            is CreateEvent.GoBack ->activity?.onBackPressedDispatcher?.onBackPressed()
                            is CreateEvent.ClearState -> activityViewModel.activityAdded()
                            is CreateEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is CreateEvent.OpenCamera->{      viewModel.shouldShowCamera.value = true
                            }

                            is CreateEvent.DisplayPicture->{
                                viewModel.photo_uri.value=event.photo_url.toUri()
                                viewModel.shouldShowCamera.value=true
                                viewModel.shouldShowPhoto.value=true
                                viewModel.displayPhoto.value=true
                            }
                            is CreateEvent.OpenGallery -> {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            is CreateEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is CreateEvent.GoToLive -> viewModel.handleGoToLive()
                            is CreateEvent.GoToActivity -> viewModel.handleGoToActivity()
                            is CreateEvent.GoToMap -> viewModel.handleGoToMap()
                            is CreateEvent.CreateActivity -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id: String = uuid.toString()
                                val participants_profile_pictures: HashMap<String,String> = hashMapOf()
                                val participants_usernames: HashMap<String,String> = hashMapOf()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)

                                participants_profile_pictures[authViewModel.currentUser!!.uid]=UserData.user!!.pictureUrl!!
                                participants_usernames[authViewModel.currentUser!!.uid]=UserData.user!!.username!!
                                //get LAT LNG FROM STRING
                                val values =
                                    event.location.split("/")
                                val lat= values.get(0).toDouble()
                                val lng= values.get(1).toDouble()
                                //Create geohash
                                val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))
                                val geohash: MutableMap<String, Any> = mutableMapOf(
                                    "geohash" to hash,
                                    "lat" to lat,
                                    "lng" to lng
                                )

                                //handle privacy settings IF PRIVATE
                                if (event.selectedPrivacy.equals("Private")){

                                    val activity=Activity(
                                        id = id,
                                        title = event.title,
                                        date = event.date,
                                        start_time = event.start_time,
                                        time_length = event.time_length,
                                        creator_id = if (authViewModel.currentUser == null) {
                                            ""
                                        } else {
                                            authViewModel.currentUser!!.uid.toString()
                                        },
                                        description= event.description,
                                        creator_username = UserData.user!!.username!! ,
                                        creator_name =UserData.user!!.name!! ,
                                        creator_profile_picture = UserData.user!!.pictureUrl!! ,
                                        time_left = "",
                                        end_time = "",
                                        geoHash=hash,
                                        lat=lat,
                                        lng=lng,
                                        custom_location = event.custom_location,
                                        minUserCount=if(event.min.equals("")){0}else{event.min.toInt()},
                                        maxUserCount=if(event.max.equals("")){100}else{event.max.toInt()},
                                        disableChat = event.disableChat,
                                        likes = 0,
                                        invited_users = arrayListOf(UserData.user!!.id),
                                        participants_profile_pictures =participants_profile_pictures ,
                                        participants_usernames =participants_usernames,
                                        creation_time = current,
                                        location=event.location,
                                        pictures=HashMap(),
                                        enableActivitySharing=event.enableActivitySharing,
                                        disablePictures=event.disablePictures,
                                        disableNotification=event.disableNotification,
                                        privateChat=event.privateChat,
                                        public =if(event.selectedPrivacy.equals("Public")) true else false,
                                        participants_ids = arrayListOf(UserData.user!!.id),
                                        awaitConfirmation=event.awaitConfirmation,
                                        requests = ArrayList(),
                                        reports = 0,
                                        tags = event.tags,

                                    )
                                    activityViewModel.addActivity(activity)
                                    clearSharedPrefs()
                                    viewModel.handleGoToMap()
                                }else{

                                    //PRIVACY EITHER PUBLIC OR FRIENDS
                                    val activity=Activity(
                                        id = id,
                                        title = event.title,
                                        date = event.date,
                                        start_time = event.start_time,
                                        time_length = event.time_length,
                                        creator_id = if (authViewModel.currentUser == null) {
                                            ""
                                        } else {
                                            authViewModel.currentUser!!.uid.toString()
                                        },
                                        description= event.description,
                                        creator_username = UserData.user!!.username!! ,
                                        creator_name =UserData.user!!.name!! ,
                                        creator_profile_picture = UserData.user!!.pictureUrl!! ,
                                        time_left = "",
                                        end_time = "",
                                        geoHash=hash,
                                        lat=lat,
                                        lng=lng,
                                        custom_location = event.custom_location,
                                        minUserCount=if(event.min.equals("")){0}else{event.min.toInt()},
                                        maxUserCount=if(event.max.equals("")){100}else{event.max.toInt()},
                                        disableChat = event.disableChat,
                                        likes = 0,
                                        invited_users = arrayListOf(),
                                        participants_profile_pictures =participants_profile_pictures ,
                                        participants_usernames =participants_usernames,
                                        creation_time = current,
                                        location=event.location,
                                        pictures=HashMap(),
                                        enableActivitySharing=event.enableActivitySharing,
                                        disablePictures=event.disablePictures,
                                        disableNotification=event.disableNotification,
                                        privateChat=event.privateChat,
                                        public =if(event.selectedPrivacy.equals("Public")) true else false,
                                        participants_ids = arrayListOf(UserData.user!!.id),
                                        awaitConfirmation=event.awaitConfirmation,
                                          requests = ArrayList(),
                                        reports = 0,
                                        tags = event.tags,


                                        )

                                    viewModel.handleGoToFriendsPicker(activity)
                                    clearSharedPrefs()
                                }



                            }
                            else->{}
                        }
                    },
                        bottomNavEvent = { screen ->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile -> viewModel.handleGoToProfile()
                                else->{}
                            }
                        },outputDirectory=outputDirectory, executor = cameraExecutor, onImageCaptured = ::handleImageCapture)
                }
            }
        }
    }

    private fun clearSharedPrefs() {
        val sharedPref = requireContext().getSharedPreferences("create_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

}
