package fi.oulu.hshi.reminderapp.entity

import java.util.*

data class Reminder(
    var key: String = "",
    var message: String = "",
    var location_x: Double = 0.0,
    var location_y: Double = 0.0,
    var reminder_time: String = "",
    var creation_time: String = "",
    var image_uri: String = "" ,
    var reminder_see: Boolean = false
)

data class User (
    var username: String = "",
    var email: String = "",
    var password: String = ""
)




