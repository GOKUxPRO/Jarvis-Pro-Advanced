package com.jarvis.assistant.agent

object StopManager {
    @Volatile var isStopped = false
    
    fun stop() { isStopped = true }
    fun reset() { isStopped = false }
}
