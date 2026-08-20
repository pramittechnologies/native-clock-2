package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmEntity
import com.example.ui.ClockViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    viewModel: ClockViewModel,
    alarms: List<AlarmEntity>,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    var editingAlarm by remember { mutableStateOf<AlarmEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    val activeAlarmsCount = alarms.count { it.isEnabled }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Hero Banner: Next Alarm Summary
            item {
                NextAlarmHeroBanner(alarms = alarms, is24Hour = is24Hour)
            }

            if (alarms.isEmpty()) {
                item {
                    EmptyAlarmState(onAddClick = {
                        editingAlarm = AlarmEntity(hour = 7, minute = 0, label = "Morning Alarm")
                        isCreatingNew = true
                    })
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        is24Hour = is24Hour,
                        onToggle = { viewModel.toggleAlarm(alarm) },
                        onEdit = {
                            editingAlarm = alarm
                            isCreatingNew = false
                        },
                        onDelete = { viewModel.deleteAlarm(alarm) },
                        onTestTrigger = { viewModel.triggerAlarmPreview(alarm) }
                    )
                }
            }
        }

        // Floating Action Button to Add Alarm
        FloatingActionButton(
            onClick = {
                val cal = Calendar.getInstance()
                editingAlarm = AlarmEntity(
                    hour = cal.get(Calendar.HOUR_OF_DAY),
                    minute = (cal.get(Calendar.MINUTE) + 5) % 60,
                    label = "Alarm"
                )
                isCreatingNew = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .testTag("add_alarm_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm", modifier = Modifier.size(28.dp))
        }
    }

    // Add / Edit Modal Sheet
    if (editingAlarm != null) {
        AlarmEditBottomSheet(
            initialAlarm = editingAlarm!!,
            isCreatingNew = isCreatingNew,
            is24Hour = is24Hour,
            viewModel = viewModel,
            onDismiss = { editingAlarm = null },
            onSave = { savedAlarm ->
                viewModel.saveAlarm(savedAlarm)
                editingAlarm = null
            }
        )
    }
}

