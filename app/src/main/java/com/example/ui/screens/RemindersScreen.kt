package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderSoundType
import com.example.ui.ClockViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: ClockViewModel,
    reminders: List<ReminderEntity>,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf("UPCOMING") } // ALL, UPCOMING, COMPLETED
    var editingReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    val filteredList = when (filterTab) {
        "UPCOMING" -> reminders.filter { !it.isCompleted }
        "COMPLETED" -> reminders.filter { it.isCompleted }
        else -> reminders
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info & Voice info banner
            item {
                RemindersVoiceInfoBanner(
                    onTestSampleVoice = {
                        viewModel.testReminderVoice(
                            title = "Project Review and Sync",
                            timeFormatted = "10:30 AM",
                            description = "Prepare presentation slides and review key metrics."
                        )
                    }
                )
            }

            // Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("UPCOMING" to "Upcoming", "ALL" to "All", "COMPLETED" to "Done").forEach { (key, label) ->
                        val isSelected = filterTab == key
                        Surface(
                            onClick = { filterTab = key },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    EmptyRemindersState(
                        filter = filterTab,
                        onAddClick = {
                            val defaultCal = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 2) }
                            editingReminder = ReminderEntity(
                                title = "Important Task",
                                scheduledTimeMillis = defaultCal.timeInMillis,
                                soundType = ReminderSoundType.AI_VOICE
                            )
                            isCreatingNew = true
                        }
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        is24Hour = is24Hour,
                        onToggleComplete = { viewModel.toggleReminderCompleted(reminder) },
                        onEdit = {
                            editingReminder = reminder
                            isCreatingNew = false
                        },
                        onDelete = { viewModel.deleteReminder(reminder) },
                        onTriggerAlert = { viewModel.triggerReminderAlert(reminder) },
                        onTestSound = {
                            val timeStr = formatReminderTime(reminder.scheduledTimeMillis, is24Hour)
                            if (reminder.soundType == ReminderSoundType.AI_VOICE) {
                                viewModel.testReminderVoice(reminder.title, timeStr, reminder.description)
                            } else {
                                viewModel.soundManager.playPreview(reminder.ringtoneName, reminder.ringtoneUri)
                            }
                        }
                    )
                }
            }
        }

        // Add Reminder FAB
        FloatingActionButton(
            onClick = {
                val defaultCal = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }
                editingReminder = ReminderEntity(
                    title = "",
                    scheduledTimeMillis = defaultCal.timeInMillis,
                    soundType = ReminderSoundType.AI_VOICE
                )
                isCreatingNew = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .testTag("add_reminder_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reminder", modifier = Modifier.size(28.dp))
        }
    }

    if (editingReminder != null) {
        ReminderEditBottomSheet(
            initialReminder = editingReminder!!,
            isCreatingNew = isCreatingNew,
            is24Hour = is24Hour,
            viewModel = viewModel,
            onDismiss = { editingReminder = null },
            onSave = { savedReminder ->
                viewModel.saveReminder(savedReminder)
                editingReminder = null
            }
        )
    }
}

