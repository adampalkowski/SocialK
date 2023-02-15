package com.example.socialk.di

import android.net.Uri
import android.util.Log
import com.example.socialk.await1
import com.example.socialk.model.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val storageRef: StorageReference,
) : UserRepository {

    override suspend fun getUser(id: String): Flow<Response<User>> = callbackFlow {
        trySend(Response.Loading)
        val registration = usersRef.document(id).addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                channel.close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    trySend(Response.Success(user))
                }
            } else {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            "get user snaphot doesn't exist",
                            Exception()
                        )
                    )
                )

            }
        }

        awaitClose() {
            registration.remove()
        }

    }

    override suspend fun getUserListener(id: String): Flow<Response<User>> = callbackFlow {

        val registration = usersRef.document(id).addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                channel.close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user: User? = snapshot.toObject<User>()
                if (user != null) {
                    trySend(Response.Success(user))
                }


            } else {
            }

        }
        awaitClose() {
            registration.remove()
        }
    }

    override suspend fun getUserByUsername(username: String): Flow<Response<User>> = callbackFlow {
        val registration =
            usersRef.whereEqualTo("username", username).get().addOnSuccessListener { documents ->
                var userList: List<User> = mutableListOf()

                //list should always be the size of 1
                if (userList.size > 1) {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "more than one of the usernames exist",
                                Exception()
                            )
                        )
                    )
                }

                val response = if (documents != null) {

                    userList = documents.map { it.toObject<User>() }
                    if (userList.size == 1) {
                        trySend(Response.Success(userList[0]))
                    } else {
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    "user found and is not 1",
                                    Exception()
                                )
                            )
                        )
                    }
                } else {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "getUser by name document null",
                                Exception()
                            )
                        )
                    )
                }

            }.addOnFailureListener { exception ->
                channel.close(exception)
                trySend(
                    Response.Failure(
                        e = SocialException(
                            "get user by name document doesnt exist",
                            Exception()
                        )
                    )
                )
            }

        awaitClose() {
        }

    }

    override suspend fun addUser(user: User): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val userId = user.id
            val addition = usersRef.document(userId).set(user).await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addUser exception", Exception())))
        }
    }

    override suspend fun deleteUser(id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = usersRef.document(id).delete().await1()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("deleteUser exception", Exception())))
        }
    }

    override suspend fun addUsernameToUser(id: String, username: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)


                val addition = usersRef.document(id).update("username", username).await1()
                emit(Response.Success(addition))

            } catch (e: Exception) {
                emit(
                    Response.Failure(
                        e = SocialException(
                            "addUsernameToUser exception",
                            Exception()
                        )
                    )
                )
            }
        }

    override suspend fun changeUserProfilePicture(
        user_id: String,
        picture_url: String
    ): Flow<Response<Void?>> = flow {
        try {
            Log.d("ImagePicker", "changeUserProfilePicture called")
            emit(Response.Loading)
            val addition =
                usersRef.document(user_id).update("pictureUrl", picture_url)
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "changeUserProfilePicture exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addProfilePictureToStorage(
        user_id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            Log.d("ImagePicker", " storeage called")
            emit(Response.Loading)
            Log.d("ImagePicker", "123 called")

            if (imageUri != null) {
                Log.d("c", "try called")
                val fileName = user_id
                Log.d("ImagePicker", "123 called")
                val imageRef = storageRef.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val url = storageRef.child("images/$fileName" + "_200x200").downloadUrl.await1()
                emit(Response.Success(url.toString()))
            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addProfilePictureToStorage EXCEPTION")
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

    override suspend fun deleteProfilePictureFromStorage(
        user_id: String,
        picture_url: String
    ): Flow<Response<Void?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProfilePictureFromStorage(
        user_id: String,
        picture_url: String
    ): Flow<Response<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriends(id: String): Flow<Response<ArrayList<User>>> = callbackFlow {
        val registration =
            usersRef.whereArrayContains( "friends_ids_list",id).limit(9).get().addOnSuccessListener { documents ->
                var userList: ArrayList<User> = ArrayList()

                val response = if (documents != null) {
                    userList = documents.map { it.toObject<User>() } as ArrayList<User>
                    Log.d("UserRepositoyImpl","User list"+userList.toString())
                    trySend(Response.Success(userList))
                } else {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "getUser by name document null",
                                Exception()
                            )
                        )
                    )
                }

            }.addOnFailureListener { exception ->
                channel.close(exception)
                trySend(
                    Response.Failure(
                        e = SocialException(
                            "get user by name document doesnt exist",
                            Exception()
                        )
                    )
                )
            }

        awaitClose() {

        }

    }

    override suspend fun addInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition =
                usersRef.document(my_id).update("invited_ids", FieldValue.arrayUnion(invited_id))
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addInvitedIDs exception", Exception())))
        }
    }


    override suspend fun acceptInvite(
        current_user: User,
        user: User,
        chat: Chat
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val list_for_user_two = ArrayList<String>()
            val list_for_user_one = ArrayList<String>()
            list_for_user_two.add(user.id)
            list_for_user_one.add(current_user.id)
            val one = chatCollectionsRef.document(chat.id!!).set(chat).await1()
            val two = usersRef.document(user.id).update(
                "friends_ids" + "." + current_user.id,
                chat.id,"friends_ids_list" ,list_for_user_one,
                "invited_ids",
                FieldValue.arrayRemove(current_user.id)
            ).await1()

            val three =
                usersRef.document(current_user.id).update("friends_ids" + "." + user.id,  chat.id,"friends_ids_list", list_for_user_two)
                    .await1()
            emit(Response.Success(three))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addInvitedIDs exception", Exception())))
        }
    }

    override suspend fun removeInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion =
                usersRef.document(my_id).update("invited_ids", FieldValue.arrayRemove(invited_id))
                    .await1()
            emit(Response.Success(deletion))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("removeInvitedIDs exception", Exception())))
        }
    }

    override suspend fun addBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition =
                usersRef.document(my_id).update("blocked_ids", FieldValue.arrayUnion(blocked_id))
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addBlockedIDs exception", Exception())))
        }
    }

    override suspend fun removeBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion =
                usersRef.document(my_id).update("blocked_ids", FieldValue.arrayRemove(blocked_id))
                    .await1()
            emit(Response.Success(deletion))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("removeBlockedIDs exception", Exception())))
        }
    }


    override suspend fun addFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val addition =
                    usersRef.document(my_id).update("friends_ids" + "." + friend_id, "").await1()
                emit(Response.Success(addition))

            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("addFriendsIDs exception", Exception())))
            }
        }

    override suspend fun removeFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val deletion = usersRef.document(my_id)
                    .update("friends_ids" + "." + friend_id, FieldValue.delete()).await1()
                emit(Response.Success(deletion))

            } catch (e: Exception) {
                emit(
                    Response.Failure(
                        e = SocialException(
                            "removeFriendsIDs exception",
                            Exception()
                        )
                    )
                )
            }
        }

    override suspend fun addFriendToBothUsers(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one = usersRef.document(my_id).update("friends_ids" + "." + friend_id, "").await1()
            val two = usersRef.document(friend_id).update("friends_ids" + "." + my_id, "").await1()

            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "addFriendToBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }


    override suspend fun removeFriendFromBothUsers(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one = usersRef.document(my_id)
                .update("friends_ids" + "." + friend_id, FieldValue.delete()).await1()
            val two = usersRef.document(friend_id)
                .update("friends_ids" + "." + my_id, FieldValue.delete()).await1()
            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeFriendFromBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addChatCollectionToUsers(
        id: String,
        friend_id: String,
        chat_id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one =
                usersRef.document(id).update("friends_ids" + "." + friend_id, chat_id).await1()
            val two =
                usersRef.document(friend_id).update("friends_ids" + "." + id, chat_id).await1()
            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeFriendFromBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }

    //todo paginate the daataaaaa
    override suspend fun getInvites(id: String): Flow<Response<ArrayList<User>>> = callbackFlow {
        val registration = usersRef.whereArrayContains("invited_ids", id).limit(5)
            .addSnapshotListener() { snapshots, exception ->

                if (exception != null) {
                    channel.close(exception)
                    return@addSnapshotListener
                }
                var invites_list = ArrayList<User>()
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val user = dc.document.toObject(User::class.java)
                            invites_list.add(user)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val user = dc.document.toObject(User::class.java)
                            invites_list.add(user)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val user = dc.document.toObject(User::class.java)
                            invites_list.remove(user)
                        }
                    }

                }
                trySend(Response.Success(invites_list))
            }

        awaitClose() {
            registration.remove()
        }

    }

    override suspend fun checkIfChatCollectionExists(
        id: String,
        chatter_id: String
    ): Flow<Response<User>> = callbackFlow {
        usersRef.document(id).get().addOnSuccessListener { documentSnapshot ->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<User>()
                activity?.friends_ids?.forEach { (key, value) ->

                    if (key == chatter_id) {

                    }

                }


                Response.Success(activity)
            } else {
                Response.Failure(e = SocialException("getMessage document null", Exception()))
            }
            trySend(response as Response<User>).isSuccess
        }

    }

}