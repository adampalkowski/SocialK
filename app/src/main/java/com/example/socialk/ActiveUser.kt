package com.example.socialk

data class ActiveUser(
    val id: String,
    val creator_id:String,
    val pictureUrl: String,
    val username: String,
    val location: String?,
    val time_length: String,
    val time_start: String,
    val time_end: String
){
    constructor(): this(id="",creator_id="",pictureUrl="",username="",location="",time_length="",time_start="",time_end="")

}