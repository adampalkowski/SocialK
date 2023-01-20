package com.example.socialk.di

import com.example.socialk.model.Response
import com.example.socialk.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(id:String):Flow<Response<User>>
    suspend fun getUserByUsername(username:String):Flow<Response<User>>
    suspend fun addUser(user: User): Flow<Response<Void?>>
    suspend fun deleteUser(id:String): Flow<Response<Void?>>
    suspend fun addUsernameToUser(id:String,username:String): Flow<Response<Void?>>
}