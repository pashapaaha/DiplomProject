package com.example.paaha.findyourfriend.model

object FriendInfoList {

    private val list =  mutableListOf<String>()

    fun add(friend: String){
        list.add(friend)
    }

    fun clear(){
        list.clear()
    }

    fun contains(item: String) = list.contains(item)
}