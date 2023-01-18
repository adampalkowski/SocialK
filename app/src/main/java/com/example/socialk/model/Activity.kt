package com.example.socialk.model



data class Activity(
    val id: String,
    val title: String,
    val date: String,
    val start_time: String,
    val time_length: String,
    val creator_id:String
){
    constructor(): this("", "","", "","", "")
}