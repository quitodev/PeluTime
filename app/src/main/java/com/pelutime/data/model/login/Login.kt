package com.pelutime.data.model.login

data class Login (
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var pass: String = "",
    var image: String = "",
    var place: String = "",
    var token: String = "",
    var bookings: Int = 0,
    var rejections: Int = 0
)