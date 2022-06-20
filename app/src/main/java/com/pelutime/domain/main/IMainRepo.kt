package com.pelutime.domain.main

import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.model.login.Login
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.data.model.main.ubication.Ubication
import kotlinx.coroutines.flow.Flow

interface IMainRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>
    suspend fun deleteUserRoom(userEntity: UserEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE AUTH
    suspend fun signOut()

    // FIREBASE MESSAGING
    suspend fun getLastToken() : State<String>

    // FIREBASE DATABASE
    suspend fun checkUser(id: String) : State<String>
    suspend fun getPlaces() : State<List<Ubication>>
    suspend fun getEmployeesByPlace(places: List<Ubication>) : State<Map<String, List<Employees>>>
    suspend fun getRandomDocuments(place: String, listEmployees: List<Employees>, currentDate: String, lastDate: String) : Flow<State<Map<String, Map<String, Map<String, String>>>>>
    suspend fun setDocuments(mapPlaces: Map<String, List<Employees>>, mapSchedule: MutableMap<String, Map<String, List<String>>>, listDates: List<String>) : State<String>
    suspend fun getUsers() : State<List<Login>>
    suspend fun updateUsers(users: List<Login>) : State<String>
    suspend fun updateAdminToken(id: String, token: String) : State<String>
}