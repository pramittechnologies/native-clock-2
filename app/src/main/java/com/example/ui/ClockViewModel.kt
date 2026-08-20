package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.AlarmEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderSoundType
import com.example.data.model.WorldCityEntity
import com.example.data.repository.AppUserPreferences
import com.example.data.repository.ClockRepository
import com.example.sound.SoundManager
import com.example.sound.VoiceAssistantManager
import com.example.ui.theme.AppColorTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

data class StopwatchLap(
    val lapNumber: Int,
    val lapTimeMillis: Long,
    val totalTimeMillis: Long
)

enum class NavigationTab(val title: String) {
    ALARM("Alarm"),
    WORLD_CLOCK("World Clock"),
    TIMER("Timer"),
    STOPWATCH("Stopwatch"),
    REMINDERS("Reminders")
}

data class ActiveAlertState(
    val isOpen: Boolean = false,
    val title: String = "",
    val message: String = "",
    val timeFormatted: String = "",
    val isReminder: Boolean = false,
    val isAiVoice: Boolean = false,
    val soundName: String = "",
    val soundUri: String = ""
)

class ClockViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClockRepository
    val soundManager: SoundManager = SoundManager(application)
    val voiceAssistant: VoiceAssistantManager = VoiceAssistantManager(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(NavigationTab.ALARM)
    val currentTab = _currentTab.asStateFlow()

    // Preferences & Settings
    val userPreferences: StateFlow<AppUserPreferences>

    // Settings Bottom Sheet Open/Close
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen = _isSettingsOpen.asStateFlow()

    // About Dialog Open/Close
    private val _isAboutOpen = MutableStateFlow(false)
    val isAboutOpen = _isAboutOpen.asStateFlow()

    // Active Triggering Alert (Alarm/Reminder/Timer Dialog)
    private val _activeAlert = MutableStateFlow(ActiveAlertState())
    val activeAlert = _activeAlert.asStateFlow()

    // Live Current Time Ticker (updates every 1000ms)
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis = _currentTimeMillis.asStateFlow()

    // Alarms
    val alarms: StateFlow<List<AlarmEntity>>

    // World Clock Cities
    val worldCities: StateFlow<List<WorldCityEntity>>

    // Reminders
    val reminders: StateFlow<List<ReminderEntity>>

    // ================= TIMER STATE =================
    private val _timerTotalSeconds = MutableStateFlow(300L) // default 5 minutes
    val timerTotalSeconds = _timerTotalSeconds.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(300L)
    val timerRemainingSeconds = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // ================= STOPWATCH STATE =================
    private val _stopwatchElapsedMillis = MutableStateFlow(0L)
    val stopwatchElapsedMillis = _stopwatchElapsedMillis.asStateFlow()

    private val _isStopwatchRunning = MutableStateFlow(false)
    val isStopwatchRunning = _isStopwatchRunning.asStateFlow()

    private val _stopwatchLaps = MutableStateFlow<List<StopwatchLap>>(emptyList())
    val stopwatchLaps = _stopwatchLaps.asStateFlow()

    private var stopwatchJob: Job? = null
    private var stopwatchStartTimestamp: Long = 0L
    private var stopwatchAccumulatedBeforeStart: Long = 0L

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ClockRepository(db, application)
        userPreferences = repository.userPreferences

        alarms = repository.allAlarms.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        worldCities = repository.allCities.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        reminders = repository.allReminders.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Populate initial default alarms and cities if first launch
        viewModelScope.launch {
            initDefaultDataIfEmpty()
        }

        // Start live ticker
        viewModelScope.launch {
            while (isActive) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    private suspend fun initDefaultDataIfEmpty() {
        if (repository.getCityCount() == 0) {
            val defaultCities = listOf(
                WorldCityEntity(cityName = "London", countryName = "United Kingdom", timeZoneId = "Europe/London", isPinned = true, sortOrder = 1),
                WorldCityEntity(cityName = "New York", countryName = "United States", timeZoneId = "America/New_York", isPinned = true, sortOrder = 2),
                WorldCityEntity(cityName = "Tokyo", countryName = "Japan", timeZoneId = "Asia/Tokyo", isPinned = true, sortOrder = 3),
                WorldCityEntity(cityName = "Paris", countryName = "France", timeZoneId = "Europe/Paris", isPinned = false, sortOrder = 4),
                WorldCityEntity(cityName = "Sydney", countryName = "Australia", timeZoneId = "Australia/Sydney", isPinned = false, sortOrder = 5),
                WorldCityEntity(cityName = "Dubai", countryName = "United Arab Emirates", timeZoneId = "Asia/Dubai", isPinned = false, sortOrder = 6),
                WorldCityEntity(cityName = "Singapore", countryName = "Singapore", timeZoneId = "Asia/Singapore", isPinned = false, sortOrder = 7)
            )
            repository.insertCities(defaultCities)
        }
    }

    // Navigation
    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun openAbout() {
        _isAboutOpen.value = true
    }

    fun closeAbout() {
        _isAboutOpen.value = false
    }

    // Settings actions
    fun setThemeMode(mode: String) = repository.setThemeMode(mode)
    fun setColorTheme(theme: AppColorTheme) = repository.setColorTheme(theme)
    fun setIs24Hour(is24: Boolean) = repository.setIs24Hour(is24)
    fun setShowSeconds(show: Boolean) = repository.setShowSeconds(show)
    fun setDefaultRingtone(name: String, uri: String) = repository.setDefaultRingtone(name, uri)

    // ================= ALARMS OPERATIONS =================
    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.id == 0L) {
                repository.insertAlarm(alarm)
            } else {
                repository.updateAlarm(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }

    fun triggerAlarmPreview(alarm: AlarmEntity) {
        val timeFormatted = String.format("%02d:%02d", alarm.hour, alarm.minute)
        _activeAlert.value = ActiveAlertState(
            isOpen = true,
            title = alarm.label.ifBlank { "Alarm" },
            message = "Wake up! Alarm is ringing.",
            timeFormatted = timeFormatted,
            isReminder = false,
            isAiVoice = false,
            soundName = alarm.ringtoneName,
            soundUri = alarm.ringtoneUri
        )
        soundManager.startAlarmAlert(alarm.ringtoneName, alarm.ringtoneUri, alarm.vibrate)
    }

    // ================= WORLD CLOCK OPERATIONS =================
    fun addWorldCity(cityName: String, countryName: String, timeZoneId: String) {
        viewModelScope.launch {
            repository.insertCity(
                WorldCityEntity(
                    cityName = cityName,
                    countryName = countryName,
                    timeZoneId = timeZoneId,
                    isPinned = false
                )
            )
        }
    }

    fun togglePinCity(city: WorldCityEntity) {
        viewModelScope.launch {
            repository.updateCity(city.copy(isPinned = !city.isPinned))
        }
    }

    fun deleteWorldCity(city: WorldCityEntity) {
        viewModelScope.launch {
            repository.deleteCity(city)
        }
    }

    // ================= TIMER OPERATIONS =================
    fun setTimerDuration(hours: Int, minutes: Int, seconds: Int) {
        val total = (hours * 3600L) + (minutes * 60L) + seconds
        if (total > 0) {
            _timerTotalSeconds.value = total
            _timerRemainingSeconds.value = total
            if (_isTimerRunning.value) {
                pauseTimer()
            }
        }
    }

    fun addTimerSeconds(secondsToAdd: Long) {
        val newRemaining = _timerRemainingSeconds.value + secondsToAdd
        val newTotal = maxOf(_timerTotalSeconds.value, newRemaining)
        _timerRemainingSeconds.value = newRemaining
        _timerTotalSeconds.value = newTotal
    }

    fun startTimer() {
        if (_timerRemainingSeconds.value <= 0) {
            _timerRemainingSeconds.value = _timerTotalSeconds.value
        }
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _timerRemainingSeconds.value > 0) {
                delay(1000)
                _timerRemainingSeconds.value -= 1
            }
            if (_timerRemainingSeconds.value <= 0) {
                _isTimerRunning.value = false
                // Timer finished alert!
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerRemainingSeconds.value = _timerTotalSeconds.value
    }

    private fun onTimerFinished() {
        _activeAlert.value = ActiveAlertState(
            isOpen = true,
            title = "Timer Finished",
            message = "Your countdown timer has elapsed!",
            timeFormatted = "00:00",
            isReminder = false,
            isAiVoice = false,
            soundName = userPreferences.value.defaultRingtone,
            soundUri = userPreferences.value.defaultRingtoneUri
        )
        soundManager.startAlarmAlert(userPreferences.value.defaultRingtone, userPreferences.value.defaultRingtoneUri, true)
    }

    // ================= STOPWATCH OPERATIONS =================
    fun startStopwatch() {
        if (!_isStopwatchRunning.value) {
            _isStopwatchRunning.value = true
            stopwatchStartTimestamp = System.currentTimeMillis()
            stopwatchAccumulatedBeforeStart = _stopwatchElapsedMillis.value

            stopwatchJob = viewModelScope.launch {
                while (isActive && _isStopwatchRunning.value) {
                    val currentNow = System.currentTimeMillis()
                    _stopwatchElapsedMillis.value = stopwatchAccumulatedBeforeStart + (currentNow - stopwatchStartTimestamp)
                    delay(16) // ~60fps smooth ticker
                }
            }
        }
    }

    fun pauseStopwatch() {
        if (_isStopwatchRunning.value) {
            _isStopwatchRunning.value = false
            stopwatchJob?.cancel()
            stopwatchJob = null
            stopwatchAccumulatedBeforeStart = _stopwatchElapsedMillis.value
        }
    }

    fun resetStopwatch() {
        pauseStopwatch()
        _stopwatchElapsedMillis.value = 0L
        stopwatchAccumulatedBeforeStart = 0L
        _stopwatchLaps.value = emptyList()
    }

    fun recordLap() {
        if (_isStopwatchRunning.value || _stopwatchElapsedMillis.value > 0) {
            val total = _stopwatchElapsedMillis.value
            val previousLapTotal = _stopwatchLaps.value.firstOrNull()?.totalTimeMillis ?: 0L
            val lapDuration = total - previousLapTotal
            val newLap = StopwatchLap(
                lapNumber = _stopwatchLaps.value.size + 1,
                lapTimeMillis = lapDuration,
                totalTimeMillis = total
            )
            _stopwatchLaps.value = listOf(newLap) + _stopwatchLaps.value
        }
    }

    // ================= REMINDERS OPERATIONS =================
    fun saveReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            if (reminder.id == 0L) {
                repository.insertReminder(reminder)
            } else {
                repository.updateReminder(reminder)
            }
        }
    }

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun testReminderVoice(title: String, timeFormatted: String, description: String = "") {
        voiceAssistant.speakReminder(title, timeFormatted, description)
    }

    fun triggerReminderAlert(reminder: ReminderEntity) {
        val cal = Calendar.getInstance().apply { timeInMillis = reminder.scheduledTimeMillis }
        val timeFormatted = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        val isAiVoice = reminder.soundType == ReminderSoundType.AI_VOICE

        _activeAlert.value = ActiveAlertState(
            isOpen = true,
            title = reminder.title,
            message = reminder.description.ifBlank { "Scheduled reminder alert." },
            timeFormatted = timeFormatted,
            isReminder = true,
            isAiVoice = isAiVoice,
            soundName = reminder.ringtoneName,
            soundUri = reminder.ringtoneUri
        )

        if (isAiVoice) {
            voiceAssistant.speakReminder(reminder.title, timeFormatted, reminder.description)
        } else {
            soundManager.startAlarmAlert(reminder.ringtoneName, reminder.ringtoneUri, true)
        }
    }

    fun dismissActiveAlert() {
        soundManager.stop()
        voiceAssistant.stopSpeaking()
        _activeAlert.value = ActiveAlertState(isOpen = false)
    }

    fun snoozeActiveAlert(minutes: Int = 5) {
        soundManager.stop()
        voiceAssistant.stopSpeaking()
        _activeAlert.value = ActiveAlertState(isOpen = false)
        // Add timer countdown for snooze
        setTimerDuration(0, minutes, 0)
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.stop()
        voiceAssistant.shutdown()
        timerJob?.cancel()
        stopwatchJob?.cancel()
    }
}
