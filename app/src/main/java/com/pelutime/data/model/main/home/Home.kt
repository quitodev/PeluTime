package com.pelutime.data.model.main.home

data class HomeAdmin (
    var future: List<HomeFuture>,
    var pending: List<HomePending>,
    var rejected: List<HomeRejected>,
    var today: List<HomeToday>,
)

data class HomeUsers (
    var confirmed: List<HomeConfirmed>,
    var pending: List<HomePending>,
    var rejected: List<HomeRejected>,
    var free: List<HomeFree>,
)

data class HomeConfirmed (
    var experiences: String = "",
    var hobbies: String = "",
    var id: String = "",
    var image: String = "",
    var name: String = "",
    var schedule: String = "",
    var sections: String = "",
    var sectionSelected: String = ""
)

data class HomeFree (
    var experiences: String = "",
    var hobbies: String = "",
    var id: String = "",
    var image: String = "",
    var minutes: String = "",
    var name: String = "",
    var schedule: String = "",
    var sections: String = ""
)

data class HomeFuture (
    var id: String = "",
    var image: String = "",
    var nameEmployee: String = "",
    var nameUser: String = "",
    var schedule: String = "",
    var sectionSelected: String = ""
)

data class HomePending (
    var experiences: String = "",
    var hobbies: String = "",
    var id: String = "",
    var image: String = "",
    var nameEmployee: String = "",
    var nameUser: String = "",
    var schedule: String = "",
    var sections: String = "",
    var sectionSelected: String = ""
)

data class HomeRejected (
    var experiences: String = "",
    var hobbies: String = "",
    var id: String = "",
    var image: String = "",
    var nameEmployee: String = "",
    var nameUser: String = "",
    var schedule: String = "",
    var sections: String = "",
    var sectionSelected: String = ""
)

data class HomeToday (
    var id: String = "",
    var image: String = "",
    var nameEmployee: String = "",
    var nameUser: String = "",
    var schedule: String = "",
    var sectionSelected: String = ""
)