@Composable
fun NextAlarmHeroBanner(alarms: List<AlarmEntity>, is24Hour: Boolean) {
    val enabledAlarms = alarms.filter { it.isEnabled }
    val nextText = if (enabledAlarms.isEmpty()) {
        "No active alarms scheduled"
    } else {
        val next = enabledAlarms.first()
        val formatted = formatAlarmTime(next.hour, next.minute, is24Hour)
        "Next alarm: $formatted (${next.label.ifBlank { "Alarm" }})"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Scheduled Alarms",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = nextText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmEntity,
    is24Hour: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestTrigger: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("alarm_card_${alarm.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val timePair = getAlarmDisplayPair(alarm.hour, alarm.minute, is24Hour)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = timePair.first,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = if (alarm.isEnabled)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        if (timePair.second.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timePair.second,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (alarm.isEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = alarm.label.ifBlank { "Alarm" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata row: Repeat Days, Ringtone, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val days = listOf("S", "M", "T", "W", "T", "F", "S")
                    val selectedDays = alarm.repeatDays.split(",").filter { it.isNotBlank() }

                    days.forEachIndexed { index, dayLetter ->
                        val dayIndexStr = (index + 1).toString()
                        val isDaySelected = selectedDays.contains(dayIndexStr) || alarm.repeatDays == "Everyday"
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDaySelected && alarm.isEnabled)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                    else
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLetter,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isDaySelected && alarm.isEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${alarm.ringtoneName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = onTestTrigger,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test Alarm Ringing",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Alarm",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
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
fun AlarmEditBottomSheet(
    initialAlarm: AlarmEntity,
    isCreatingNew: Boolean,
    is24Hour: Boolean,
    viewModel: ClockViewModel,
    onDismiss: () -> Unit,
    onSave: (AlarmEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hour by remember { mutableIntStateOf(initialAlarm.hour) }
    var minute by remember { mutableIntStateOf(initialAlarm.minute) }
    var label by remember { mutableStateOf(initialAlarm.label) }
    var vibrate by remember { mutableStateOf(initialAlarm.vibrate) }
    var snoozeMinutes by remember { mutableIntStateOf(initialAlarm.snoozeMinutes) }
    var selectedTone by remember { mutableStateOf(initialAlarm.ringtoneName) }
    var selectedUri by remember { mutableStateOf(initialAlarm.ringtoneUri) }
    var repeatDays by remember { mutableStateOf(initialAlarm.repeatDays) }

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
                text = if (isCreatingNew) "Create Alarm" else "Edit Alarm",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Time Selector Dial / Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour Adjuster
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { hour = if (hour >= 23) 0 else hour + 1 }) {
                        Text("▲", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = String.format("%02d", if (is24Hour) hour else if (hour % 12 == 0) 12 else hour % 12),
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { hour = if (hour <= 0) 23 else hour - 1 }) {
                        Text("▼", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = ":",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Minute Adjuster
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { minute = if (minute >= 59) 0 else minute + 1 }) {
                        Text("▲", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = String.format("%02d", minute),
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { minute = if (minute <= 0) 59 else minute - 1 }) {
                        Text("▼", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                if (!is24Hour) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val isAm = hour < 12
                        Surface(
                            onClick = { if (!isAm) hour -= 12 },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                "AM",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            onClick = { if (isAm) hour += 12 },
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                "PM",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (!isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Label text field
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Alarm Label") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Repeat Days Selector
            Text(
                text = "Repeat Days",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                val activeDays = repeatDays.split(",").toMutableSet()

                dayNames.forEachIndexed { index, name ->
                    val dayNum = (index + 1).toString()
                    val selected = activeDays.contains(dayNum)
                    Surface(
                        onClick = {
                            if (selected) activeDays.remove(dayNum) else activeDays.add(dayNum)
                            repeatDays = activeDays.filter { it.isNotBlank() }.joinToString(",")
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sound Tone Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sound: $selectedTone",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                OutlinedButton(
                    onClick = {
                        // Preview tone
                        viewModel.soundManager.playPreview(selectedTone, selectedUri)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Test tone", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Preview")
                }
            }

            // Quick tone chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                viewModel.soundManager.builtInTones.take(3).forEach { tone ->
                    val isToneSelected = selectedTone == tone.name
                    Surface(
                        onClick = {
                            selectedTone = tone.name
                            selectedUri = ""
                            viewModel.soundManager.playPreview(tone.name, "")
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isToneSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = tone.name.take(12),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToneSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vibrate Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vibration")
                }
                Switch(checked = vibrate, onCheckedChange = { vibrate = it })
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions: Cancel & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onSave(
                            initialAlarm.copy(
                                hour = hour,
                                minute = minute,
                                label = label,
                                vibrate = vibrate,
                                snoozeMinutes = snoozeMinutes,
                                ringtoneName = selectedTone,
                                ringtoneUri = selectedUri,
                                repeatDays = repeatDays,
                                isEnabled = true
                            )
                        )
                    },
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
fun EmptyAlarmState(onAddClick: () -> Unit) {
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
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Alarms Set",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tap the + button to create your first alarm",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Set Alarm")
        }
    }
}

fun formatAlarmTime(hour: Int, minute: Int, is24Hour: Boolean): String {
    val pair = getAlarmDisplayPair(hour, minute, is24Hour)
    return "${pair.first} ${pair.second}".trim()
}

fun getAlarmDisplayPair(hour: Int, minute: Int, is24Hour: Boolean): Pair<String, String> {
    return if (is24Hour) {
        Pair(String.format("%02d:%02d", hour, minute), "")
    } else {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        Pair(String.format("%02d:%02d", displayHour, minute), amPm)
    }
}
