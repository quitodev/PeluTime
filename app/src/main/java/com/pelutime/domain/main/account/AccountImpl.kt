package com.pelutime.domain.main.account

import android.net.Uri
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.local.main.account.AccountLocalDataSource
import com.pelutime.data.remote.main.account.AccountRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class AccountImpl @Inject constructor (private val accountLocalDataSource: AccountLocalDataSource,
                                       private val accountRemoteDataSource: AccountRemoteDataSource) : IAccountRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = accountLocalDataSource.getUserRoomDB()
    override suspend fun setUserRoom(userEntity: UserEntity) = accountLocalDataSource.setUserRoomDB(userEntity)
    override suspend fun deleteUserRoom(userEntity: UserEntity) = accountLocalDataSource.deleteUserRoomDB(userEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE AUTH
    override suspend fun updateUserEmail(email: String) = accountRemoteDataSource.updateUserEmailDB(email)
    override suspend fun updateUserPass(pass: String) = accountRemoteDataSource.updateUserPassDB(pass)
    override suspend fun deleteUserAuth() = accountRemoteDataSource.deleteUserAuthDB()
    override suspend fun signIn(email: String, pass: String) = accountRemoteDataSource.signInDB(email, pass)
    override suspend fun signOut() = accountRemoteDataSource.signOutDB()

    // FIREBASE DATABASE
    override suspend fun updateImageUser(id: String, image: String) : State<String> = accountRemoteDataSource.updateImageUserDB(id, image)
    override suspend fun updateUser(id: String, name: String, email: String) : State<String> = accountRemoteDataSource.updateUserDB(id, name, email)
    override suspend fun deleteUser(id: String) : State<String> = accountRemoteDataSource.deleteUserDB(id)

    // FIREBASE STORAGE
    override suspend fun getImages(id: String) : State<String> = accountRemoteDataSource.getImagesDB(id)
    override suspend fun uploadImages(id: String, image: Uri) : State<String> = accountRemoteDataSource.uploadImagesDB(id, image)
    override suspend fun deleteImages(id: String) : State<String> = accountRemoteDataSource.deleteImagesDB(id)
}