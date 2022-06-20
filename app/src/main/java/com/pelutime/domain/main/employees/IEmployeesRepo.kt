package com.pelutime.domain.main.employees

import android.net.Uri
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity
import com.pelutime.data.model.main.ubication.Ubication
import kotlinx.coroutines.flow.Flow

interface IEmployeesRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    suspend fun getEmployees(place: String) : Flow<State<List<Employees>>>
    suspend fun getUbication(place: String) : State<Ubication>
    suspend fun setDocuments(place: String, mapSchedule: Map<String, List<String>>, employees: Employees, listDates: List<String>)
    suspend fun updateEmployees(place: String, employees: Employees) : State<String>
    suspend fun updateImageEmployees(place: String, document: String, image: String) : State<String>
    suspend fun setEmployees(place: String, employees: Employees) : State<String>
    suspend fun deleteEmployees(place: String, nameEmployee: String) : State<String>

    // FIREBASE STORAGE
    suspend fun getImages(place: String, imageName: String) : State<String>
    suspend fun uploadImages(place: String, image: Uri, imageName: String) : State<String>
    suspend fun deleteImages(place: String, imageName: String) : State<String>
}