package com.pelutime.domain.login

import com.pelutime.data.model.login.Login
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.local.login.LoginLocalDataSource
import com.pelutime.data.remote.login.LoginRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class LoginImpl @Inject constructor (private val loginLocalDataSource: LoginLocalDataSource,
                                     private val loginRemoteDataSource: LoginRemoteDataSource) : ILoginRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = loginLocalDataSource.getUserRoomDB()
    override suspend fun setUserRoom(userEntity: UserEntity) = loginLocalDataSource.setUserRoomDB(userEntity)
    override suspend fun deleteRoom() = loginLocalDataSource.deleteRoomDB()

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE MESSAGING
    override suspend fun getLastToken(): State<String> = loginRemoteDataSource.getLastTokenDB()

    // FIREBASE AUTH
    override suspend fun sendPassResetEmail(email: String) : State<String> = loginRemoteDataSource.sendPassResetEmailDB(email)
    override suspend fun signIn(email: String, pass: String) = loginRemoteDataSource.signInDB(email, pass)
    override suspend fun signUp(email: String, pass: String) = loginRemoteDataSource.signUpDB(email, pass)

    // FIREBASE DATABASE
    override suspend fun checkVersion(id: String) : State<Int> = loginRemoteDataSource.checkVersionDB(id)
    override suspend fun getUser(id: String) : State<Login> = loginRemoteDataSource.getUserDB(id)
    override suspend fun setUser(login: Login) : State<String> = loginRemoteDataSource.setUserDB(login)
    override suspend fun updateUserToken(id: String, token: String) : State<String> = loginRemoteDataSource.updateUserTokenDB(id, token)
}