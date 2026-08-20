package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ClockViewModel
import com.example.ui.NavigationTab
import com.example.ui.components.PillBottomDock
import com.example.ui.components.TopPillHeader
import com.example.ui.screens.ActiveAlarmDialog
import com.example.ui.screens.AlarmScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SettingsSheet
import com.example.ui.screens.StopwatchScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.screens.WorldClockScreen
import com.example.ui.theme.ClockAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ClockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
            val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
            val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()

            val alarms by viewModel.alarms.collectAsStateWithLifecycle()
            val worldCities by viewModel.worldCities.collectAsStateWithLifecycle()
            val reminders by viewModel.reminders.collectAsStateWithLifecycle()

            val timerTotal by viewModel.timerTotalSeconds.collectAsStateWithLifecycle()
            val timerRemaining by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
            val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

            val stopwatchElapsed by viewModel.stopwatchElapsedMillis.collectAsStateWithLifecycle()
            val isStopwatchRunning by viewModel.isStopwatchRunning.collectAsStateWithLifecycle()
            val stopwatchLaps by viewModel.stopwatchLaps.collectAsStateWithLifecycle()

            ClockAppTheme(
                themeMode = userPrefs.themeMode,
                colorTheme = userPrefs.colorTheme
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top Header with Pill Hamburger on Right
                            TopPillHeader(
                                currentTab = currentTab,
                                currentTimeMillis = currentTimeMillis,
                                showSeconds = userPrefs.showSeconds,
                                is24Hour = userPrefs.is24Hour,
                                onMenuClick = { viewModel.openSettings() }
                            )

                            // Main Content Area with animated tab switching
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "main_screen_tab_switch"
                                ) { targetTab ->
                                    when (targetTab) {
                                        NavigationTab.ALARM -> AlarmScreen(
                                            viewModel = viewModel,
                                            alarms = alarms,
                                            is24Hour = userPrefs.is24Hour
                                        )
                                        NavigationTab.WORLD_CLOCK -> WorldClockScreen(
                                            viewModel = viewModel,
                                            cities = worldCities,
                                            currentTimeMillis = currentTimeMillis,
                                            is24Hour = userPrefs.is24Hour,
                                            showSeconds = userPrefs.showSeconds
                                        )
                                        NavigationTab.TIMER -> TimerScreen(
                                            viewModel = viewModel,
                                            totalSeconds = timerTotal,
                                            remainingSeconds = timerRemaining,
                                            isRunning = isTimerRunning
                                        )
                                        NavigationTab.STOPWATCH -> StopwatchScreen(
                                            viewModel = viewModel,
                                            elapsedMillis = stopwatchElapsed,
                                            isRunning = isStopwatchRunning,
                                            laps = stopwatchLaps
                                        )
                                        NavigationTab.REMINDERS -> RemindersScreen(
                                            viewModel = viewModel,
                                            reminders = reminders,
                                            is24Hour = userPrefs.is24Hour
                                        )
                                    }
                                }
                            }
                        }

                        // Floating Bottom Pill Dock
                        PillBottomDock(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.setTab(it) },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )

                        // Settings Bottom Sheet (Hamburger Pill Destination)
                        if (isSettingsOpen) {
                            SettingsSheet(
                                viewModel = viewModel,
                                preferences = userPrefs,
                                onDismiss = { viewModel.closeSettings() }
                            )
                        }

                        // Active Alert Triggering Dialog (Alarm / Reminder / Timer)
                        ActiveAlarmDialog(
                            state = activeAlert,
                            onDismiss = { viewModel.dismissActiveAlert() },
                            onSnooze = { mins -> viewModel.snoozeActiveAlert(mins) }
                        )
                    }
                }
            }
        }
    }
}
