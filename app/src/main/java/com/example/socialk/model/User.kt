package com.example.socialk.model

data class User(
    var name: String?,
    var username: String?,
    var email: String?,
    var id: String,
    var pictureUrl: String?,
    var blocked_ids: ArrayList<String>,
    var friends_ids: HashMap<String,String>,
    var invited_ids: ArrayList<String>,
){
    constructor(): this(name="",username="",email="",id="",pictureUrl="",blocked_ids= ArrayList(),friends_ids= HashMap(),invited_ids= ArrayList())
}