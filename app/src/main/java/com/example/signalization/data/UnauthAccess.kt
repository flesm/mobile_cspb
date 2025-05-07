package com.example.signalization.data

data class UnauthAccess(
    val id: Int,
    val distance: Float,
    var is_decided: Boolean,
    val created_at: String,
    val photo_base64: String?
){
    override fun toString(): String {
        return "ID: $id\nCreated at: $created_at\nDistance: $distance\nIs decided: $is_decided\n"
    }
}