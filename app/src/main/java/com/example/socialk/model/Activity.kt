package com.example.socialk.model



data class Activity(
    val id: String,
    val title: String,
    val date: String,
    val start_time: String,
    val time_length: String,
    val creator_id:String,
    val description:String,
    val creator_username:String,
    val creator_name:String,
    val creator_profile_picture:String,
    var time_left:String,
    var end_time:String,
    var latLng:String,
    val minUserCount:Int,
    val maxUserCount:Int,
    val disableChat:Boolean,
    val disableMemories:Boolean,
    val likes:Int,

):java.io.Serializable{
    constructor(): this("", "","", "","", "","",
        "","","", "","","", 0,100,false,false,0)
}