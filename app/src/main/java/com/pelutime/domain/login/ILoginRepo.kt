package com.pelutime.domain.login

import com.pelutime.data.model.login.Login
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State

interface ILoginRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>
    suspend fun setUserRoom(userEntity: UserEntity)
    suspend fun deleteRoom()

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE MESSAGING
    suspend fun getLastToken() : State<String>

    // FIREBASE AUTH
    suspend fun sendPassResetEmail(email: String) : State<String>
    suspend fun signIn(email: String, pass: String) : State<String>
    suspend fun signUp(email: String, pass: String) : State<String>

    // FIREBASE DATABASE
    suspend fun checkVersion(id: String) : State<Int>
    suspend fun getUser(id: String) : State<Login>
    suspend fun setUser(login: Login) : State<String>
    suspend fun updateUserToken(id: String, token: String) : State<String>
}