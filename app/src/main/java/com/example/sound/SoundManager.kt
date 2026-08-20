package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class SoundPreset(
    val id: String,
    val name: String,
    val description: String,
    val frequencies: List<Double>
)

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingLoop = false
    private var loopJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val builtInTones: List<SoundPreset> = listOf(
        SoundPreset("radiant_chime", "Radiant Chime", "Gentle ascending harmonic chimes", listOf(523.25, 659.25, 783.99, 1046.50)),
        SoundPreset("morning_bell", "Gentle Morning Bell", "Warm resonant deep morning bell", listOf(440.0, 554.37, 659.25, 880.0)),
        SoundPreset("cyber_pulse", "Cyber Pulse", "Clean futuristic rhythmic alert", listOf(600.0, 750.0, 900.0, 1200.0)),
        SoundPreset("zen_bowl", "Zen Singing Bowl", "Calm soothing meditational sound", listOf(329.63, 493.88, 659.25)),
        SoundPreset("digital_beep", "Classic Digital Beep", "Crisp digital clock alarm", listOf(800.0, 1000.0, 800.0, 1000.0)),
        SoundPreset("cosmic_harp", "Cosmic Harp", "Soft pleasant acoustic triad", listOf(392.0, 493.88, 587.33, 783.99))
    )

    fun getDeviceRingtones(): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        runCatching {
            val manager = RingtoneManager(context)
            manager.setType(RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_NOTIFICATION or RingtoneManager.TYPE_RINGTONE)
            val cursor = manager.cursor
            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val uri = manager.getRingtoneUri(cursor.position).toString()
                list.add(Pair(title, uri))
            }
        }
        return list
    }

    fun playPreview(toneName: String, uriString: String = "") {
        stop()
        scope.launch {
            if (uriString.isNotEmpty()) {
                playUri(uriString, loop = false)
            } else {
                playSynthesizedPreset(toneName)
            }
        }
    }

    fun startAlarmAlert(toneName: String, uriString: String = "", shouldVibrate: Boolean = true) {
        stop()
        isPlayingLoop = true
        if (shouldVibrate) {
            startVibration()
        }

        loopJob = scope.launch {
            while (isActive && isPlayingLoop) {
                if (uriString.isNotEmpty()) {
                    playUri(uriString, loop = true)
                    break
                } else {
                    playSynthesizedPreset(toneName)
                    delay(1200)
                }
            }
        }
    }

    fun stop() {
        isPlayingLoop = false
        loopJob?.cancel()
        loopJob = null
        stopVibration()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    private fun startVibration() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 500, 200, 500, 400)
                val amplitudes = intArrayOf(0, 200, 0, 255, 0)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 200, 500), 0)
            }
        }
    }

    private fun stopVibration() {
        runCatching {
            vibrator?.cancel()
        }
    }

    private fun playUri(uriString: String, loop: Boolean) {
        try {
            val uri = Uri.parse(uriString)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(context, uri)
                isLooping = loop
                prepare()
                start()
            }
        } catch (e: Exception) {
            // Fallback to synthesis if URI playback fails
            scope.launch { playSynthesizedPreset("Radiant Chime") }
        }
    }

    private fun playSynthesizedPreset(presetName: String) {
        val preset = builtInTones.find { it.name.equals(presetName, ignoreCase = true) }
            ?: builtInTones.first()
        synthesizeChimes(preset.frequencies)
    }

    private fun synthesizeChimes(frequencies: List<Double>) {
        val sampleRate = 44100
        val noteDuration = 0.22 // seconds per note
        val totalSamplesPerNote = (sampleRate * noteDuration).toInt()
        val totalSamples = totalSamplesPerNote * frequencies.size
        val generatedSound = ShortArray(totalSamples)

        var sampleIndex = 0
        for (freq in frequencies) {
            for (i in 0 until totalSamplesPerNote) {
                val t = i.toDouble() / sampleRate
                // Harmonic richness + natural exponential decay envelope
                val envelope = kotlin.math.exp(-3.5 * (i.toDouble() / totalSamplesPerNote))
                val sampleVal = (0.7 * sin(2.0 * Math.PI * freq * t) +
                        0.2 * sin(4.0 * Math.PI * freq * t) +
                        0.1 * sin(6.0 * Math.PI * freq * t)) * envelope * 30000.0
                generatedSound[sampleIndex++] = sampleVal.toInt().coerceIn(-32767, 32767).toShort()
            }
        }

        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(totalSamples * 2.coerceAtLeast(minBufSize))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSound, 0, totalSamples)
            audioTrack.play()
            Thread.sleep((frequencies.size * noteDuration * 1000).toLong() + 100)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }
}
