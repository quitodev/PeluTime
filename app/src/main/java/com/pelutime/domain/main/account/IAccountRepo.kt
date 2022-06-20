package com.pelutime.domain.main.account

import android.net.Uri
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State

interface IAccountRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>
    suspend fun setUserRoom(userEntity: UserEntity)
    suspend fun deleteUserRoom(userEntity: UserEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE AUTH
    suspend fun updateUserEmail(email: String) : State<String>
    suspend fun updateUserPass(pass: String) : State<String>
    suspend fun deleteUserAuth() : State<String>
    suspend fun signIn(email: String, pass: String) : State<String>
    suspend fun signOut()

    // FIREBASE DATABASE
    suspend fun updateImageUser(id: String, image: String) : State<String>
    suspend fun updateUser(id: String, name: String, email: String) : State<String>
    suspend fun deleteUser(id: String) : State<String>

    // FIREBASE STORAGE
    suspend fun getImages(id: String) : State<String>
    suspend fun uploadImages(id: String, image: Uri) : State<String>
    suspend fun deleteImages(id: String) : State<String>
}