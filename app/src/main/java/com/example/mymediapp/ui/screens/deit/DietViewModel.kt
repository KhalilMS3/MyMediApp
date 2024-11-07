package com.example.mymediapp.ui.screens.deit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymediapp.model.MealItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

class DietViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _mealItems = MutableLiveData<List<MealItem>>(emptyList())
    val mealItems: LiveData<List<MealItem>> get() = _mealItems

    // LiveData for total calories per day
    private val _caloriesPerDay = MutableLiveData<Map<String, Int>>()
    val caloriesPerDay: LiveData<Map<String, Int>> get() = _caloriesPerDay


    private var listenerRegistration: ListenerRegistration? = null

    init {
        fetchMealItems()
    }

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"
    }


    private fun getMealCollection() = db.collection("users")
        .document(getUserId())
        .collection("meals")

    private fun fetchMealItems() {
        getMealCollection()
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    //Dilbrin Nav: Handle error
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val meals = snapshots.documents.mapNotNull { it.toObject(MealItem::class.java) }
                    _mealItems.postValue(meals)
                }
            }
    }

    fun addMealItem(mealItem: MealItem) {
        getMealCollection()
            .document(mealItem.id)
            .set(mealItem)
            .addOnSuccessListener {
                // Dilbrin Nav:  Meal item added successfully
            }
            .addOnFailureListener { e ->
                //Dilbrin Nav: Handle error
            }
    }

    fun deleteMealItem(mealItemId: String) {
        getMealCollection()
            .document(mealItemId)
            .delete()
            .addOnSuccessListener {
                //Dilbrin Nav: Meal item deleted successfully
            }
            .addOnFailureListener { e ->
                //Dilbrin Nav: Handle error
            }
    }

    fun updateMealItem(mealItem: MealItem) {
        getMealCollection()
            .document(mealItem.id)
            .set(mealItem)
            .addOnSuccessListener {
                // Optionally notify success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    // Calculate total calories per day
    private fun calculateCaloriesPerDay(meals: List<MealItem>) {
        val caloriesMap = meals.groupingBy { it.date }.fold(0) { acc, meal -> acc + meal.calories }
        _caloriesPerDay.postValue(caloriesMap)
    }

   /* override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }*/


}
