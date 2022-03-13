package com.example.handsonchatapp

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}