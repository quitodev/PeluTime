package com.pelutime.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.model.login.Login
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.domain.login.ILoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val iLoginRepo: ILoginRepo): ViewModel() {

    fun liveSendPassResetEmail(email: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            emit(iLoginRepo.sendPassResetEmail(email))
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    val liveCheckVersion = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            emit(iLoginRepo.checkVersion("KAngQSj7XbNQgV8WnHfwEYqX5Xs1"))
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveSignIn(email: String, pass: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iLoginRepo.signIn(email, pass).mapSuccess { id ->
                if (id != "Usuario desconectado") {
                    iLoginRepo.getLastToken().mapSuccess { token ->
                        if (token != "Token null") {
                            iLoginRepo.updateUserToken(id, token).mapSuccess {
                                iLoginRepo.getUser(id).mapSuccess { user ->
                                    iLoginRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, token, user.bookings, user.rejections))
                                    emit(State.Success(user))
                                }
                            }
                        } else {
                            iLoginRepo.getUser(id).mapSuccess { user ->
                                iLoginRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, token, user.bookings, user.rejections))
                                emit(State.Success(user))
                            }
                        }
                    }
                } else {
                    iLoginRepo.deleteRoom()
                    emit(State.Success(Login("Usuario desconectado", "", "", "", "", "", "", 0, 0)))
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveSignUp(name: String, email: String, pass: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iLoginRepo.signUp(email, pass).mapSuccess { id ->
                iLoginRepo.getLastToken().mapSuccess { token ->
                    if (token != "Token null") {
                        iLoginRepo.setUser(Login(id, name, email, "N/A", "Vacío", "Vacío", token, 0, 0)).mapSuccess {
                            iLoginRepo.setUserRoom(UserEntity(id, name, email, "N/A", "Vacío", "Vacío", token, 0, 0))
                            emit(State.Success(Login(id, name, email, "N/A", "Vacío", "Vacío", token, 0, 0)))
                        }
                    } else {
                        iLoginRepo.setUser(Login(id, name, email, "N/A", "Vacío", "Vacío", "", 0, 0)).mapSuccess {
                            iLoginRepo.setUserRoom(UserEntity(id, name, email, "N/A", "Vacío", "Vacío", "", 0, 0))
                            emit(State.Success(Login(id, name, email, "N/A", "Vacío", "Vacío", "", 0, 0)))
                        }
                    }
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }
}