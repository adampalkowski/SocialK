package com.example.socialk.model

data class ChatMessage (
    val text: String,
    var sender_picture_url:String,
    var sent_time:String,
    val sender_id:String,
    var message_type:String="text",
    var id :String
){

    constructor(): this("", "","", "","","")
}
