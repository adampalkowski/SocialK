package com.example.socialk.model


data class Chat (
    val create_date:String,
    val owner_id:String,
    var id:String,
    var members:List<String>,
    var chat_name:String,
    var chat_picture:String,
    var recent_message_time: String,
    var recent_message:String,
    var type:String="duo",
    ){
    constructor(): this("", "","", listOf(),"","","","","")
}