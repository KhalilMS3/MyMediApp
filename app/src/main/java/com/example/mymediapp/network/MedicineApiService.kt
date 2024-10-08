package com.example.mymediapp.network

import com.example.mymediapp.model.MedicineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MedicineApiService {
    @GET("/legemidler/legemidler/sok")
    suspend fun searchMedicines(
        @Query("queryText") queryText: String,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): Response<List<MedicineResponse>>
}
