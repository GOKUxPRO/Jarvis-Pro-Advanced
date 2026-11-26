package com.jarvis.assistant.agent

import android.content.Context
import android.content.Intent
import android.net.Uri

object WebSearchManager {
    fun performSearch(context: Context, query: String): String {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            "માફ કરશો બોસ, AI થી જવાબ ના મળ્યો તેથી મેં Google પર '$query' સર્ચ કર્યું છે. પરિણામ સ્ક્રીન પર જોઈ શકો છો."
        } catch (e: Exception) {
            "Search error: ${e.message}"
        }
    }
}
