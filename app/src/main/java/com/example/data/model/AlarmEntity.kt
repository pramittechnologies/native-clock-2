package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val hour: Int, // 0-23
    val minute: Int, // 0-59
    val isEnabled: Boolean = true,
    val label: String = "Alarm",
    val repeatDays: String = "", // Comma-separated: "1,2,3,4,5,6,7" (1=Sun, 2=Mon...) or "Once"
    val ringtoneName: String = "Radiant Chime",
    val ringtoneUri: String = "",
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)
