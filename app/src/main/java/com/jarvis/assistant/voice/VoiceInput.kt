package com.jarvis.assistant.voice

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.math.max

class VoiceInput(
    private val context: Context,
    var customWakePhrase: String = "hey jarvis",
    var earlyAccept: Boolean = false,
    var onMicProblem: (() -> Unit)? = null,
    var cancelCommand: (() -> Unit)? = null,
    var canTranscribe: Boolean = true,
    var listenWhileSpeaking: Boolean = false,
    var echoCancellation: Boolean = true,
    var wakeEnabled: Boolean = true
) {
    private val TAG = "VoiceInput"
    
    // JarvisService ને જે વેરિયેબલ્સ જોઈએ છે તે બધા પબ્લિક કરો
    var isListening: Boolean = false
        private set
    var muted: Boolean = false
    var partialText: String = ""
    var level: Float = 0f
    var listenForCommand: Boolean = false
    var hasWakeWord: Boolean = false
    var isStopCommand: Boolean = false

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private var audioRecord: AudioRecord? = null
    private var lastTriggerTime = 0L

    // અગાઉનું માઇક ગેઇન અને બફર ફિક્સ અહીં ઉમેર્યું છે
    fun start() {
        if (isListening) return
        isListening = true
        
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = max(minBuffer * 2, 4096) // Buffer Gap Fix

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate, channelConfig, audioFormat, bufferSize
        )

        try {
            audioRecord?.startRecording()
            val buffer = ShortArray(bufferSize)
            
            Thread {
                while (isListening) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0 && !muted) {
                        // અવાજ મોટો કરવાનું લોજિક
                        for (i in 0 until read) {
                            buffer[i] = (buffer[i] * 2.5f).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        
                        // Level અપડેટ કરો
                        level = calculateRMS(buffer).toFloat()
                        
                        // Sherpa-ONNX Wake Word લોજિક (સિમ્યુલેટેડ)
                        if (wakeEnabled && !listenForCommand) {
                            val currentTime = System.currentTimeMillis()
                            if (level > 1500 && (currentTime - lastTriggerTime > 2000)) {
                                lastTriggerTime = currentTime
                                hasWakeWord = true
                            }
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            Log.e(TAG, "Mic Error: ${e.message}")
            onMicProblem?.invoke()
        }
    }

    private fun calculateRMS(buffer: ShortArray): Double {
        var sum = 0.0
        for (i in buffer.indices) sum += buffer[i] * buffer[i]
        return kotlin.math.sqrt(sum / buffer.size)
    }

    fun stop() {
        isListening = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {}
        audioRecord = null
    }
}
