package com.example.socialk.create

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.chat.ChatEvent
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
 fun calculateDestroyTime(currentTime:String,otherTime:String):String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val (currentHours, currentMinutes) = currentTime.split(":").map { it.toInt() }
    val (otherHours, otherMinutes) = otherTime.split(":").map { it.toInt() }

    var totalMinutes = currentMinutes + otherMinutes + (currentHours + otherHours) * 60
    if (totalMinutes >= 1440) {
        totalMinutes -= 1440

        val current = LocalDate.now().plusDays(1).format(formatter)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        val result = String.format("%02d:%02d", hours, minutes)
        return "$current $result"
    }else{
        val current = LocalDate.now().format(formatter)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        val result = String.format("%02d:%02d", hours, minutes)
        return "$current $result"
    }


}
@AndroidEntryPoint
class LiveFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activeUsersViewModel by viewModels<ActiveUsersViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private  var fusedLocationClient: FusedLocationProviderClient?=null
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                activeUsersViewModel.setLocation(LatLng(location.latitude,location.longitude))

            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activeUsersViewModel.activeUserAdded()
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Live)
            }
        }


        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
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
            Looper.getMainLooper())
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.\
                    activeUsersViewModel.permissionGranted()
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
                activeUsersViewModel.permissionGranted()
            }
            shouldShowRequestPermissionRationale(   android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
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
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    LiveScreen(  activeUsersViewModel=activeUsersViewModel,onEvent = { event ->
                        when (event) {
                            is LiveEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is LiveEvent.GoToHome -> viewModel.handleGoToHome()
                            is LiveEvent.LogOut -> viewModel.handleLogOut()
                            is LiveEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is LiveEvent.GoToLive -> viewModel.handleGoToLive()
                            is LiveEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is LiveEvent.GoToActivity -> viewModel.handleGoToActivity()
                            is LiveEvent.AskForPermission -> {
                                requestPermissionLauncher.launch(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            is LiveEvent.ClearState -> activeUsersViewModel.activeUserAdded()
                            is LiveEvent.CreateActiveUser -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id:String = uuid.toString()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)

                                //todo what if current user is null
                                val participants_profile_pictures: java.util.HashMap<String, String> = hashMapOf()
                                val participants_usernames: java.util.HashMap<String, String> = hashMapOf()
                                val destroyTime:String= calculateDestroyTime( event.start_time,event.time_length)
                                participants_profile_pictures[authViewModel.currentUser!!.uid]=UserData.user!!.pictureUrl!!
                                participants_usernames[authViewModel.currentUser!!.uid]=UserData.user!!.username!!
                                activeUsersViewModel.addActiveUser(
                                    ActiveUser(id=id,
                                        creator_id = if (authViewModel.currentUser==null){""}else{ authViewModel.currentUser!!.uid.toString()},
                                        participants_profile_pictures = participants_profile_pictures,
                                        participants_usernames =  participants_usernames,
                                        latLng = event.latLng,
                                        time_end = "",
                                        time_length = event.time_length,
                                        time_start = event.start_time,
                                        create_time = current,
                                        invited_users = ArrayList<String>(UserData.user!!.friends_ids.keys),
                                        destroy_time=destroyTime,
                                ))
                            }
                            else->{}

                        }
                    },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile ->viewModel.handleGoToProfile()
                                else->{}

                            }
                        })
                }
            }
        }
    }
}
