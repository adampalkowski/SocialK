package com.example.socialk.model

data class User(
    var name: String?,
    var username: String?,
    var email: String?,
    var id: String,
    var pictureUrl: String?
){
    constructor(): this(name="",username="",email="",id="",pictureUrl="")
}