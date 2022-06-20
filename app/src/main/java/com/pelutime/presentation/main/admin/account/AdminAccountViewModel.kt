package com.pelutime.presentation.main.admin.account

import android.net.Uri
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.domain.main.account.IAccountRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AdminAccountViewModel @Inject constructor(private val iAccountRepo: IAccountRepo): BaseViewModel() {

    val liveGetUserRoom = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            emit(iAccountRepo.getUserRoom())
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveUpdateUser(name: String, email: String, pass: String, oldPass: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            liveGetUserRoom.value!!.mapSuccess { user ->
                iAccountRepo.signIn(user.email, oldPass).mapSuccess { id ->
                    if (id != "Usuario desconectado") {
                        iAccountRepo.updateUserEmail(email).mapSuccess {
                            if (pass != "Contraseña") {
                                iAccountRepo.updateUserPass(pass)
                            }
                            iAccountRepo.updateUser(user.id, name, email).mapSuccess {
                                iAccountRepo.setUserRoom(UserEntity(user.id, name, email, "N/A", user.image, user.place, user.token, user.bookings, user.rejections))
                                emit(State.Success(it))
                            }
                        }
                    } else {
                        emit(State.Success("No se pudo loguear nuevamente"))
                    }
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveUpdateImage(image: Uri) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iAccountRepo.getUserRoom().mapSuccess { user ->
                iAccountRepo.uploadImages(user.id, image).mapSuccess {
                    iAccountRepo.getImages(user.id).mapSuccess { url ->
                        iAccountRepo.updateImageUser(user.id, url).mapSuccess {
                            iAccountRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, url, user.place, user.token, user.bookings, user.rejections))
                            emit(State.Success(url))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    val liveSignOut = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iAccountRepo.getUserRoom().mapSuccess { user ->
                iAccountRepo.deleteUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, user.token, user.bookings, user.rejections))
                iAccountRepo.signOut()
                emit(State.Success("Operación realizada con éxito"))
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }
}