package com.test.github.database

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class History @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
    private val key = "search_history"
    private val maxSize = 5

    private var searches: MutableList<String> = loadHistory()

    fun add(search: String) {
        searches.remove(search)
        searches.add(0, search)

        if (searches.size > maxSize) {
            searches.removeAt(maxSize)
        }

        saveHistory()
    }

    fun getAll(): List<String> = searches.toList()

    private fun saveHistory() {
        val byteStream = ByteArrayOutputStream()
        ObjectOutputStream(byteStream).use { it.writeObject(searches) }
        val encoded = byteStream.toByteArray().joinToString(",") { it.toString() }
        prefs.edit().putString(key, encoded).apply()
    }

    private fun loadHistory(): MutableList<String> {
        val encoded = prefs.getString(key, null) ?: return mutableListOf()
        val byteArray = encoded.split(",").mapNotNull { it.toByteOrNull() }.toByteArray()

        return try {
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use { input ->
                    @Suppress("UNCHECKED_CAST")
                    input.readObject() as? MutableList<String> ?: mutableListOf()
                }
            }
        } catch (e: Exception) {
            mutableListOf()
        }
    }

}