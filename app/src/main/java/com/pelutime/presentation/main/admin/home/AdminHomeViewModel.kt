package com.pelutime.presentation.main.admin.home

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.model.main.home.*
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.domain.main.home.IHomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(private val iHomeRepo: IHomeRepo): BaseViewModel() {

    val liveGetUserRoom = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            var greeting = ""
            iHomeRepo.getUserRoom().mapSuccess { dataUser ->
                when (getCurrentDate().substringAfter("--").substringBefore(":").toInt()) {
                    in 0..4 -> greeting = "¡Buenas noches ${dataUser.name}!"
                    in 5..12 -> greeting = "¡Buenos días ${dataUser.name}!"
                    in 13..19 -> greeting = "¡Buenas tardes ${dataUser.name}!"
                    in 20..23 -> greeting = "¡Buenas noches ${dataUser.name}!"
                }
                val map = mapOf(
                    greeting to dataUser
                )

                emit(State.Success(map))
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    val liveGetDocuments = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            delay(500)
            iHomeRepo.getUserRoom().mapSuccess { user ->
                iHomeRepo.getEmployees(user.place).collect { resultEmployees ->
                    resultEmployees.mapSuccess { listEmployees ->
                        iHomeRepo.getUbication(user.place).mapSuccess { ubication ->
                            val mapSchedule = getScheduleByPlace(ubication.schedule)
                            iHomeRepo.getDocuments(user.place, mapSchedule, listEmployees, getCurrentDate(), getLastDate().last()).collect { resultBookings ->
                                resultBookings.mapSuccess { mapBookings ->
                                    val documents = HomeAdmin(
                                        today = getTodayForAdapter(mapBookings, listEmployees),
                                        pending = getPendingForAdapter(mapBookings, listEmployees),
                                        rejected = getRejectedForAdapter(mapBookings, listEmployees),
                                        future = getFutureForAdapter(mapBookings, listEmployees)
                                    )
                                    emit(State.Success(documents))
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateToFree(date: String, hour: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                emit(iHomeRepo.updateToFree(user.place, date, hour))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateToBooked(map: Map<String, String>) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                emit(iHomeRepo.updateToBooked(user.place, map))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateToRejected(map: Map<String, String>) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                emit(iHomeRepo.updateToRejected(user.place, map))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveSendNotification(notificationData: NotificationData, id: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getToken("000_app_users", id).mapSuccess { token ->
                if (token.isNotEmpty()) {
                    PushNotification(notificationData, token).also {
                        emit(State.Success(iHomeRepo.sendNotification(it)))
                    }
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    /*
    <<<<<< VIEW MODEL >>>>>>
     */

    private fun getTodayForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>, listEmployees: List<Employees>) : List<HomeToday> {
        val list = mutableListOf<HomeToday>()

        val arrayCurrentDate = getCurrentDate().split("--").toTypedArray()
        val currentDate = arrayCurrentDate[0]

        mapBookings.forEach {
            if (it.key.contains(currentDate)) {
                it.value.forEach { map ->
                    if (map.value["status"] == "RESERVADO") {
                        for (employee in listEmployees) {
                            if (map.value["employee"] == employee.name) {
                                list.add(
                                    HomeToday(
                                        id = "${map.value["idUser"]}",
                                        image = employee.image,
                                        nameEmployee = employee.name,
                                        nameUser = "${map.value["nameUser"]}",
                                        schedule = "${map.value["schedule"]} ${map.key} hs.",
                                        sectionSelected = "${map.value["sectionSelected"]}"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return list
    }

    private fun getPendingForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>, listEmployees: List<Employees>) : List<HomePending> {
        val list = mutableListOf<HomePending>()

        mapBookings.values.forEach {
            it.forEach { map ->
                if (map.value["status"] == "PENDIENTE") {
                    for (employee in listEmployees) {
                        if (map.value["employee"] == employee.name) {
                            list.add(
                                HomePending(
                                    experiences = "",
                                    hobbies = "",
                                    id = "${map.value["idUser"]}",
                                    image = employee.image,
                                    nameEmployee = employee.name,
                                    nameUser = "${map.value["nameUser"]}",
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = "",
                                    sectionSelected = "${map.value["sectionSelected"]}"
                                )
                            )
                        }
                    }
                }
            }
        }

        return list
    }

    private fun getRejectedForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>, listEmployees: List<Employees>) : List<HomeRejected> {
        val list = mutableListOf<HomeRejected>()

        mapBookings.values.forEach {
            it.forEach { map ->
                if (map.value["status"] == "RECHAZADO") {
                    for (employee in listEmployees) {
                        if (map.value["employee"] == employee.name) {
                            list.add(
                                HomeRejected(
                                    experiences = "",
                                    hobbies = "",
                                    id = "${map.value["idUser"]}",
                                    image = employee.image,
                                    nameEmployee = employee.name,
                                    nameUser = "${map.value["nameUser"]}",
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = "",
                                    sectionSelected = "${map.value["sectionSelected"]}"
                                )
                            )
                        }
                    }
                }
            }
        }

        return list
    }

    private fun getFutureForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>, listEmployees: List<Employees>) : List<HomeFuture> {
        val list = mutableListOf<HomeFuture>()

        val arrayCurrentDate = getCurrentDate().split("--").toTypedArray()
        val currentDate = arrayCurrentDate[0]

        mapBookings.forEach {
            if (!it.key.contains(currentDate)) {
                it.value.forEach { map ->
                    if (map.value["status"] == "RESERVADO") {
                        for (employee in listEmployees) {
                            if (map.value["employee"] == employee.name) {
                                list.add(
                                    HomeFuture(
                                        id = "${map.value["idUser"]}",
                                        image = employee.image,
                                        nameEmployee = employee.name,
                                        nameUser = "${map.value["nameUser"]}",
                                        schedule = "${map.value["schedule"]} ${map.key} hs.",
                                        sectionSelected = "${map.value["sectionSelected"]}"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return list
    }
}