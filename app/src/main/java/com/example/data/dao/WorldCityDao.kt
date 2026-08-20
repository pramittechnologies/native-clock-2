package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WorldCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldCityDao {
    @Query("SELECT * FROM world_cities ORDER BY isPinned DESC, sortOrder ASC, cityName ASC")
    fun getAllCities(): Flow<List<WorldCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: WorldCityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<WorldCityEntity>)

    @Update
    suspend fun updateCity(city: WorldCityEntity)

    @Delete
    suspend fun deleteCity(city: WorldCityEntity)

    @Query("DELETE FROM world_cities WHERE id = :id")
    suspend fun deleteCityById(id: Long)

    @Query("SELECT COUNT(*) FROM world_cities")
    suspend fun getCityCount(): Int
}
