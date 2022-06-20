package com.pelutime.domain.main.maps

import com.pelutime.data.local.dao.UserEntity
import com.pelutime.application.State
import com.pelutime.data.local.main.maps.MapsLocalDataSource
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.data.remote.main.maps.MapsRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class MapsImpl @Inject constructor (private val mapsLocalDataSource: MapsLocalDataSource,
                                    private val mapsRemoteDataSource: MapsRemoteDataSource) : IMapsRepo {

    /*
    <<<<<< LOCAL DATABASE >>>>>>
     */

    // ROOM DATABASE
    override suspend fun getUserRoom() = mapsLocalDataSource.getUserRoomDB()
    override suspend fun setUserRoom(userEntity: UserEntity) = mapsLocalDataSource.setUserRoomDB(userEntity)

    /*
    <<<<<< REMOTE DATABASE >>>>>>
     */

    // FIREBASE DATABASE
    override suspend fun getPlaces() : Flow<State<List<Ubication>>> = mapsRemoteDataSource.getPlacesDB()
    override suspend fun updateUserPlace(id: String, place: String) : State<String> = mapsRemoteDataSource.updateUserPlaceDB(id, place)
}