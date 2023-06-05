package com.example.connect.bean

class UserBean(var uid: String = "", val name: String, val gender: String, val dob: String) {
    constructor() : this("", "", "", "")

    val imgUrl: String = ""
    val friendsRequestsReceived = mutableMapOf<String, Boolean>()
    val friendsRequestsSent = mutableMapOf<String, Boolean>()
    val friends = mutableMapOf<String, Boolean>()

    override fun toString(): String {
        return "UserBean(uid='$uid', name='$name', gender='$gender', dob='$dob')"
    }

}