@Composable
fun RemindersVoiceInfoBanner(onTestSampleVoice: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Spoken Voice Reminders",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Speaks the reminder title & time aloud so you never miss it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onTestSampleVoice,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderEntity,
    is24Hour: Boolean,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTriggerAlert: () -> Unit,
    onTestSound: () -> Unit
) {
    val dateStr = formatReminderDateTime(reminder.scheduledTimeMillis, is24Hour)
    val isAiVoice = reminder.soundType == ReminderSoundType.AI_VOICE

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("reminder_card_${reminder.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox / Complete Button
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (reminder.isCompleted) "Mark Incomplete" else "Mark Complete",
                        tint = if (reminder.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null
                        ),
                        color = if (reminder.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    if (reminder.description.isNotBlank()) {
                        Text(
                            text = reminder.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                // Priority Badge
                if (reminder.priority == "HIGH") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "HIGH",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer metadata row: Time, Sound Mode badge, Test button, Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Scheduled Date Time Chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Sound Option Badge (AI Voice vs Ringtone)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isAiVoice) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAiVoice) Icons.Default.Mic else Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isAiVoice) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAiVoice) "AI Voice" else "Ringtone",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isAiVoice) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onTestSound, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test Audio/Voice",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Reminder",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditBottomSheet(
    initialReminder: ReminderEntity,
    isCreatingNew: Boolean,
    is24Hour: Boolean,
    viewModel: ClockViewModel,
    onDismiss: () -> Unit,
    onSave: (ReminderEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(initialReminder.title) }
    var description by remember { mutableStateOf(initialReminder.description) }
    var scheduledMillis by remember { mutableLongStateOf(initialReminder.scheduledTimeMillis) }
    var soundType by remember { mutableStateOf(initialReminder.soundType) }
    var priority by remember { mutableStateOf(initialReminder.priority) }
    var category by remember { mutableStateOf(initialReminder.category) }
    var ringtoneName by remember { mutableStateOf(initialReminder.ringtoneName) }

    val cal = remember(scheduledMillis) {
        Calendar.getInstance().apply { timeInMillis = scheduledMillis }
    }
    var hour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(cal.get(Calendar.MINUTE)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCreatingNew) "New Important Reminder" else "Edit Reminder",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Reminder Title / What to do") },
                placeholder = { Text("e.g., Attend Marketing Sync, Take Medication...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Details / Note (Optional)") },
                placeholder = { Text("Additional notes or context...") },
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Selector
            Text(
                text = "Scheduled Time: ${formatReminderTime(cal.timeInMillis, is24Hour)}",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("In 15 min", 15),
                    Pair("In 30 min", 30),
                    Pair("In 1 hr", 60),
                    Pair("In 3 hrs", 180),
                    Pair("Tomorrow", 1440)
                ).forEach { (label, mins) ->
                    Surface(
                        onClick = {
                            val newCal = Calendar.getInstance().apply { add(Calendar.MINUTE, mins) }
                            scheduledMillis = newCal.timeInMillis
                            hour = newCal.get(Calendar.HOUR_OF_DAY)
                            minute = newCal.get(Calendar.MINUTE)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sound Options (User Requested): Normal Ringtone or AI Voice
            Text(
                text = "Alert Sound Mode",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // AI Voice Option
                Card(
                    onClick = { soundType = ReminderSoundType.AI_VOICE },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (soundType == ReminderSoundType.AI_VOICE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (soundType == ReminderSoundType.AI_VOICE) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Voice",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (soundType == ReminderSoundType.AI_VOICE) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Speaks title & time aloud",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (soundType == ReminderSoundType.AI_VOICE) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                // Normal Ringtone Option
                Card(
                    onClick = { soundType = ReminderSoundType.RINGTONE },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (soundType == ReminderSoundType.RINGTONE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (soundType == ReminderSoundType.RINGTONE) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ringtone",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (soundType == ReminderSoundType.RINGTONE) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Plays musical alert tone",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (soundType == ReminderSoundType.RINGTONE) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preview Sound / Voice Button
            OutlinedButton(
                onClick = {
                    val formattedTime = formatReminderTime(scheduledMillis, is24Hour)
                    val reminderHeading = title.ifBlank { "Important scheduled event" }
                    if (soundType == ReminderSoundType.AI_VOICE) {
                        viewModel.testReminderVoice(reminderHeading, formattedTime, description)
                    } else {
                        viewModel.soundManager.playPreview(ringtoneName, "")
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (soundType == ReminderSoundType.AI_VOICE) "Preview AI Spoken Speech" else "Preview Ringtone Sound")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save / Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(
                                initialReminder.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    scheduledTimeMillis = scheduledMillis,
                                    soundType = soundType,
                                    priority = priority,
                                    category = category,
                                    ringtoneName = ringtoneName
                                )
                            )
                        }
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun EmptyRemindersState(filter: String, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No $filter Reminders",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Create reminders with AI Voice or ringtones to stay on track",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Create Reminder")
        }
    }
}

fun formatReminderDateTime(timeMillis: Long, is24Hour: Boolean): String {
    val pattern = if (is24Hour) "MMM d, HH:mm" else "MMM d, h:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
}

fun formatReminderTime(timeMillis: Long, is24Hour: Boolean): String {
    val pattern = if (is24Hour) "HH:mm" else "h:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
}
