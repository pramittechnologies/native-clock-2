package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.WorldCityEntity
import com.example.ui.ClockViewModel
import com.example.ui.components.AnalogClockCanvas
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WorldCityPreset(
    val cityName: String,
    val countryName: String,
    val timeZoneId: String
)

val WORLD_CITY_PRESETS = listOf(
    WorldCityPreset("London", "United Kingdom", "Europe/London"),
    WorldCityPreset("New York", "United States", "America/New_York"),
    WorldCityPreset("Tokyo", "Japan", "Asia/Tokyo"),
    WorldCityPreset("Paris", "France", "Europe/Paris"),
    WorldCityPreset("Sydney", "Australia", "Australia/Sydney"),
    WorldCityPreset("Dubai", "United Arab Emirates", "Asia/Dubai"),
    WorldCityPreset("Singapore", "Singapore", "Asia/Singapore"),
    WorldCityPreset("Hong Kong", "China", "Asia/Hong_Kong"),
    WorldCityPreset("Los Angeles", "United States", "America/Los_Angeles"),
    WorldCityPreset("Chicago", "United States", "America/Chicago"),
    WorldCityPreset("San Francisco", "United States", "America/Los_Angeles"),
    WorldCityPreset("Toronto", "Canada", "America/Toronto"),
    WorldCityPreset("Seoul", "South Korea", "Asia/Seoul"),
    WorldCityPreset("Berlin", "Germany", "Europe/Berlin"),
    WorldCityPreset("Rome", "Italy", "Europe/Rome"),
    WorldCityPreset("Madrid", "Spain", "Europe/Madrid"),
    WorldCityPreset("Amsterdam", "Netherlands", "Europe/Amsterdam"),
    WorldCityPreset("Zurich", "Switzerland", "Europe/Zurich"),
    WorldCityPreset("Mumbai", "India", "Asia/Kolkata"),
    WorldCityPreset("New Delhi", "India", "Asia/Kolkata"),
    WorldCityPreset("Bangkok", "Thailand", "Asia/Bangkok"),
    WorldCityPreset("Cairo", "Egypt", "Africa/Cairo"),
    WorldCityPreset("Johannesburg", "South Africa", "Africa/Johannesburg"),
    WorldCityPreset("Rio de Janeiro", "Brazil", "America/Sao_Paulo"),
    WorldCityPreset("Buenos Aires", "Argentina", "America/Argentina/Buenos_Aires"),
    WorldCityPreset("Auckland", "New Zealand", "Pacific/Auckland"),
    WorldCityPreset("Honolulu", "United States", "Pacific/Honolulu"),
    WorldCityPreset("Mexico City", "Mexico", "America/Mexico_City"),
    WorldCityPreset("Stockholm", "Sweden", "Europe/Stockholm"),
    WorldCityPreset("Istanbul", "Turkey", "Europe/Istanbul")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(
    viewModel: ClockViewModel,
    cities: List<WorldCityEntity>,
    currentTimeMillis: Long,
    is24Hour: Boolean,
    showSeconds: Boolean,
    modifier: Modifier = Modifier
) {
    var isAddCityOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Local Clock Hero Card
            item {
                LocalClockHeroCard(
                    currentTimeMillis = currentTimeMillis,
                    is24Hour = is24Hour,
                    showSeconds = showSeconds
                )
            }

            // World Clock List
            items(cities, key = { it.id }) { city ->
                WorldCityCard(
                    city = city,
                    currentTimeMillis = currentTimeMillis,
                    is24Hour = is24Hour,
                    showSeconds = showSeconds,
                    onTogglePin = { viewModel.togglePinCity(city) },
                    onDelete = { viewModel.deleteWorldCity(city) }
                )
            }
        }

        // Add City FAB
        FloatingActionButton(
            onClick = { isAddCityOpen = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .testTag("add_city_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add City", modifier = Modifier.size(28.dp))
        }
    }

    if (isAddCityOpen) {
        AddCityBottomSheet(
            existingCities = cities,
            onDismiss = { isAddCityOpen = false },
            onCitySelected = { preset ->
                viewModel.addWorldCity(preset.cityName, preset.countryName, preset.timeZoneId)
                isAddCityOpen = false
            }
        )
    }
}

