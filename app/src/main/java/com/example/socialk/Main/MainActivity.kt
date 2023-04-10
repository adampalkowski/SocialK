package com.example.socialk.Main

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.socialk.R
import com.example.socialk.home.HomeFragment
import com.example.socialk.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.jar.Manifest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val viewModel by viewModels<HomeViewModel>()
        navView.setupWithNavController(navController)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (   deepLink?.pathSegments?.get(0).equals("Activity")){
                    Log.d("HomeFragment", deepLink?.pathSegments?.get(1).toString())
                    viewModel.setActivityLink( deepLink?.pathSegments?.get(1).toString())
                }else if( deepLink?.pathSegments?.get(0).equals("User")){
                    Log.d("HomeFragment", deepLink?.pathSegments?.get(1).toString())
                    viewModel.setUserLink( deepLink?.pathSegments?.get(1).toString())
                }


            }
            .addOnFailureListener(this) { e -> Log.w(ContentValues.TAG, "getDynamicLink:onFailure", e) }
    }


}
