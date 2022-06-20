package com.pelutime.domain.main.booking

import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.model.main.ubication.Ubication
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface IBookingRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>
    suspend fun setUserRoom(userEntity: UserEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // RETROFIT SERVICE
    suspend fun sendNotification(pushNotification: PushNotification) : State<Response<ResponseBody>>

    // FIREBASE DATABASE
    suspend fun getToken(id: String) : State<String>
    suspend fun getEmployees(place: String) : Flow<State<List<Employees>>>
    suspend fun getDocuments(place: String, mapSchedule: Map<String, List<String>>, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>>
    suspend fun getUbication(place: String) : State<Ubication>
    suspend fun updateToPending(map: Map<String, String>) : State<String>
    suspend fun updateUserBookings(id: String, bookings: Int) : State<String>
}