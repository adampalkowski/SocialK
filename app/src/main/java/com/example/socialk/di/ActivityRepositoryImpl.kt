package com.example.socialk.di

import android.util.Log
import com.example.socialk.ActiveUser
import com.example.socialk.model.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.model.Document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.A
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class ActivityRepositoryImpl @Inject constructor(
    private val activitiesRef: CollectionReference,
    private val activeUsersRef:CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val messagessRef: CollectionReference,
):ActivityRepository{
    private var lastVisibleData: DocumentSnapshot? = null
    private var lastVisibleDataForUserProfile: DocumentSnapshot? = null

    override suspend fun getActivity(id:String): Flow<Response<Activity>> = callbackFlow {
        activitiesRef.document(id).get().addOnSuccessListener {  documentSnapshot ->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<Activity>()
                Log.d("ActivityRepositoryImpl",activity.toString())
                Response.Success(activity)
            } else {
                Response.Failure(e= SocialException("getActivty document null",Exception()))
            }
            trySend(response as Response<Activity>).isSuccess
        }
        awaitClose(){
        }
    }

    override suspend fun likeActivity(id: String, user: User): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(id).update("participants_profile_pictures"+"."+user.id,user.pictureUrl,"participants_usernames"+"."+user.id,user.username).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("likeActivity exception",Exception())))
        }
    }

    override suspend fun unlikeActivity(id: String, user: User): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(id).update("participants_profile_pictures"+"."+user.id,FieldValue.delete(),"participants_usernames"+"."+user.id,FieldValue.delete()).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("unlikeActivity exception",Exception())))
        }
    }

    override suspend fun addActivity(activity: Activity) :Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val activityId=activity.id
            val addition = activitiesRef.document(activityId).set(activity).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("AddActivity exception",Exception())))
        }
    }

    override suspend fun addUserToActivityInvites(activity: Activity,user_id:String): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            val activityId=activity.id
            val addition = activitiesRef.document(activityId).update("invited_users",FieldValue.arrayUnion(user_id)).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addUserToActivityInvites exception",Exception())))
        }
    }

    override suspend fun removeUserFromActivityInvites(activity: Activity,user_id:String): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            val activityId=activity.id
            val addition = activitiesRef.document(activityId).update("invited_users",FieldValue.arrayRemove(user_id)).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeUserFromActivityInvites exception",Exception())))
        }
    }

    override suspend fun deleteActivity(id: String) :Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
           // val deletion1 = activitiesRef.document(id).delete().await()
            //todo delete messages that are in the collections
            //val deletion2 = messagessRef.document(id).collection("messages").dele().await()
            val deletion3 = chatCollectionsRef.document(id).delete().await()
            emit(Response.Success(deletion3))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteActivity exception",Exception())))
        }
    }
    override suspend fun getUserActivities(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        val snapshotListener = activitiesRef.whereArrayContains("creator_id",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener { task->
                var activitiesList:List<Activity> = mutableListOf()
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newActivities = ArrayList<Activity>()
                        for (document in documents) {
                            val activity = document.toObject<Activity>()
                            Log.d("ActivityRepositoryImpl",activity.toString())

                            if (activity!=null){
                                newActivities.add(activity)
                            }
                        }
                        lastVisibleDataForUserProfile= documents[documents.size - 1]
                        trySend(Response.Success(newActivities))

                    }
                } else {
                    // There are no more messages to load
                    trySend(Response.Failure(e=SocialException(message="failed to get more activities",e=Exception())))
                }

            }
        awaitClose {
        }

    }
    override suspend fun getMoreUserActivities(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        val snapshotListener = activitiesRef.whereArrayContains("creator_id",id)  .orderBy("creation_time", Query.Direction.DESCENDING).startAfter(lastVisibleDataForUserProfile?.data?.get("creation_time")).get().addOnCompleteListener { task->
            var activitiesList:List<Activity> = mutableListOf()
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null && documents.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in documents) {
                        val activity = document.toObject<Activity>()
                        if (activity!=null){
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleDataForUserProfile= documents[documents.size - 1]
                    trySend(Response.Success(newActivities))

                }
            } else {
                // There are no more messages to load
                trySend(Response.Failure(e=SocialException(message="failed to get more activities",e=Exception())))
            }

        }
        awaitClose {
        }


    }
    override suspend fun getMoreActivitiesForUser(id: String): Flow<Response<List<Activity>>> =callbackFlow {

        val snapshotListener = activitiesRef.whereArrayContains("invited_users",id)  .orderBy("creation_time", Query.Direction.DESCENDING).startAfter(lastVisibleData?.data?.get("creation_time")).get().addOnCompleteListener { task->
            var activitiesList:List<Activity> = mutableListOf()
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null && documents.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in documents) {
                        val activity = document.toObject<Activity>()
                        if (activity!=null){
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleData= documents[documents.size - 1]
                    trySend(Response.Success(newActivities))

                }
            } else {
                // There are no more messages to load
                trySend(Response.Failure(e=SocialException(message="failed to get more activities",e=Exception())))
            }

        }
        awaitClose {
        }
    }
    override suspend fun getActivitiesForUser(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        val snapshotListener = activitiesRef.whereArrayContains("invited_users",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener { task->
            var activitiesList:List<Activity> = mutableListOf()
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null && documents.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in documents) {
                        val activity = document.toObject<Activity>()
                        Log.d("ActivityRepositoryImpl",activity.toString())

                        if (activity!=null){
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleData= documents[documents.size - 1]
                    trySend(Response.Success(newActivities))

                }
            } else {
                // There are no more messages to load
                trySend(Response.Failure(e=SocialException(message="failed to get more activities",e=Exception())))
            }

        }
        awaitClose {
        }
    }
        //TODO CHANGE WHERE_EQUAL_TO
    override suspend fun getActiveUsers(id: String): Flow<Response<List<ActiveUser>>> = callbackFlow {
        val snapshotListener = activeUsersRef.whereArrayContains("invited_users",id).get().addOnSuccessListener { documents->
            var activitiesList:List<ActiveUser> = mutableListOf()

            val response = if (documents != null) {
                activitiesList =documents.map { it.toObject<ActiveUser>() }
                Response.Success(activitiesList)
            } else {
                Response.Failure(e= SocialException("getActiveUsers",Exception()))
            }
            trySend(response).isSuccess
        }
        awaitClose {
        }
    }

    override suspend fun addActiveUser(activeUser: ActiveUser): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val activityId=activeUser.id
            val addition = activeUsersRef.document(activeUser.creator_id).set(activeUser).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addActiveUser",Exception())))
        }
    }

    override suspend fun deleteActiveUser(id: String): Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
            val deletion = activeUsersRef.document(id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteActiveUser",Exception())))
        }
    }


}