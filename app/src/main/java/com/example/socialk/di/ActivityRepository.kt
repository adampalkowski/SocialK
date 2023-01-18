package com.example.socialk.di

import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import kotlinx.coroutines.flow.Flow


interface ActivityRepository {

    suspend fun getActivity(id:String) : Flow<Response<Activity>>

    suspend fun addActivity(activity:Activity) : Flow<Response<Void?>>

    suspend fun deleteActivity(id:String) : Flow<Response<Void?>>

    suspend fun getActivitiesForUser(id:String) : Flow<Response<List<Activity>>>

}