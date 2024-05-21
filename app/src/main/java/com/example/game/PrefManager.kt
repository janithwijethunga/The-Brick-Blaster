package com.example.game

import android.content.Context

// manage high score preferences
class PrefManager(context: Context) {

    private val pref = context.getSharedPreferences("HighScorePref", Context.MODE_PRIVATE)

    // function to save the high score
    fun saveHighScore(highScore: Int) {
        pref.edit().putInt("HighScore", highScore).apply() // Save the high score to SharedPreferences
    }

    // function to retrieve the high score
    fun getHighScore(): Int {
        return pref.getInt("HighScore", 0) // Return the high score, defaulting to 0 if not found
    }
}
