package com.jarvis.assistant.voice

import kotlin.math.sqrt

/**
 * MicGain — auto-generated to match VoiceInput.kt call sites.
 *
 * Used by VoiceInput.kt:
 *   micGain.process(samples, learn = waiting)  → Float (RMS after noise-gate)
 *   MicGain.rms(floatArray)                    → Float (companion static)
 */
class MicGain(
    var gain: Float = 1.0f,
    var noiseFloor: Float = 0.01f
) {
    /**
     * Process a frame of float samples in [-1, 1] range.
     * If [learn] is true, updates the internal noise-floor estimate
     * (only while waiting for the wake word).
     * Returns the gated RMS level. Gain is applied by caller.
     */
    fun process(samples: FloatArray, learn: Boolean = false): Float {
        val rmsLevel = rms(samples)
        if (learn) {
            // Exponential moving average for adaptive noise floor
            noiseFloor = 0.99f * noiseFloor + 0.01f * rmsLevel
        }
        return (rmsLevel - noiseFloor).coerceAtLeast(0f)
    }

    companion object {
        fun rms(x: FloatArray): Float {
            if (x.isEmpty()) return 0f
            var s = 0.0
            for (v in x) s += v.toDouble() * v
            return sqrt(s / x.size).toFloat()
        }
        fun rms(x: ShortArray): Float {
            if (x.isEmpty()) return 0f
            var s = 0.0
            for (v in x) s += v.toDouble() * v
            return sqrt(s / x.size).toFloat()
        }
        fun rms(x: ByteArray): Float {
            if (x.isEmpty()) return 0f
            var s = 0.0
            for (v in x) s += v.toDouble() * v
            return sqrt(s / x.size).toFloat()
        }
    }
}
