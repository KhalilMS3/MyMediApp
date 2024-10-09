package com.example.mymediapp.model

data class MedicineResponse(
    val medicines: List<Medicine>,
    val navn: String,
    val navnFormStyrke: String?
)

data class Medicine(
    val varenavn: String,
    val navnFormStyrke: String,
    val varenummer: String
)
