package com.pelutime.presentation.main.maps

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.domain.main.maps.IMapsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val iMapsRepo: IMapsRepo): BaseViewModel() {

    val liveGetPlaces = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iMapsRepo.getPlaces().collect { resultPlaces ->
                resultPlaces.mapSuccess { places ->
                    emit(State.Success(places))
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveSetPlace(place: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iMapsRepo.getUserRoom().mapSuccess { user ->
                iMapsRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, place, user.token, user.bookings, user.rejections))
                iMapsRepo.updateUserPlace(user.id, place).mapSuccess {
                    emit(State.Success(user.id))
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }
}