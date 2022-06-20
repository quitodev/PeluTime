package com.pelutime.domain.main.home

import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.model.main.ubication.Ubication
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface IHomeRepo {

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
    suspend fun getToken(collection: String, id: String) : State<String>
    suspend fun getEmployees(place: String) : Flow<State<List<Employees>>>
    suspend fun getUbication(place: String) : State<Ubication>
    suspend fun getDocuments(place: String, mapSchedule: Map<String, List<String>>, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>>
    suspend fun updateToFree(place: String, date: String, hour: String) : State<String>
    suspend fun updateToPending(place: String, map: Map<String, String>) : State<String>
    suspend fun updateToBooked(place: String, map: Map<String, String>) : State<String>
    suspend fun updateToRejected(place: String, map: Map<String, String>) : State<String>
    suspend fun updateUserBookings(id: String, bookings: Int) : State<String>
    suspend fun updateUserRejections(id: String, rejections: Int) : State<String>
    suspend fun updateUserPlace(id: String) : State<String>
}