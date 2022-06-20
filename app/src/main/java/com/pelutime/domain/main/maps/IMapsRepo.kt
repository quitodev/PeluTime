package com.pelutime.domain.main.maps

import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.model.main.ubication.Ubication
import kotlinx.coroutines.flow.Flow

interface IMapsRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>
    suspend fun setUserRoom(userEntity: UserEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    suspend fun getPlaces() : Flow<State<List<Ubication>>>
    suspend fun updateUserPlace(id: String, place: String) : State<String>
}