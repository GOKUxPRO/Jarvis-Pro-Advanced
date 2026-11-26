package com.jarvis.assistant.voice

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.math.max

class WakeWordEngine {
    private val TAG = "WakeWordEngine"
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private var isListening = false
    private var lastTriggerTime = 0L

    // FIX 1: Larger buffer to prevent audio gaps
    private fun getOptimalBufferSize(): Int {
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        return max(minBuffer * 2, 4096) 
    }

    fun startListening(onWakeWordDetected: () -> Unit) {
        isListening = true
        val bufferSize = getOptimalBufferSize()
        
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate, channelConfig, audioFormat, bufferSize
        )
        
        try {
            audioRecord.startRecording()
            val buffer = ShortArray(bufferSize)
            
            while (isListening) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    // RMS Calculation for diagnostics
                    var sum = 0.0
                    for (i in 0 until read) { sum += buffer[i] * buffer[i] }
                    val rms = kotlin.math.sqrt(sum / read)
                    
                    // FIX 2: Threshold Tuning (0.45f stops Insta Reels, RMS > 1000 stops silence)
                    val keywordsThreshold = 0.45f 
                    val hasVoiceEnergy = rms > 1000.0 
                    
                    // Simulated Sherpa-ONNX score (Replace with actual Sherpa API)
                    val score = 0.0f // e.g., sherpaResult.score
                    
                    Log.d(TAG, "RMS: $rms | Score: $score | Threshold: $keywordsThreshold")
                    
                    val currentTime = System.currentTimeMillis()
                    // FIX 3: Cooldown logic to prevent double triggers
                    if (hasVoiceEnergy && score >= keywordsThreshold && (currentTime - lastTriggerTime > 1500)) {
                        lastTriggerTime = currentTime
                        Log.i(TAG, "Wake Word Detected!")
                        onWakeWordDetected()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecord error: ${e.message}")
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }
    }
    
    fun stopListening() { isListening = false }
}
