package com.example.socialk.di

import android.net.Uri
import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.User
import kotlinx.coroutines.flow.Flow


interface ActivityRepository {

    suspend fun getActivity(id:String) : Flow<Response<Activity>>
    suspend fun getUserActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun getMoreUserActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun addImageFromGalleryToStorage(id: String,uri: Uri): Flow<Response<String>>
    suspend fun likeActivity(id:String,user: User) : Flow<Response<Void?>>
    suspend fun addParticipantImageToActivity(activity_id:String,user_id:String,picture_url: String) : Flow<Response<Void?>>
    suspend fun setParticipantPicture(id:String,user: User) : Flow<Response<Void?>>
    suspend fun unlikeActivity(id:String,user:User) : Flow<Response<Void?>>

    suspend fun addActivity(activity:Activity) : Flow<Response<Void?>>
    suspend fun addUserToActivityInvites(activity: Activity,user_id:String) : Flow<Response<Void?>>
    suspend fun removeUserFromActivityInvites(activity: Activity,user_id:String) : Flow<Response<Void?>>
    suspend fun deleteActivity(id:String) : Flow<Response<Void?>>

    suspend fun getActivitiesForUser(id:String) : Flow<Response<List<Activity>>>
    suspend fun getMoreActivitiesForUser(id:String) : Flow<Response<List<Activity>>>

    suspend fun getActiveUsers(id:String) : Flow<Response<List<ActiveUser>>>

    suspend fun addActiveUser(activeUser: ActiveUser) : Response<Boolean>

    suspend fun deleteActiveUser(id:String) : Flow<Response<Void?>>

}