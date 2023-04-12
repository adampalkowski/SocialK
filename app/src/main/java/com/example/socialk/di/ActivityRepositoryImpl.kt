package com.example.socialk.di

import android.net.Uri
import android.util.Log
import com.example.socialk.ActiveUser
import com.example.socialk.await1
import com.example.socialk.model.*
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.model.Document
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.A
import java.lang.reflect.Field
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class ActivityRepositoryImpl @Inject constructor(
    private val activitiesRef: CollectionReference,
    private val activeUsersRef:CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val messagessRef: CollectionReference,
    private val resStorage: StorageReference,
    private val lowResStorage: StorageReference,
):ActivityRepository{
    private var lastVisibleData: DocumentSnapshot? = null
    private var lastVisibleUserData: DocumentSnapshot? = null
    private var lastVisibleDataForUserProfile: DocumentSnapshot? = null
    private var lastVisibleClosestData: DocumentSnapshot? = null

    private  var loaded_public_activities: ArrayList<Activity> = ArrayList()

    override suspend fun getClosestActivities(lat: Double,lng:Double): Flow<Response<List<Activity>>> =callbackFlow {
        lastVisibleClosestData=null
        val center = GeoLocation(lat,lng)
        val radiusInM = 50.0 * 1000.0
        Log.d("getMoreClosestActivites","settings null")
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q =  activitiesRef.whereEqualTo("public",true)
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result



                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d("ActivityRepositoryImpl",activity.toString())

                        if (activity!=null){
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleClosestData= matchingDocs[matchingDocs.size - 1]

                    trySend(Response.Success(newActivities))

                }
            }.addOnFailureListener(){
                trySend(Response.Failure(e= SocialException(message = "Nearby activity download failure",e=Exception())))

            }

        awaitClose {
        }
    }
    override suspend fun getMoreClosestActivities(lat: Double,lng:Double): Flow<Response<List<Activity>>> =callbackFlow {
        val center = GeoLocation(lat,lng)
        val radiusInM = 5.0 * 1000.0


        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q =  activitiesRef.whereEqualTo("public",true)
                .orderBy("geoHash")
                .startAfter(lastVisibleClosestData?.data?.get("geoHash"))
                .endAt(b.endHash)
                .limit(1)
            tasks.add(q.get())
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result


                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d("getMoreClosestActivities",activity.toString())

                        if (activity!=null){
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleClosestData= matchingDocs[matchingDocs.size - 1]
                    loaded_public_activities.addAll(newActivities)
                    val new_instance= ArrayList<Activity>()
                    Log.d("ActivityRepositoryImpl",loaded_public_activities.toString())
                    new_instance.addAll(loaded_public_activities)
                    trySend(Response.Success(new_instance))

                }
            }.addOnFailureListener(){
                trySend(Response.Failure(e= SocialException(message = "Nearby activity download failure",e=Exception())))

            }

        awaitClose {
        }
    }
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
    override suspend fun setParticipantPicture(id: String, user: User): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(id).update("participants_profile_pictures"+"."+user.id,FieldValue.delete(),"participants_usernames"+"."+user.id,FieldValue.delete()).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("unlikeActivity exception",Exception())))
        }
    }

    override suspend fun likeActivity(id: String, user: User): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(id).update("participants_profile_pictures"+"."+user.id,user.pictureUrl,"participants_usernames"+"."+user.id,user.username,
            "participants_ids",FieldValue.arrayUnion(id)).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("likeActivity exception",Exception())))
        }
    }

    override suspend fun addParticipantImageToActivity(activity_id:String,user_id:String,picture_url: String): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(activity_id).update("pictures"+"."+user_id,picture_url).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("likeActivity exception",Exception())))
        }
    }
    override suspend fun unlikeActivity(id: String, user: User): Flow<Response<Void?>> =flow{
        try{
            emit(Response.Loading)
            val update = activitiesRef.document(id).update("participants_profile_pictures"+"."+user.id,FieldValue.delete(),"participants_usernames"+"."+user.id,FieldValue.delete(),
            "participants_ids",FieldValue.arrayRemove(id)).await()
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
    override suspend fun updateActivityInvites(activity_id: String,invites:ArrayList<String>) :Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition = activitiesRef.document(activity_id).update("invited_users",invites).await()
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
    override suspend fun leaveLiveActivity(activity_id: String,user_id:String): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            Log.d("HOMESCREEN","LEAVEACTIVITY")
            Log.d("HOMESCREEN",activity_id+user_id)
            val addition = activeUsersRef.document(activity_id).update("participants_profile_pictures"+"."+user_id
                ,FieldValue.delete(),"participants_usernames"+"."+user_id,FieldValue.delete()).await()
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
            val collectionRef = messagessRef.document(id).collection("messages")
            val ref =collectionRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
            ref.await()
            val deletion1 = activitiesRef.document(id).delete().await()
            val deletion3 = chatCollectionsRef.document(id).delete().await()
            emit(Response.Success(deletion3))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteActivity exception",Exception())))
        }
    }
    override suspend fun joinActiveUser(live_activity_id:String,user_id: String,profile_url:String,username:String) :Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
            val update = activeUsersRef.document(live_activity_id).update("participants_profile_pictures"+"."+user_id,profile_url,"participants_usernames"+"."+user_id,username).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("joinActiveUser exception",Exception())))
        }
    }
    override suspend fun getUserActivities(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        Log.d("PROFILESCREEN","GET USER ACTIVITIES")
        val snapshotListener = activitiesRef.whereEqualTo("creator_id",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener { task->
                var activitiesList:List<Activity> = mutableListOf()
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newActivities = ArrayList<Activity>()
                        for (document in documents) {
                            val activity = document.toObject<Activity>()
                            Log.d("PROFILESCREEN",activity.toString())


                            if (activity!=null){
                                newActivities.add(activity)
                            }
                        }
                        lastVisibleUserData= documents[documents.size - 1]
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
    override suspend fun getJoinedActivities(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        Log.d("GETJOINEDACTIVITIES","CALL")
        val snapshotListener = activitiesRef.whereArrayContains("participants_ids",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener { task->
                var activitiesList:List<Activity> = mutableListOf()
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newActivities = ArrayList<Activity>()
                        for (document in documents) {
                            val activity = document.toObject<Activity>()
                            Log.d("GETJOINEDACTIVITIES",activity.toString())


                            if (activity!=null){
                                newActivities.add(activity)
                            }
                        }
                        lastVisibleUserData= documents[documents.size - 1]
                        trySend(Response.Success(newActivities))

                    }
                } else {
                    // There are no more messages to load
                    trySend(Response.Failure(e=SocialException(message="failed to get activities",e=Exception())))
                }

            }
        awaitClose {

        }

    }
    override suspend fun getMoreUserActivities(id: String): Flow<Response<List<Activity>>> =callbackFlow {
        val snapshotListener = activitiesRef.whereEqualTo("creator_id",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).startAfter(lastVisibleUserData?.data?.get("creation_time")).get().addOnCompleteListener { task->
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
                    lastVisibleUserData= documents[documents.size - 1]
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

    override suspend fun addImageFromGalleryToStorage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            if (imageUri != null) {
                val fileName = id
                try {
                    resStorage.child("high_res_images/$fileName" + "_1080x1920").delete().await1()
                }catch (e:StorageException){

                }
                val imageRef = resStorage.child("high_res_images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = resStorage.child("high_res_images/$fileName" + "_1080x1920")
                val url = keepTrying(8, reference)
                emit(Response.Success(url))
            }
        } catch (e: Exception) {
            Log.d("ActivityRepositoryImpl", "try addProfilePictureToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun deleteImageFromHighResStorage(
        id: String
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
                val fileName = id
                try {
                    val deletion=resStorage.child("high_res_images/$fileName" + "_1080x1920").delete().await1()
                }catch (e:StorageException){

                }
            emit(Response.Success("succesfully deleted from storage"))

        } catch (e: Exception) {
            Log.d("deleteImageFromHighResStorage", "deletion from storage exception")
            emit(
                Response.Failure(
                    e = SocialException(
                        "deleteImageFromHighResStorage exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun deleteActivityImageFromFirestoreActivity(
        activity_id: String,
        user_id: String
    ): Flow<Response<String>> =flow {

        try{
            emit(Response.Loading)
            val deletion = activitiesRef.document(activity_id).update("pictures"+"."+user_id,FieldValue.delete()).await()
            emit(Response.Success("deletion"))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteActivityImageFromFirestoreActivity",Exception())))
        }
    }

    suspend fun keepTrying(triesRemaining: Int, storageRef: StorageReference): String {
        if (triesRemaining < 0) {
            throw TimeoutException("out of tries")
        }

        return try {
            val url = storageRef.downloadUrl.await()
            url.toString()
        } catch (error: Exception) {
            when (error) {
                is StorageException -> {
                    if (error.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        delay(1000)
                        keepTrying(triesRemaining - 1, storageRef)
                    } else {
                        println(error)
                        throw error
                    }
                }
                else -> {
                    println(error)
                    throw error
                }
            }
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
        Log.d("getActivitiesForUser","getActivitiesForUser 2")
        val snapshotListener = activitiesRef.whereArrayContains("invited_users",id)
            .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener { task->
            var activitiesList:List<Activity> = mutableListOf()
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null && documents.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in documents) {
                        val activity = document.toObject<Activity>()
                        Log.d("getActivitiesForUser 2",activity.toString())

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

    override suspend fun addActiveUser(activeUser: ActiveUser): Response<Boolean>   {
        return try {
            val result = activeUsersRef.document(activeUser.creator_id).set(activeUser).await()
            com.example.socialk.model.Response.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            com.example.socialk.model.Response.Failure(
                com.example.socialk.model.SocialException(
                    "signIn error",
                    e
                )
            )
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