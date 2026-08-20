package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppDatabase
import com.example.data.model.AlarmEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.WorldCityEntity
import com.example.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUserPreferences(
    val themeMode: String = "DARK", // "SYSTEM", "LIGHT", "DARK"
    val colorTheme: AppColorTheme = AppColorTheme.GEOMETRIC,
    val is24Hour: Boolean = false,
    val showSeconds: Boolean = true,
    val defaultRingtone: String = "Radiant Chime",
    val defaultRingtoneUri: String = ""
)

class ClockRepository(private val database: AppDatabase, private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("clock_app_preferences", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences = _userPreferences.asStateFlow()

    // Alarms
    val allAlarms: Flow<List<AlarmEntity>> = database.alarmDao().getAllAlarms()
    suspend fun insertAlarm(alarm: AlarmEntity): Long = database.alarmDao().insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = database.alarmDao().updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = database.alarmDao().deleteAlarm(alarm)
    suspend fun deleteAlarmById(id: Long) = database.alarmDao().deleteAlarmById(id)

    // World Cities
    val allCities: Flow<List<WorldCityEntity>> = database.worldCityDao().getAllCities()
    suspend fun insertCity(city: WorldCityEntity): Long = database.worldCityDao().insertCity(city)
    suspend fun insertCities(cities: List<WorldCityEntity>) = database.worldCityDao().insertCities(cities)
    suspend fun updateCity(city: WorldCityEntity) = database.worldCityDao().updateCity(city)
    suspend fun deleteCity(city: WorldCityEntity) = database.worldCityDao().deleteCity(city)
    suspend fun deleteCityById(id: Long) = database.worldCityDao().deleteCityById(id)
    suspend fun getCityCount(): Int = database.worldCityDao().getCityCount()

    // Reminders
    val allReminders: Flow<List<ReminderEntity>> = database.reminderDao().getAllReminders()
    suspend fun insertReminder(reminder: ReminderEntity): Long = database.reminderDao().insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = database.reminderDao().updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = database.reminderDao().deleteReminder(reminder)
    suspend fun deleteReminderById(id: Long) = database.reminderDao().deleteReminderById(id)

    // User Preferences Management
    private fun loadPreferences(): AppUserPreferences {
        val theme = prefs.getString("theme_mode", "DARK") ?: "DARK"
        val colorName = prefs.getString("color_theme", AppColorTheme.GEOMETRIC.name) ?: AppColorTheme.GEOMETRIC.name
        val color = runCatching { AppColorTheme.valueOf(colorName) }.getOrDefault(AppColorTheme.GEOMETRIC)
        val is24 = prefs.getBoolean("is_24_hour", false)
        val seconds = prefs.getBoolean("show_seconds", true)
        val ringtone = prefs.getString("default_ringtone", "Radiant Chime") ?: "Radiant Chime"
        val ringtoneUri = prefs.getString("default_ringtone_uri", "") ?: ""
        return AppUserPreferences(
            themeMode = theme,
            colorTheme = color,
            is24Hour = is24,
            showSeconds = seconds,
            defaultRingtone = ringtone,
            defaultRingtoneUri = ringtoneUri
        )
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _userPreferences.value = _userPreferences.value.copy(themeMode = mode)
    }

    fun setColorTheme(theme: AppColorTheme) {
        prefs.edit().putString("color_theme", theme.name).apply()
        _userPreferences.value = _userPreferences.value.copy(colorTheme = theme)
    }

    fun setIs24Hour(is24: Boolean) {
        prefs.edit().putBoolean("is_24_hour", is24).apply()
        _userPreferences.value = _userPreferences.value.copy(is24Hour = is24)
    }

    fun setShowSeconds(show: Boolean) {
        prefs.edit().putBoolean("show_seconds", show).apply()
        _userPreferences.value = _userPreferences.value.copy(showSeconds = show)
    }

    fun setDefaultRingtone(name: String, uri: String) {
        prefs.edit().putString("default_ringtone", name).putString("default_ringtone_uri", uri).apply()
        _userPreferences.value = _userPreferences.value.copy(defaultRingtone = name, defaultRingtoneUri = uri)
    }
}
