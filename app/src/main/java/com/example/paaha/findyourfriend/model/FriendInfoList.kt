package com.example.paaha.findyourfriend.model

object FriendInfoList {

    private val list = mutableListOf<FriendInfo>()

    fun add(friend: FriendInfo) {
        list.add(friend)
    }

    fun clear() {
        list.clear()
    }

    fun contains(item: String) = list.map { it.friend }.contains(item)

    fun getList() = list.toMutableList()
}