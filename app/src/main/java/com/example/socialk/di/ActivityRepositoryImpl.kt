package com.example.socialk.di

import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.SocialException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private val activeUsersRef:CollectionReference
):ActivityRepository{

    override suspend fun getActivity(id:String): Flow<Response<Activity>> = callbackFlow {

       activitiesRef.document(activitiesRef.document().id).get().addOnSuccessListener {  documentSnapshot ->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<Activity>()
                Response.Success(activity)
            } else {
                Response.Failure(e= SocialException("getActivty document null",Exception()))
            }
            trySend(response as Response<Activity>).isSuccess
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

    override suspend fun deleteActivity(id: String) :Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
            val deletion = activitiesRef.document(id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteActivity exception",Exception())))
        }
    }

    override suspend fun getActivitiesForUser(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        val snapshotListener = activitiesRef.whereEqualTo("creator_id",id).get().addOnSuccessListener { documents->
            var activitiesList:List<Activity> = mutableListOf()


            val response = if (documents != null) {
                activitiesList =documents.map { it.toObject<Activity>() }
                val ref=activitiesRef.toString()
                val id= id
                Response.Success(activitiesList)
            } else {
                Response.Failure(e= SocialException("",Exception()))
            }
            trySend(response).isSuccess
        }
        awaitClose {
        }
    }
        //TODO CHANGE WHERE_EQUAL_TO
    override suspend fun getActiveUsers(id: String): Flow<Response<List<ActiveUser>>> = callbackFlow {
        val snapshotListener = activeUsersRef.whereEqualTo("creator_id",id).get().addOnSuccessListener { documents->
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
            val addition = activeUsersRef.document(activityId).set(activeUser).await()
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