package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReminderSoundType {
    AI_VOICE,
    RINGTONE
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val scheduledTimeMillis: Long,
    val soundType: ReminderSoundType = ReminderSoundType.AI_VOICE,
    val ringtoneName: String = "Radiant Chime",
    val ringtoneUri: String = "",
    val isCompleted: Boolean = false,
    val category: String = "Important",
    val priority: String = "NORMAL", // LOW, NORMAL, HIGH
    val createdAt: Long = System.currentTimeMillis()
)
