package com.jarvis.assistant.voice

import kotlin.math.min

object MicGain {
    // આ ફંક્શન માઇકનો અવાજ મોટો કરશે (Software Gain)
    fun applyGain(buffer: ShortArray, gainFactor: Float = 2.5f): ShortArray {
        for (i in buffer.indices) {
            val amplified = (buffer[i] * gainFactor).toInt()
            // જેથી અવાજ ક્રેક ના થાય (Max limit 32767)
            buffer[i] = min(amplified, Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
