package com.pelutime.domain.main.home

import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.notifications.PushNotification
import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.local.main.home.HomeLocalDataSource
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.data.remote.main.home.HomeRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class HomeImpl @Inject constructor (private val homeLocalDataSource: HomeLocalDataSource,
                                    private val homeRemoteDataSource: HomeRemoteDataSource) : IHomeRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = homeLocalDataSource.getUserRoomDB()
    override suspend fun setUserRoom(userEntity: UserEntity) = homeLocalDataSource.setUserRoomDB(userEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // RETROFIT SERVICE
    override suspend fun sendNotification(pushNotification: PushNotification) = homeRemoteDataSource.sendNotificationDB(pushNotification)

    // FIREBASE DATABASE
    override suspend fun getToken(collection: String, id: String) : State<String> = homeRemoteDataSource.getTokenDB(collection, id)
    override suspend fun getEmployees(place: String) : Flow<State<List<Employees>>> = homeRemoteDataSource.getEmployeesDB(place)
    override suspend fun getUbication(place: String) : State<Ubication> = homeRemoteDataSource.getUbicationDB(place)
    override suspend fun getDocuments(place: String, mapSchedule: Map<String, List<String>>, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>> = homeRemoteDataSource.getDocumentsDB(place, mapSchedule, listEmployees, currentDate, lastDate)
    override suspend fun updateToFree(place: String, date: String, hour: String) : State<String> = homeRemoteDataSource.updateToFreeDB(place, date, hour)
    override suspend fun updateToPending(place: String, map: Map<String, String>) : State<String> = homeRemoteDataSource.updateToPendingDB(place, map)
    override suspend fun updateToBooked(place: String, map: Map<String, String>) : State<String> = homeRemoteDataSource.updateToBookedDB(place, map)
    override suspend fun updateToRejected(place: String, map: Map<String, String>) : State<String> = homeRemoteDataSource.updateToRejectedDB(place, map)
    override suspend fun updateUserBookings(id: String, bookings: Int) : State<String> = homeRemoteDataSource.updateUserBookingsDB(id, bookings)
    override suspend fun updateUserRejections(id: String, rejections: Int) : State<String> = homeRemoteDataSource.updateUserRejectionsDB(id, rejections)
    override suspend fun updateUserPlace(id: String): State<String> = homeRemoteDataSource.updateUserPlaceDB(id)
}