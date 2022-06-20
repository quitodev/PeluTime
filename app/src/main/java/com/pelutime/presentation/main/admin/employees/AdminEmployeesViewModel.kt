package com.pelutime.presentation.main.admin.employees

import android.net.Uri
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.core.BaseViewModel
import com.pelutime.domain.main.employees.IEmployeesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class AdminEmployeesViewModel @Inject constructor(private val iEmployeesRepo: IEmployeesRepo): BaseViewModel() {

    val liveGetEmployees = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iEmployeesRepo.getUserRoom().mapSuccess { user ->
                iEmployeesRepo.getEmployees(user.place).collect {
                    emit(it)
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveSetEmployees(employees: Employees) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iEmployeesRepo.getUserRoom().mapSuccess { user ->
                var nameUsed = false
                iEmployeesRepo.getEmployees(user.place).collect { resultEmployees ->
                    resultEmployees.mapSuccess { employeesAll ->
                        for (employee in employeesAll) {
                            if (employee.name == employees.name) {
                                nameUsed = true
                                emit(State.Success("Nombre en uso"))
                            }
                        }
                        if (!nameUsed) {
                            iEmployeesRepo.setEmployees(user.place, employees).mapSuccess {
                                val mapDays = mapOf<String, Map<String, Map<String, String>>>()
                                val listDates = getDatesToCreate(mapDays)

                                iEmployeesRepo.getUbication(user.place).mapSuccess { ubication ->
                                    val mapSchedule = getScheduleByPlace(ubication.schedule)
                                    iEmployeesRepo.setDocuments(user.place, mapSchedule, employees, listDates)
                                    emit(State.Success("Documentos creados con éxito"))
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

    fun liveUpdateEmployees(employees: Employees) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iEmployeesRepo.getUserRoom().mapSuccess { user ->
                emit(iEmployeesRepo.updateEmployees(user.place, employees))
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveUpdateImage(image: Uri, imageName: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iEmployeesRepo.getUserRoom().mapSuccess { user ->
                iEmployeesRepo.uploadImages(user.place, image, imageName).mapSuccess {
                    iEmployeesRepo.getImages(user.place, imageName).mapSuccess { url ->
                        iEmployeesRepo.updateImageEmployees(user.place, imageName, url).mapSuccess {
                            emit(State.Success(url))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(State.Failure(e))
        }
    }

    fun liveDeleteEmployees(employee: String) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(State.Loading())
        try {
            iEmployeesRepo.getUserRoom().mapSuccess { user ->
                iEmployeesRepo.getEmployees(user.place).collect { resultEmployees ->
                    resultEmployees.mapSuccess { employees ->
                        employees.forEach {
                            if (it.name == employee) {
                                if (it.image != "Vacío") {
                                    iEmployeesRepo.deleteImages(user.place, employee).mapSuccess {
                                        emit(iEmployeesRepo.deleteEmployees(user.place, employee))
                                    }
                                } else {
                                    emit(iEmployeesRepo.deleteEmployees(user.place, employee))
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
}