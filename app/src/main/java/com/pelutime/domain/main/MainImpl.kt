package com.pelutime.domain.main

import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.local.main.MainLocalDataSource
import com.pelutime.data.model.login.Login
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.data.remote.main.MainRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class MainImpl @Inject constructor (private val mainLocalDataSource: MainLocalDataSource,
                                    private val mainRemoteDataSource: MainRemoteDataSource) : IMainRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = mainLocalDataSource.getUserRoomDB()
    override suspend fun deleteUserRoom(userEntity: UserEntity) = mainLocalDataSource.deleteUserRoomDB(userEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE AUTH
    override suspend fun signOut() = mainRemoteDataSource.signOutDB()

    // FIREBASE MESSAGING
    override suspend fun getLastToken(): State<String> = mainRemoteDataSource.getLastTokenDB()

    // FIREBASE DATABASE
    override suspend fun checkUser(id: String) : State<String> = mainRemoteDataSource.checkUserDB(id)
    override suspend fun getPlaces() : State<List<Ubication>> = mainRemoteDataSource.getPlacesDB()
    override suspend fun getEmployeesByPlace(places: List<Ubication>) : State<Map<String, List<Employees>>> = mainRemoteDataSource.getEmployeesByPlaceDB(places)
    override suspend fun getRandomDocuments(place: String, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>> = mainRemoteDataSource.getRandomDocumentsDB(place, listEmployees, currentDate, lastDate)
    override suspend fun setDocuments(mapPlaces: Map<String, List<Employees>>, mapSchedule: MutableMap<String, Map<String, List<String>>>, listDates: List<String>) : State<String> = mainRemoteDataSource.setDocumentsDB(mapPlaces, mapSchedule, listDates)
    override suspend fun getUsers() : State<List<Login>> = mainRemoteDataSource.getUsersDB()
    override suspend fun updateUsers(users: List<Login>) : State<String> = mainRemoteDataSource.updateUsersDB(users)
    override suspend fun updateAdminToken(id: String, token: String) : State<String> = mainRemoteDataSource.updateAdminTokenDB(id, token)
}