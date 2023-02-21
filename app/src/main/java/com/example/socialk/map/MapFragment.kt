package com.example.socialk.map

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.util.jar.Manifest

@AndroidEntryPoint
class MapFragment:Fragment() {
    private val viewModel by viewModels<MapViewModel>()
    private  var fusedLocationClient: FusedLocationProviderClient?=null
    private  var PERMISSION_REQUEST_CODE= 100


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                viewModel.setLocation(LatLng(location.latitude,location.longitude))

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
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if(navigateTo==Screen.Create){
                    viewModel.clicked_location.value.let {
                        val bundle = Bundle()

                        bundle.putString("location",it.toString())
                        Log.d("mapscreen","go to location"+it.toString())
                        navigate(navigateTo, Screen.Map,bundle)

                    }


                }else{
                    navigate(navigateTo, Screen.Map)

                }

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
        fusedLocationClient!!.requestLocationUpdates(LocationRequest(),
            locationCallback,
            Looper.getMainLooper())
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
                    MapScreen(onEvent = { event ->
                        when (event) {
                            is MapEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is MapEvent.LogOut -> viewModel.handleLogOut()
                            is MapEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is MapEvent.GoToHome -> viewModel.handleGoToHome()
                            is MapEvent.GoToCreateActivity -> {
                                Log.d("mapscreen","go to create"+event.latLng.toString())

                                viewModel.handleGoToCreate(event.latLng)
                            }
                            is MapEvent.AskForPermission -> {
                                Log.d("Mapfragment","ask")
                                requestPermissionLauncher.launch(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Create -> viewModel.handleGoToCreate(null)
                                is Profile ->viewModel.handleGoToProfile()
                            }
                        }
                    ,viewModel,locationCallback)
                }
            }
        }
    }

}