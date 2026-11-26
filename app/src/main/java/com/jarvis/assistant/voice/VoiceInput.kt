package com.jarvis.assistant.voice

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.math.max

class VoiceInput {
    private val TAG = "VoiceInput"
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private var isListening = false
    
    // રીલ્સ ટ્રિગર અટકાવવા માટે Threshold વધાર્યો (0.25 થી 0.65)
    private val wakeWordThreshold = 0.65f 
    private var lastTriggerTime = 0L

    fun startListening(onAudioCaptured: (ShortArray) -> Unit, onWakeWordDetected: () -> Unit) {
        isListening = true
        
        // FIX: Buffer Size મોટું કર્યું જેથી અવાજ વચ્ચે ગેપ ના પડે
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = max(minBuffer * 2, 4096)

        // FIX: MIC ને બદલે VOICE_RECOGNITION વાપર્યું જેથી બેકગ્રાઉન્ડ નોઈઝ ઓછો થાય
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
                    // સોફ્ટવેર ગેઇન લગાવો જેથી તમારો અવાજ મોટો થાય
                    val gainedBuffer = MicGain.applyGain(buffer)
                    
                    // STT માટે ઓડિયો મોકલો
                    onAudioCaptured(gainedBuffer)

                    // Wake Word ડિટેક્શન લોજિક
                    val rms = calculateRMS(gainedBuffer)
                    val currentTime = System.currentTimeMillis()

                    // જો અવાજ પૂરતો મોટો હોય (RMS > 1500) અને કૂલડાઉન પૂરું થયું હોય
                    if (rms > 1500 && (currentTime - lastTriggerTime > 2000)) {
                        // અહીં Sherpa-ONNX નું અસલી ચેક આવશે (અત્યારે સિમ્યુલેટેડ છે)
                        val isWakeWord = checkSherpaModel(gainedBuffer)
                        
                        if (isWakeWord) {
                            Log.i(TAG, "Wake Word Detected! RMS: $rms")
                            lastTriggerTime = currentTime
                            onWakeWordDetected()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecord Error: ${e.message}")
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }
    }

    private fun calculateRMS(buffer: ShortArray): Double {
        var sum = 0.0
        for (i in buffer.indices) sum += buffer[i] * buffer[i]
        return kotlin.math.sqrt(sum / buffer.size)
    }

    private fun checkSherpaModel(buffer: ShortArray): Boolean {
        // TODO: અહીં તમારો Sherpa-ONNX નો કોડ આવશે.
        // ઉદાહરણ: return sherpaKeywordSpotter.process(buffer) > wakeWordThreshold
        return true 
    }

    fun stopListening() { isListening = false }
}
