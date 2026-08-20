package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClockViewModel
import com.example.ui.StopwatchLap
import com.example.ui.components.CircularProgressTimer

@Composable
fun StopwatchScreen(
    viewModel: ClockViewModel,
    elapsedMillis: Long,
    isRunning: Boolean,
    laps: List<StopwatchLap>,
    modifier: Modifier = Modifier
) {
    val totalSeconds = elapsedMillis / 1000
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val millis = (elapsedMillis % 1000) / 10

    val timeString = String.format("%02d:%02d", minutes, seconds)
    val msString = String.format(".%02d", millis)

    // Calculate fastest and slowest laps
    val minLapTime = if (laps.size >= 2) laps.minOf { it.lapTimeMillis } else -1L
    val maxLapTime = if (laps.size >= 2) laps.maxOf { it.lapTimeMillis } else -1L

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stopwatch Dial & Large Display
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag("stopwatch_dial_box"),
            contentAlignment = Alignment.Center
        ) {
            val progressFraction = (elapsedMillis % 60000L) / 60000f
            CircularProgressTimer(
                progress = progressFraction,
                size = 230.dp,
                strokeWidth = 10.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                progressColor = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 44.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = msString,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = if (isRunning) "Running" else if (elapsedMillis > 0) "Stopped" else "Ready",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset / Lap Button
            if (isRunning) {
                FilledTonalButton(
                    onClick = { viewModel.recordLap() },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("stopwatch_lap_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Lap",
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { viewModel.resetStopwatch() },
                    enabled = elapsedMillis > 0,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            if (elapsedMillis > 0) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .testTag("stopwatch_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Stopwatch",
                        tint = if (elapsedMillis > 0) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Start / Stop Pill Button
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseStopwatch() else viewModel.startStopwatch()
                },
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(64.dp)
                    .width(150.dp)
                    .testTag("stopwatch_start_pause_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Stop" else "Start",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "STOP" else "START",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Laps Table Header & List
        if (laps.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lap #",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Split Time",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total Time",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(laps) { lap ->
                    val isFastest = minLapTime > 0 && lap.lapTimeMillis == minLapTime
                    val isSlowest = maxLapTime > 0 && lap.lapTimeMillis == maxLapTime

                    val badgeColor = when {
                        isFastest -> Color(0xFF10B981) // Emerald Green
                        isSlowest -> Color(0xFFF97316) // Vibrant Orange
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isFastest -> Color(0xFF10B981).copy(alpha = 0.12f)
                                isSlowest -> Color(0xFFF97316).copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("Lap %02d", lap.lapNumber),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = badgeColor
                                )
                                if (isFastest) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FASTEST",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                        color = Color(0xFF10B981)
                                    )
                                } else if (isSlowest) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SLOWEST",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                        color = Color(0xFFF97316)
                                    )
                                }
                            }

                            Text(
                                text = formatLapMillis(lap.lapTimeMillis),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = badgeColor
                            )

                            Text(
                                text = formatLapMillis(lap.totalTimeMillis),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatLapMillis(millisTotal: Long): String {
    val mins = (millisTotal / 60000)
    val secs = (millisTotal % 60000) / 1000
    val ms = (millisTotal % 1000) / 10
    return String.format("%02d:%02d.%02d", mins, secs, ms)
}
