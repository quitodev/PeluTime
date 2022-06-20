package com.pelutime.domain.main.ubication

import android.net.Uri
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.application.State
import com.pelutime.data.local.dao.UserEntity

interface IUbicationRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    suspend fun getUserRoom() : State<UserEntity>

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    suspend fun getUbication(place: String) : State<Ubication>
    suspend fun updateUbication(place: String, ubication: Ubication) : State<String>
    suspend fun updateUbicationImage(place: String, image: String) : State<String>

    // FIREBASE STORAGE
    suspend fun uploadImages(place: String, image: Uri, imageName: String) : State<String>
    suspend fun getImages(place: String, imageName: String) : State<String>
}