@Composable
fun LocalClockHeroCard(
    currentTimeMillis: Long,
    is24Hour: Boolean,
    showSeconds: Boolean
) {
    val localTz = TimeZone.getDefault()
    val localTime = getCityFormattedTime(currentTimeMillis, localTz.id, is24Hour, showSeconds)
    val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(currentTimeMillis))

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Local Time",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${localTz.displayName} (${localTz.id})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "HOME",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Analog Clock Visualization
            AnalogClockCanvas(
                timeMillis = currentTimeMillis,
                timeZoneId = localTz.id,
                size = 140.dp,
                showSeconds = showSeconds
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = localTime,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WorldCityCard(
    city: WorldCityEntity,
    currentTimeMillis: Long,
    is24Hour: Boolean,
    showSeconds: Boolean,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    val cityTime = getCityFormattedTime(currentTimeMillis, city.timeZoneId, is24Hour, showSeconds)
    val offsetDiff = getCityOffsetDiffString(city.timeZoneId)
    val dayDiff = getCityDayDifference(currentTimeMillis, city.timeZoneId)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (city.isPinned)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("city_card_${city.cityName.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Analog Clock
            AnalogClockCanvas(
                timeMillis = currentTimeMillis,
                timeZoneId = city.timeZoneId,
                size = 56.dp,
                showSeconds = showSeconds
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = city.cityName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (city.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "${city.countryName} • $offsetDiff",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = cityTime,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (dayDiff.isNotBlank()) {
                    Text(
                        text = dayDiff,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onTogglePin, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (city.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin City",
                    tint = if (city.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete City",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityBottomSheet(
    existingCities: List<WorldCityEntity>,
    onDismiss: () -> Unit,
    onCitySelected: (WorldCityPreset) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredPresets = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            WORLD_CITY_PRESETS
        } else {
            WORLD_CITY_PRESETS.filter {
                it.cityName.contains(searchQuery, ignoreCase = true) ||
                        it.countryName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add World City",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search city or country...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredPresets) { preset ->
                    val isAlreadyAdded = existingCities.any { it.cityName.equals(preset.cityName, ignoreCase = true) }
                    Surface(
                        onClick = { if (!isAlreadyAdded) onCitySelected(preset) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isAlreadyAdded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = preset.cityName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isAlreadyAdded) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = preset.countryName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isAlreadyAdded) {
                                Text(
                                    text = "Added",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCityFormattedTime(currentTimeMillis: Long, timeZoneId: String, is24Hour: Boolean, showSeconds: Boolean): String {
    val sdf = SimpleDateFormat(
        when {
            is24Hour && showSeconds -> "HH:mm:ss"
            is24Hour -> "HH:mm"
            showSeconds -> "h:mm:ss a"
            else -> "h:mm a"
        },
        Locale.getDefault()
    )
    sdf.timeZone = TimeZone.getTimeZone(timeZoneId)
    return sdf.format(Date(currentTimeMillis))
}

fun getCityOffsetDiffString(timeZoneId: String): String {
    val localTz = TimeZone.getDefault()
    val targetTz = TimeZone.getTimeZone(timeZoneId)
    val now = System.currentTimeMillis()
    val diffMillis = targetTz.getOffset(now) - localTz.getOffset(now)
    val hours = diffMillis / (1000 * 60 * 60)
    val minutes = Math.abs((diffMillis / (1000 * 60)) % 60)

    return when {
        diffMillis == 0 -> "Same as local"
        hours > 0 && minutes > 0 -> "+${hours}h ${minutes}m"
        hours > 0 -> "+${hours}h"
        hours < 0 && minutes > 0 -> "${hours}h ${minutes}m"
        else -> "${hours}h"
    }
}

fun getCityDayDifference(currentTimeMillis: Long, timeZoneId: String): String {
    val localCal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val targetCal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).apply { timeInMillis = currentTimeMillis }

    val localDay = localCal.get(Calendar.DAY_OF_YEAR)
    val targetDay = targetCal.get(Calendar.DAY_OF_YEAR)

    return when {
        targetDay > localDay -> "Tomorrow"
        targetDay < localDay -> "Yesterday"
        else -> ""
    }
}
