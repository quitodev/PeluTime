package com.pelutime.domain.main.booking

import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.local.main.booking.BookingLocalDataSource
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.data.remote.main.booking.BookingRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class BookingImpl @Inject constructor (private val bookingLocalDataSource: BookingLocalDataSource,
                                       private val bookingRemoteDataSource: BookingRemoteDataSource) : IBookingRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = bookingLocalDataSource.getUserRoomDB()
    override suspend fun setUserRoom(userEntity: UserEntity) = bookingLocalDataSource.setUserRoomDB(userEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // RETROFIT SERVICE
    override suspend fun sendNotification(pushNotification: PushNotification) = bookingRemoteDataSource.sendNotificationDB(pushNotification)
    
    // FIREBASE DATABASE
    override suspend fun getToken(id: String) : State<String> = bookingRemoteDataSource.getTokenDB(id)
    override suspend fun getEmployees(place: String) : Flow<State<List<Employees>>> = bookingRemoteDataSource.getEmployeesDB(place)
    override suspend fun getDocuments(place: String, mapSchedule: Map<String, List<String>>, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>> = bookingRemoteDataSource.getDocumentsDB(place, mapSchedule, listEmployees, currentDate, lastDate)
    override suspend fun getUbication(place: String) : State<Ubication> = bookingRemoteDataSource.getUbicationDB(place)
    override suspend fun updateToPending(map: Map<String, String>) : State<String> = bookingRemoteDataSource.updateToPendingDB(map)
    override suspend fun updateUserBookings(id: String, bookings: Int) : State<String> = bookingRemoteDataSource.updateUserBookingsDB(id, bookings)
}