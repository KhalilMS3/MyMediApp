package com.example.mymediapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.model.MedicineResponse
import com.example.mymediapp.network.MedicineApiService
import com.example.mymediapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel : ViewModel() {

    private val _medicineResults = MutableLiveData<List<MedicineResponse>>()
    val medicineResults: LiveData<List<MedicineResponse>> = _medicineResults

    private val apiService: MedicineApiService = RetrofitInstance.createService(MedicineApiService::class.java)

    fun searchMedicines(queryText: String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.searchMedicines(queryText, apiKey)
                if (response.isSuccessful) {
                    _medicineResults.postValue(response.body() ?: emptyList())
                } else {
                    _medicineResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                _medicineResults.postValue(emptyList())
            }
        }
    }
}
