package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_cities")
data class WorldCityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cityName: String,
    val countryName: String,
    val timeZoneId: String,
    val isPinned: Boolean = false,
    val sortOrder: Int = 0
)
