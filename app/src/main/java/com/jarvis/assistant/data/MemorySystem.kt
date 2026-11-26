package com.jarvis.assistant.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MemorySystem(context: Context) : SQLiteOpenHelper(context, "JarvisMemory.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT, user_text TEXT, ai_text TEXT, time INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS history")
        onCreate(db)
    }

    // 3. Advanced Memory System
    fun saveConversation(user: String, ai: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_text", user)
            put("ai_text", ai)
            put("time", System.currentTimeMillis())
        }
        db.insert("history", null, values)
        db.close()
    }

    fun getRecentMemory(limit: Int = 5): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT user_text, ai_text FROM history ORDER BY time DESC LIMIT $limit", null)
        val sb = StringBuilder("Recent Context:\n")
        while (cursor.moveToNext()) {
            sb.append("User: ${cursor.getString(0)}\nAI: ${cursor.getString(1)}\n")
        }
        cursor.close()
        db.close()
        return sb.toString()
    }
}
