package com.example.socialk

data class ActiveUser(
    val id: String,
    val creator_id:String,
    val pictureUrl: String,
    val username: String,
    val location: String?,
    val time_length: String,
    val time_start: String,
    val time_end: String,
    val create_time:String,
    val destroy_time:String,
    val invited_users:ArrayList<String>,
){
    constructor(): this(id="",creator_id="",pictureUrl="",username="",location="",time_length="",time_start="",time_end="",create_time="",destroy_time="",ArrayList())

}