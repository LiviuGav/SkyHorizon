package com.example.skyhorizon.managers

import com.example.skyhorizon.models.FavoriteCity
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    fun addFavoriteCity(city: FavoriteCity, uid: String, onComplete: (Boolean) -> Unit) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(city.id)
            .set(city)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getFavoriteCities(uid: String, onComplete: (List<FavoriteCity>) -> Unit) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(FavoriteCity::class.java) }
                onComplete(list)
            }
    }

    fun removeFavoriteCity(uid: String, cityId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(cityId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}