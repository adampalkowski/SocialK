package com.example.socialk.map

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.home.HomeViewModel
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {
    private val viewModel by viewModels<MapViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val activityViewModel by activityViewModels<ActivityViewModel>()
    private val activeUsersViewModel by viewModels<ActiveUsersViewModel>()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var PERMISSION_REQUEST_CODE = 100


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                Log.d("Mapfragment","location callback")
                val location = locationList.last()
                viewModel.setLocation(LatLng(location.latitude, location.longitude))
                activityViewModel.setLocation(LatLng(location.latitude, location.longitude))
                activityViewModel.getClosestActivities(location.latitude,location.longitude,  50.0*1000.0)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)

    }

    override fun onStart() {
        super.onStart()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo == Screen.Create) {
                    viewModel.clicked_location.value.let {
                        val bundle = Bundle()
                        bundle.putString("location", it.toString())
                        Log.d("mapscreen", "go to location" + it.toString())
                        if (it != null) {
                            if (it.toString().isNotEmpty()) {
                                navigate(navigateTo, Screen.Map, bundle)
                            } else {
                                navigate(navigateTo, Screen.Map)
                            }

                        } else {
                            navigate(navigateTo, Screen.Map)

                        }

                    }
                } else if (navigateTo == Screen.Chat) {
                    val bundle = Bundle()
                    bundle.putSerializable("activity", viewModel.clicked_chat_activity.value)
                    navigate(navigateTo, Screen.Map, bundle)
                } else if (navigateTo == Screen.FriendsPicker) {
                    val bundle = Bundle()
                    bundle.putSerializable("activity", viewModel.clicked_chat_activity.value)
                    navigate(navigateTo, Screen.Map, bundle)
                } else if (navigateTo == Screen.UserProfile) {
                    val bundle = Bundle()
                    bundle.putSerializable("user_id", viewModel.clicked_profile.value)
                    navigate(navigateTo, Screen.Map, bundle)
                }
                else{
                    navigate(navigateTo, Screen.Map)
                }
            }
        }
        homeViewModel.activity_link.value.let {
            if (it != null) {
                activityViewModel.getActivity(it)
                homeViewModel.resetLink()
            }
        }
        homeViewModel.user_link.value.let {
            if (it != null) {
                homeViewModel.resetUserLink()
                viewModel.handleGoToUserProfile(it)

            }
        }



        activityViewModel.getActivitiesForUser(authViewModel?.currentUser?.uid)

        activeUsersViewModel.getActiveUsersForUser(authViewModel?.currentUser?.uid)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
        if (ActivityCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }
        fusedLocationClient!!.requestLocationUpdates(
            LocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.\
                    viewModel.permissionGranted()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }

            }
        when {
            ContextCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.permissionGranted()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
        var latlngInitial: String? = arguments?.getString("latlng")
        var latLng: LatLng? = null
        if (latlngInitial != null) {
            val values = latlngInitial?.split("/")
            latLng = LatLng(values?.get(0)?.toDouble()!!, values?.get(1)?.toDouble()!!)
        }
        Log.d("Mapfragment","launch")
        return ComposeView(requireContext()).apply {
            setContent {
                val systemUiController = rememberSystemUiController()

                SocialTheme {
                    MapScreen(homeViewModel,systemUiController, latLng, activityViewModel, onEvent = { event ->
                        when (event) {
                            is MapEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is MapEvent.ReportActivity -> activityViewModel.reportActivity(event.activity_id)
                            is MapEvent.LeaveActivity -> activityViewModel.unlikeActivity(event.activity_id,event.user_id)
                            is MapEvent.HideActivity-> activityViewModel.hideActivity(event.activity_id,event.user_id)
                            is MapEvent.LogOut -> viewModel.handleLogOut()
                            is MapEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                            is MapEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is MapEvent.GoToHome -> viewModel.handleGoToHome()
                            is MapEvent.GoToChats -> viewModel.handleGoToChats()
                            is MapEvent.AddPeople -> viewModel.handleGoToSearch()
                            is MapEvent.GoToGroup -> viewModel.handleGoToGroup()
                            is MapEvent.GoToUserProfile ->    viewModel.handleGoToUserProfile(event.user.id)
                            is MapEvent.GoToCreated -> viewModel.handleGoToCreated()
                            is MapEvent.GoToBookmarked -> viewModel.handleGoToBookmarked()
                            is MapEvent.GoToCalendar-> viewModel.handleGoToCalendar()
                            is MapEvent.GoToTrending -> viewModel.handleGoToTrending()
                            is MapEvent.GoToHelp -> viewModel.handleGoToHelp()
                            is MapEvent.GoToInfo -> viewModel.handleGoToInfo()
                            is MapEvent.SendRequest -> {
                                Log.d("Mapfragment","send request")
                                activityViewModel.addRequestToActivity(event.activity.id,UserData.user!!.id)
                                userViewModel.addRequestToUser(event.activity.id,UserData.user!!.id)
                            }


                            is MapEvent.BackPressed -> {}
                            is MapEvent.GoToCreateActivity -> {
                                viewModel.handleGoToCreate(event.latLng)
                            }
                            is MapEvent.AskForPermission -> {
                                requestPermissionLauncher.launch(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            }
                            is MapEvent.GoToChat -> {
                                Log.d("HOMESCREEN", "here")
                                Log.d("HOMESCREEN", event.activity.toString())
                                viewModel.handleGoToChat(event.activity)
                            }
                            is MapEvent.ActivityLiked -> {

                                if (event.activity.participants_profile_pictures.values.size<2){
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                }else{
                                    activityViewModel.addActivityParticipant(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                }
                                userViewModel.addActivityToUser(  event.activity.id,
                                    UserData.user!!)

                            }
                            is MapEvent.ActivityUnLiked -> {
                                activityViewModel.unlikeActivity(
                                    event.activity.id,
                                    UserData.user!!.id
                                )
                            }
                            is MapEvent.LeaveLiveActivity -> {
                                Log.d("HOMESCREEN", "here")

                                activeUsersViewModel.leaveLiveActivity(
                                    event.activity_id,
                                    event.user_id
                                )
                            }
                            is MapEvent.DestroyLiveActivity -> {
                                activeUsersViewModel.deleteActiveUser(event.id)
                            }
                            is MapEvent.GoToFriendsPicker -> {
                                Log.d("HOmesCreen", "FRIENDSPICKER")
                                viewModel.handleGoToFriendsPicker(event.activity)
                            }
                            else->{}
                        }
                    },
                        bottomNavEvent = { screen ->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Create -> viewModel.handleGoToCreate(null)
                                is Profile -> viewModel.handleGoToProfile()
                            }
                        }, viewModel, locationCallback, activeUsersViewModel = activeUsersViewModel,
                        chatViewModel = chatViewModel,
                        authViewModel = authViewModel,userViewModel=userViewModel)
                }
            }
        }
    }

}
