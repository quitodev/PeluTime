package com.pelutime.domain.main.ubication

import android.net.Uri
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.application.State
import com.pelutime.data.local.main.ubication.UbicationLocalDataSource
import com.pelutime.data.remote.main.ubication.UbicationRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class UbicationImpl @Inject constructor (private val ubicationLocalDataSource: UbicationLocalDataSource,
                                         private val ubicationRemoteDataSource: UbicationRemoteDataSource) : IUbicationRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = ubicationLocalDataSource.getUserRoomDB()

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    override suspend fun getUbication(place: String) : State<Ubication> = ubicationRemoteDataSource.getUbicationDB(place)
    override suspend fun updateUbication(place: String, ubication: Ubication) : State<String> = ubicationRemoteDataSource.updateUbicationDB(place, ubication)
    override suspend fun updateUbicationImage(place: String, image: String) : State<String> = ubicationRemoteDataSource.updateUbicationImageDB(place, image)

    // FIREBASE STORAGE
    override suspend fun uploadImages(place: String, image: Uri, imageName: String) : State<String> = ubicationRemoteDataSource.uploadImagesDB(place, image, imageName)
    override suspend fun getImages(place: String, imageName: String) : State<String> = ubicationRemoteDataSource.getImagesDB(place, imageName)
}