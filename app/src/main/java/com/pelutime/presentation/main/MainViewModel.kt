package com.pelutime.presentation.main

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.domain.main.IMainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val iMainRepo: IMainRepo): BaseViewModel() {

    fun liveCheckUser(id: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            if (id == "KAngQSj7XbNQgV8WnHfwEYqX5Xs1") {
                iMainRepo.getUsers().mapSuccess { users ->
                    iMainRepo.updateUsers(users).mapSuccess {
                        iMainRepo.getPlaces().mapSuccess { places ->
                            iMainRepo.getEmployeesByPlace(places).mapSuccess { mapPlaces ->
                                var randomPlace = ""
                                for (place in places) {
                                    randomPlace = place.place
                                    break
                                }
                                iMainRepo.getRandomDocuments(randomPlace, mapPlaces.getValue(randomPlace), getCurrentDate(), getLastDate().last()).collect { resultBookings ->
                                    resultBookings.mapSuccess { mapBookings ->
                                        val listDates = getDatesToCreate(mapBookings)
                                        if (listDates.last() != "Sin cambios") {
                                            val mapSchedule = mutableMapOf<String, Map<String, List<String>>>()
                                            for (place in places) {
                                                mapSchedule[place.place] = getScheduleByPlace(place.schedule)
                                            }
                                            mapSchedule.toSortedMap()
                                            iMainRepo.setDocuments(mapPlaces, mapSchedule, listDates).mapSuccess {
                                                emit(State.Success("SuperAdmin"))
                                            }
                                        } else {
                                            emit(State.Success("SuperAdmin"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                iMainRepo.checkUser(id).mapSuccess {
                    iMainRepo.getLastToken().mapSuccess { token ->
                        if (token != "Token null") {
                            iMainRepo.updateAdminToken(id, token)
                        }
                    }
                    emit(State.Success("Admin"))
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    val liveSignOut = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iMainRepo.getUserRoom().mapSuccess { user ->
                iMainRepo.deleteUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, user.token, user.bookings, user.rejections))
                iMainRepo.signOut()
                emit(State.Success("Operación realizada con éxito"))
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }
}