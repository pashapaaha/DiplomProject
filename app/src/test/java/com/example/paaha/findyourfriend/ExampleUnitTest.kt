package com.example.paaha.findyourfriend

import com.example.paaha.findyourfriend.model.FriendInfoList
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Ignore
    private fun fillFriends(){
        FriendInfoList.add("friend1")
        FriendInfoList.add("friend2")
        FriendInfoList.add("friend3")
        FriendInfoList.add("friend4")
    }

    @Test
    fun existingString() {
        fillFriends()
        assertEquals(
            FriendInfoList.contains("friend2"),
            true
        )
    }
    @Test
    fun notExistingString() {
        fillFriends()
        assertEquals(
            FriendInfoList.contains("wrongFriend"),
            false
        )
    }
    @Test
    fun emptyString() {
        fillFriends()
        assertEquals(
            FriendInfoList.contains(""),
            false
        )
    }
}
