package com.jarvis.assistant.voice

/**
 * Auto-generated MicGain to fix VoiceInput.kt unresolved references.
 * Adjust formulas as needed by your audio pipeline.
 */
class MicGain(
    var gain: Float = 1.0f,
    var noiseFloor: Float = 0.001f
) {
    fun process(x: Float): Float = (x - noiseFloor) * gain
    fun process(x: Double): Double = (x - noiseFloor) * gain
    fun process(samples: ShortArray): ShortArray = ShortArray(samples.size) { i ->
        ((samples[i].toFloat() - noiseFloor) * gain).coerceIn(-32768f, 32767f).toInt().toShort()
    }

    fun rms(x: Float): Float = kotlin.math.abs(x)
    fun rms(x: Double): Float = kotlin.math.abs(x).toFloat()
    fun rms(samples: ShortArray): Float {
        if (samples.isEmpty()) return 0f
        var sum = 0.0
        for (s in samples) sum += s.toDouble() * s
        return kotlin.math.sqrt(sum / samples.size).toFloat()
    }
}
