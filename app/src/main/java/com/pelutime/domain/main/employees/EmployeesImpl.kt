package com.pelutime.domain.main.employees

import android.net.Uri
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.application.State
import com.pelutime.data.local.main.employees.EmployeesLocalDataSource
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.data.remote.main.employees.EmployeesRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class EmployeesImpl @Inject constructor (private val employeesLocalDataSource: EmployeesLocalDataSource,
                                         private val employeesRemoteDataSource: EmployeesRemoteDataSource) : IEmployeesRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = employeesLocalDataSource.getUserRoomDB()

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    override suspend fun getEmployees(place: String) : Flow<State<List<Employees>>> = employeesRemoteDataSource.getEmployeesDB(place)
    override suspend fun getUbication(place: String) : State<Ubication> = employeesRemoteDataSource.getUbicationDB(place)
    override suspend fun setDocuments(place: String, mapSchedule: Map<String, List<String>>, employees: Employees, listDates: List<String>) = employeesRemoteDataSource.setDocumentsDB(place, mapSchedule, employees, listDates)
    override suspend fun updateEmployees(place: String, employees: Employees) : State<String> = employeesRemoteDataSource.updateEmployeesDB(place, employees)
    override suspend fun updateImageEmployees(place: String, document: String, image: String) : State<String> = employeesRemoteDataSource.updateImageEmployeesDB(place, document, image)
    override suspend fun setEmployees(place: String, employees: Employees) : State<String> = employeesRemoteDataSource.setEmployeesDB(place, employees)
    override suspend fun deleteEmployees(place: String, nameEmployee: String) : State<String> = employeesRemoteDataSource.deleteEmployeesDB(place, nameEmployee)

    // FIREBASE STORAGE
    override suspend fun getImages(place: String, imageName: String) : State<String> = employeesRemoteDataSource.getImagesDB(place, imageName)
    override suspend fun uploadImages(place: String, image: Uri, imageName: String) : State<String> = employeesRemoteDataSource.uploadImagesDB(place, image, imageName)
    override suspend fun deleteImages(place: String, imageName: String) : State<String> = employeesRemoteDataSource.deleteImagesDB(place, imageName)
}