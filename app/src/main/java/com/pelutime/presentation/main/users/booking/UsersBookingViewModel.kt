package com.pelutime.presentation.main.users.booking

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.model.main.booking.BookingEmployees
import com.pelutime.data.model.main.booking.BookingSchedule
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.domain.main.booking.IBookingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class UsersBookingViewModel @Inject constructor(private val iBookingRepo: IBookingRepo): BaseViewModel() {

    fun liveGetEmployees(section: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iBookingRepo.getUserRoom().mapSuccess { user ->
                iBookingRepo.getEmployees(user.place).collect { resultEmployees ->
                    resultEmployees.mapSuccess { listEmployees ->
                        val employees = getEmployeesForAdapter(listEmployees, section)
                        emit(State.Success(employees))
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveGetDocuments(listEmployees: List<Employees>, bookingEmployees: BookingEmployees) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iBookingRepo.getUserRoom().mapSuccess { user ->
                iBookingRepo.getUbication(user.place).mapSuccess { ubication ->
                    val mapSchedule = getScheduleByPlace(ubication.schedule)
                    iBookingRepo.getDocuments(user.place, mapSchedule, listEmployees, getCurrentDate(), getLastDate().last()).collect { resultBookings ->
                        resultBookings.mapSuccess { mapBookings ->
                            val schedule = getScheduleForAdapter(mapBookings, bookingEmployees)
                            emit(State.Success(schedule))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateToPending(map: MutableMap<String, String>) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iBookingRepo.getUserRoom().mapSuccess { user ->
                if (user.bookings <= 10) {
                    map["id"] = user.id
                    map["name"] = user.name
                    map["place"] = user.place
                    emit(iBookingRepo.updateToPending(map))
                    iBookingRepo.updateUserBookings(user.id, user.bookings + 1).mapSuccess {
                        iBookingRepo.setUserRoom(UserEntity(user.id, user.name, user.email, user.pass, user.image, user.place, user.token, user.bookings + 1, user.rejections))
                    }
                } else {
                    emit(State.Success("Límite de reservas y/o cancelaciones alcanzado!"))
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    // RETROFIT SERVICE
    fun liveSendNotification(notificationData: NotificationData) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iBookingRepo.getUserRoom().mapSuccess { user ->
                iBookingRepo.getUbication(user.place).mapSuccess { ubication ->
                    iBookingRepo.getToken(ubication.admin).mapSuccess { token ->
                        if (token.isNotEmpty()) {
                            PushNotification(notificationData, token).also {
                                emit(State.Success(iBookingRepo.sendNotification(it)))
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

    private fun getEmployeesForAdapter(listEmployees: List<Employees>, area: String) : List<BookingEmployees> {
        val list = mutableListOf<BookingEmployees>()

        for (employee in listEmployees) {
            if (employee.sections.contains(area)) {
                list.add(
                    BookingEmployees(
                    employee.experiences,
                    employee.hobbies,
                    employee.image,
                    employee.name,
                    employee.sections)
                )
            }
        }

        return list
    }

    private fun getScheduleForAdapter(mapBookings: Map<String, Map<String, Map<String, String>>>, item: BookingEmployees) : List<BookingSchedule> {
        val list = mutableListOf<BookingSchedule>()

        val arrayCurrentDate = getCurrentDate().split("--").toTypedArray()
        val currentDate = arrayCurrentDate[0]

        val arrayCurrentHour = arrayCurrentDate[1].split(":").toTypedArray()
        val currentHour = arrayCurrentHour[0]
        val currentMinute = arrayCurrentHour[1]

        mapBookings.forEach {
            if (it.key.contains(currentDate)) {
                it.value.forEach { map ->
                    if (map.value["employee"] == item.name && map.value["status"] == "LIBRE") {

                        if ("${map.key[0]}${map.key[1]}".toInt() == currentHour.toInt() &&
                            "${map.key[3]}${map.key[4]}".toInt() > currentMinute.toInt()) {

                            list.add(
                                BookingSchedule(
                                    name = item.name,
                                    image = item.image,
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = item.sections
                                )
                            )
                        }

                        if ("${map.key[0]}${map.key[1]}".toInt() > currentHour.toInt()) {

                            list.add(
                                BookingSchedule(
                                    name = item.name,
                                    image = item.image,
                                    schedule = "${map.value["schedule"]} ${map.key} hs.",
                                    sections = item.sections
                                )
                            )
                        }
                    }
                }

            } else {
                it.value.forEach { map ->
                    if (map.value["employee"] == item.name && map.value["status"] == "LIBRE") {

                        list.add(
                            BookingSchedule(
                                name = item.name,
                                image = item.image,
                                schedule = "${map.value["schedule"]} ${map.key} hs.",
                                sections = item.sections
                            )
                        )
                    }
                }
            }
        }

        return list
    }
}