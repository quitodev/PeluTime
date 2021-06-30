package com.pelutime.data.model.main.ubication

import com.google.firebase.firestore.GeoPoint

data class Ubication (
    var address: String = "",
    var admin: String = "",
    var geopoint: GeoPoint? = null,
    var image: String = "",
    var name: String = "",
    var phone: String = "",
    var place: String = "",
    var schedule: String = "",
    var website: String = ""
)