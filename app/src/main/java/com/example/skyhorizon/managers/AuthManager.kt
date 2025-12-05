package com.example.skyhorizon.managers

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    fun currentUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}