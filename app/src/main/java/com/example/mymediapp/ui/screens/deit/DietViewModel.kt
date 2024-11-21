package com.example.mymediapp.ui.screens.deit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymediapp.model.MealItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class DietViewModel(private val db: FirebaseFirestore, private val auth: FirebaseAuth) : ViewModel(){


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
        return auth.currentUser?.uid ?: run {
            Log.e("DietViewModel", "User ID is null. Using defaultUserId.")
            "defaultUserId"
        }
    }


    private fun getMealCollection() = db.collection("users")
        .document(getUserId())
        .collection("meals")

    private fun fetchMealItems() {
        getMealCollection()
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    //: Handle error
                    Log.e("DietViewModel", "Error fetching meal items: ${e.message}", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val meals = snapshots.documents.mapNotNull { it.toObject(MealItem::class.java) }
                    _mealItems.postValue(meals)
                    // Calculate calories when new data is fetched
                    calculateCaloriesPerDay(meals)
                } else {
                    Log.w("DietViewModel", "Snapshot is null.")

                }
            }
    }

    fun addMealItem(mealItem: MealItem) {
        getMealCollection()
            .document(mealItem.id)
            .set(mealItem)
            .addOnSuccessListener {
                Log.d("DietViewModel", "Meal item added successfully: ${mealItem.id}")
            }
            .addOnFailureListener { e ->
                Log.e("DietViewModel", "Error adding meal item: ${e.message}", e)

            }
    }

    fun deleteMealItem(mealItemId: String) {
        getMealCollection()
            .document(mealItemId)
            .delete()
            .addOnSuccessListener {
                Log.d("DietViewModel", "Meal item deleted successfully: $mealItemId")
            }
            .addOnFailureListener { e ->
                Log.e("DietViewModel", "Error deleting meal item: ${e.message}", e)

            }
    }

    fun updateMealItem(mealItem: MealItem) {
        getMealCollection()
            .document(mealItem.id)
            .set(mealItem)
            .addOnSuccessListener {
                // Optionally notify success
                Log.d("DietViewModel", "Meal item updated successfully: ${mealItem.id}")
            }
            .addOnFailureListener { e ->
                Log.e("DietViewModel", "Error updating meal item: ${e.message}", e)
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
