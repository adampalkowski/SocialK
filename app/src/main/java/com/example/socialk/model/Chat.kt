package com.example.socialk.model


data class Chat (
    val create_date:String,
    val owner_id:String,
    val id:String,
    var members:List<String>,
    var name:String,
    var recent_message:String,
    var type:String="duo",
    )