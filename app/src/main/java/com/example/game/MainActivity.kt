package com.example.game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//main screen
class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager // Pref-manager manage preferences
    private lateinit var highScoreTextView: TextView // highs-core textview

    // when activity created start function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_main) // Set the layout file for this activity

        // initialize PrefManager
        prefManager = PrefManager(this)

        // find and set up high score TextView
        highScoreTextView = findViewById(R.id.highScoreTextView)
        updateHighScoreText() // Update the high score text view

        // start game button
        val startgame: Button = findViewById(R.id.startgame)
        startgame.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent) // Start the Game activity
        }

        // reset high score button
        val resetHighScoreButton: Button = findViewById(R.id.resetHighScoreButton)
        resetHighScoreButton.setOnClickListener {
            resetHighScore() // Reset the high score
        }
    }

    // update the text of the high score
    private fun updateHighScoreText() {
        val highScore = prefManager.getHighScore()
        highScoreTextView.text = "High Score: $highScore"
    }

    // reset the high score
    private fun resetHighScore() {
        prefManager.saveHighScore(0) // Reset the high score
        updateHighScoreText()
    }
}
