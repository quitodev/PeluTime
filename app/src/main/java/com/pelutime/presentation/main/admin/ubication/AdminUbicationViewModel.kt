package com.pelutime.presentation.main.admin.ubication

import android.net.Uri
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.domain.main.ubication.IUbicationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AdminUbicationViewModel @Inject constructor(private val iUbicationRepo: IUbicationRepo): BaseViewModel() {

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

    fun liveUpdateUbication(ubication: Ubication) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iUbicationRepo.getUserRoom().mapSuccess { user ->
                emit(iUbicationRepo.updateUbication(user.place, ubication))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateImage(image: Uri, imageName: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iUbicationRepo.getUserRoom().mapSuccess { user ->
                iUbicationRepo.uploadImages(user.place, image, imageName).mapSuccess {
                    iUbicationRepo.getImages(user.place, imageName).mapSuccess { url ->
                        iUbicationRepo.updateUbicationImage(user.place, url).mapSuccess {
                            emit(State.Success(url))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }
}