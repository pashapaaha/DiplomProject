package com.example.paaha.findyourfriend.logic

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

open class ValueEventAdapter : ValueEventListener {
    override fun onCancelled(error: DatabaseError) {}
    override fun onDataChange(snapshot: DataSnapshot) {}
}