package com.example.mymediapp.model

import java.util.UUID

data class MealItem(
    val id: String = UUID.randomUUID().toString(),
    val meal: String = "",
    val calories: Int = 0,
    val date: String = "",
    val time: String = ""
)