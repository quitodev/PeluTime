package com.pelutime.presentation.main.users.ubication

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.domain.main.ubication.IUbicationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class UsersUbicationViewModel @Inject constructor(private val iUbicationRepo: IUbicationRepo): BaseViewModel() {

    val liveGetUbication = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iUbicationRepo.getUserRoom().mapSuccess { user ->
                emit(iUbicationRepo.getUbication(user.place))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }
}