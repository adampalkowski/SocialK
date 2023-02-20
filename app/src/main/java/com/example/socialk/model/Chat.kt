package com.example.socialk.model

import java.io.Serializable


data class Chat (
    var create_date:String?,
    val owner_id:String,
    var id:String?,
    var members:List<String>,
    var chat_name:String?,
    var chat_picture:String?,
    var recent_message_time: String?,
    var recent_message:String?,
    var type:String="duo",
    var user_one_username:String?,
    var user_one_profile_pic:String?,
    var user_two_username:String?,
    var user_two_profile_pic:String?,
    var highlited_message:String?
    ): Serializable {
    constructor(): this("", "","", listOf(),"","","","","",
    user_one_username="",user_one_profile_pic="",highlited_message="",user_two_profile_pic="",user_two_username="")
}