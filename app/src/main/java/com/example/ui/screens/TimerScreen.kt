package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.ClockViewModel
import com.example.ui.components.CircularProgressTimer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: ClockViewModel,
    totalSeconds: Long,
    remainingSeconds: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    var isSetCustomTimeOpen by remember { mutableStateOf(false) }

    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val timeString = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Minimalist circular countdown timer
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("circular_timer_box"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressTimer(
                progress = progress,
                size = 260.dp,
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                progressColor = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = if (hours > 0) 38.sp else 48.sp,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Counting down" else "Paused / Ready",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = { isSetCustomTimeOpen = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Duration",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Set Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Preset Chips
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                val presets = listOf(
                    Pair("1 min", 60),
                    Pair("3 min", 180),
                    Pair("5 min", 300),
                    Pair("10 min", 600),
                    Pair("15 min", 900),
                    Pair("30 min", 1800),
                    Pair("45 min", 2700),
                    Pair("1 hr", 3600)
                )

                presets.forEach { (label, durationSecs) ->
                    Surface(
                        onClick = {
                            viewModel.setTimerDuration(durationSecs / 3600, (durationSecs % 3600) / 60, durationSecs % 60)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (totalSeconds == durationSecs.toLong())
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "+$label",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (totalSeconds == durationSecs.toLong())
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Controls: Reset, Start/Pause Pill, +1 Min
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("timer_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Main Play/Pause Pill Button
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(64.dp)
                    .width(150.dp)
                    .testTag("timer_play_pause_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "PAUSE" else "START",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // +1 Minute Button
            FilledTonalButton(
                onClick = { viewModel.addTimerSeconds(60) },
                shape = CircleShape,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("timer_add_1m_button")
            ) {
                Text("+1m", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }

    if (isSetCustomTimeOpen) {
        SetCustomTimerBottomSheet(
            currentTotalSeconds = totalSeconds,
            onDismiss = { isSetCustomTimeOpen = false },
            onConfirm = { h, m, s ->
                viewModel.setTimerDuration(h, m, s)
                isSetCustomTimeOpen = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetCustomTimerBottomSheet(
    currentTotalSeconds: Long,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hours by remember { mutableIntStateOf((currentTotalSeconds / 3600).toInt()) }
    var minutes by remember { mutableIntStateOf(((currentTotalSeconds % 3600) / 60).toInt()) }
    var seconds by remember { mutableIntStateOf((currentTotalSeconds % 60).toInt()) }

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
                text = "Set Timer Duration",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { hours = (hours + 1).coerceAtMost(99) }) { Text("▲", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text(text = String.format("%02d", hours), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black))
                    IconButton(onClick = { hours = (hours - 1).coerceAtLeast(0) }) { Text("▼", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text("Hours", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(":", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.primary)

                // Minutes
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { minutes = if (minutes >= 59) 0 else minutes + 1 }) { Text("▲", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text(text = String.format("%02d", minutes), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black))
                    IconButton(onClick = { minutes = if (minutes <= 0) 59 else minutes - 1 }) { Text("▼", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text("Minutes", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(":", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.primary)

                // Seconds
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { seconds = if (seconds >= 59) 0 else seconds + 1 }) { Text("▲", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text(text = String.format("%02d", seconds), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black))
                    IconButton(onClick = { seconds = if (seconds <= 0) 59 else seconds - 1 }) { Text("▼", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    Text("Seconds", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val total = (hours * 3600) + (minutes * 60) + seconds
                        if (total > 0) {
                            onConfirm(hours, minutes, seconds)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Set")
                }
            }
        }
    }
}
