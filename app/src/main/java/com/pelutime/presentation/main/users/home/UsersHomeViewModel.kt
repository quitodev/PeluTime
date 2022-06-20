package com.pelutime.presentation.main.users.home

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.model.main.home.*
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.domain.main.home.IHomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class UsersHomeViewModel @Inject constructor(private val iHomeRepo: IHomeRepo): BaseViewModel() {

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
            liveGetUserRoom.value!!.mapSuccess { resultUser ->
                resultUser.values.forEach { user ->
                    iHomeRepo.getEmployees(user.place).collect { resultEmployees ->
                        resultEmployees.mapSuccess { listEmployees ->
                            iHomeRepo.getUbication(user.place).mapSuccess { ubication ->
                                val mapSchedule = getScheduleByPlace(ubication.schedule)
                                iHomeRepo.getDocuments(user.place, mapSchedule, listEmployees, getCurrentDate(), getLastDate().last()).collect { resultBookings ->
                                    resultBookings.mapSuccess { mapBookings ->
                                        val documents = HomeUsers(
                                            confirmed = getConfirmedForAdapter(mapBookings, listEmployees, user),
                                            pending = getPendingForAdapter(mapBookings, listEmployees, user),
                                            rejected = getRejectedForAdapter(mapBookings, listEmployees, user),
                                            free = getFreeForAdapter(mapBookings, listEmployees)
                                        )
                                        emit(State.Success(documents))
                                    }
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
                if (user.rejections <= 10) {
                    emit(iHomeRepo.updateToFree(user.place, date, hour))
                    iHomeRepo.updateUserRejections(user.id, user.rejections + 1).mapSuccess {
                        iHomeRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, user.token, user.bookings, user.rejections + 1))
                    }
                } else {
                    emit(State.Success("Límite de reservas y/o cancelaciones alcanzado!"))
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateToPending(map: Map<String, String>) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                if (user.bookings <= 10) {
                    emit(iHomeRepo.updateToPending(user.place, map))
                    iHomeRepo.updateUserBookings(user.id, user.bookings + 1).mapSuccess {
                        iHomeRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, user.token, user.bookings + 1, user.rejections))
                    }
                } else {
                    emit(State.Success("Límite de reservas y/o cancelaciones alcanzado!"))
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    val liveUpdatePlace = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                iHomeRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, "Vacío", user.token, user.bookings, user.rejections))
                iHomeRepo.updateUserPlace(user.id).mapSuccess {
                    emit(State.Success(it))
                }
            }
        } catch (e: Exception){
            emit(State.Failure(e))
        }
    }

    fun liveSendNotification(notificationData: NotificationData) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iHomeRepo.getUserRoom().mapSuccess { user ->
                iHomeRepo.getUbication(user.place).mapSuccess { ubication ->
                    iHomeRepo.getToken("000_app_admin", ubication.admin).mapSuccess { token ->
                        if (token.isNotEmpty()) {
                            PushNotification(notificationData, token).also {
                                emit(State.Success(iHomeRepo.sendNotification(it)))
                            }
                        }
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

    private fun getConfirmedForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>,
                                       listEmployees: List<Employees>, dataUser: UserEntity) : List<HomeConfirmed> {

        val list = mutableListOf<HomeConfirmed>()

        mapBookings.values.forEach {
            it.forEach { map ->
                if (map.value["idUser"] == dataUser.id && map.value["status"] == "RESERVADO") {
                    for (employee in listEmployees) {
                        if (map.value["employee"] == employee.name) {
                            list.add(
                                HomeConfirmed(
                                    experiences = employee.experiences,
                                    hobbies = employee.hobbies,
                                    id = "",
                                    image = employee.image,
                                    name = employee.name,
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = employee.sections,
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

    private fun getPendingForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>,
                                     listEmployees: List<Employees>, dataUser: UserEntity) : List<HomePending> {

        val list = mutableListOf<HomePending>()

        mapBookings.values.forEach {
            it.forEach { map ->
                if (map.value["idUser"] == dataUser.id && map.value["status"] == "PENDIENTE") {
                    for (employee in listEmployees) {
                        if (map.value["employee"] == employee.name) {
                            list.add(
                                HomePending(
                                    experiences = employee.experiences,
                                    hobbies = employee.hobbies,
                                    id = "",
                                    image = employee.image,
                                    nameEmployee = employee.name,
                                    nameUser = "",
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = employee.sections,
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

    private fun getRejectedForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>,
                                      listEmployees: List<Employees>, dataUser: UserEntity) : List<HomeRejected> {

        val list = mutableListOf<HomeRejected>()

        mapBookings.values.forEach {
            it.forEach { map ->
                if (map.value["idUser"] == dataUser.id && map.value["status"] == "RECHAZADO") {
                    for (employee in listEmployees) {
                        if (map.value["employee"] == employee.name) {
                            list.add(
                                HomeRejected(
                                    experiences = employee.experiences,
                                    hobbies = employee.hobbies,
                                    id = "",
                                    image = employee.image,
                                    nameEmployee = employee.name,
                                    nameUser = "",
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = employee.sections,
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

    private fun getFreeForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>,
                                  listEmployees: List<Employees>) : List<HomeFree> {

        val list = mutableListOf<HomeFree>()

        val arrayCurrentDate = getCurrentDate().split("--").toTypedArray()
        val currentDate = arrayCurrentDate[0]

        val arrayCurrentHour = arrayCurrentDate[1].split(":").toTypedArray()
        val currentHour = arrayCurrentHour[0]
        val currentMinute = arrayCurrentHour[1]

        val listCheck = mutableListOf<String>()

        when (currentHour.toInt()) {
            in 7..20 -> {
                for (employee in listEmployees) {
                    when (currentMinute.toInt()) {
                        in 0..29 -> {
                            val firstHalfHour = mapBookings["${currentDate}_${employee.name}"]?.get("${currentHour}:30")

                            if (firstHalfHour?.get("status") == "LIBRE") {
                                listCheck.add(employee.name)

                                val minutes = 30 - currentMinute.toInt()

                                list.add(
                                    HomeFree(
                                        experiences = employee.experiences,
                                        hobbies = employee.hobbies,
                                        id = "",
                                        image = employee.image,
                                        minutes = "LIBRE EN $minutes MINUTOS",
                                        name = employee.name,
                                        schedule = "${firstHalfHour["schedule"]} $currentHour:30 hs.",
                                        sections = employee.sections
                                    )
                                )
                            }
                        }

                        in 30..59 -> {
                            val newHour: String = if (currentHour.toInt() + 1 < 10) {
                                "0${currentHour.toInt() + 1}"
                            } else {
                                "${currentHour.toInt() + 1}"
                            }

                            val firstHalfHour = mapBookings["${currentDate}_${employee.name}"]?.get("$newHour:00")

                            if (firstHalfHour?.get("status") == "LIBRE") {
                                listCheck.add(employee.name)

                                val minutes = 60 - currentMinute.toInt()

                                list.add(
                                    HomeFree(
                                        experiences = employee.experiences,
                                        hobbies = employee.hobbies,
                                        id = "",
                                        image = employee.image,
                                        minutes = "LIBRE EN $minutes MINUTOS",
                                        name = employee.name,
                                        schedule = "${firstHalfHour["schedule"]} $newHour:00 hs.",
                                        sections = employee.sections
                                    )
                                )
                            }
                        }
                    }
                }

                for (employee in listEmployees) {
                    if (!listCheck.contains(employee.name)) {
                        when (currentMinute.toInt()) {
                            in 0..29 -> {
                                val newHour: String = if (currentHour.toInt() + 1 < 10) {
                                    "0${currentHour.toInt() + 1}"
                                } else {
                                    "${currentHour.toInt() + 1}"
                                }

                                val lastHalfHour = mapBookings["${currentDate}_${employee.name}"]?.get("$newHour:00")

                                if (lastHalfHour?.get("status") == "LIBRE") {
                                    val minutes = 60 - currentMinute.toInt()

                                    list.add(
                                        HomeFree(
                                            experiences = employee.experiences,
                                            hobbies = employee.hobbies,
                                            id = "",
                                            image = employee.image,
                                            minutes = "LIBRE EN $minutes MINUTOS",
                                            name = employee.name,
                                            schedule = "${lastHalfHour["schedule"]} $newHour:00 hs.",
                                            sections = employee.sections
                                        )
                                    )
                                }
                            }

                            in 30..59 -> {

                                val newHour: String = if (currentHour.toInt() + 1 < 10) {
                                    "0${currentHour.toInt() + 1}"
                                } else {
                                    "${currentHour.toInt() + 1}"
                                }

                                val lastHalfHour = mapBookings["${currentDate}_${employee.name}"]?.get("$newHour:30")

                                if (lastHalfHour?.get("status") == "LIBRE") {
                                    val minutes = 90 - currentMinute.toInt()

                                    list.add(
                                        HomeFree(
                                            experiences = employee.experiences,
                                            hobbies = employee.hobbies,
                                            id = "",
                                            image = employee.image,
                                            minutes = "LIBRE EN $minutes MINUTOS",
                                            name = employee.name,
                                            schedule = "${lastHalfHour["schedule"]} $newHour:30 hs.",
                                            sections = employee.sections
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return list
    }
}