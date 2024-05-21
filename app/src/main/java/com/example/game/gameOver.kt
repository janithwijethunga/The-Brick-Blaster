package com.example.game

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// gameOver activity to handle game over screen
class gameOver : AppCompatActivity() {
    private lateinit var prefManager: PrefManager

    // function called when activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enable edge-to-edge display
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_over)

        // apply padding to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize PrefManager
        prefManager = PrefManager(this)

        // restart button
        val restart: Button = findViewById(R.id.restart)
        restart.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }

        // home button
        val home: Button = findViewById(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // retrieve score from intent
        val finalScore = intent.getIntExtra("finalScore", 0)

        // update high score
        updateHighScore(finalScore)

        // display the final score
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "Score: $finalScore"
    }

    // update high score
    private fun updateHighScore(score: Int) {
        val currentHighScore = prefManager.getHighScore()
        if (score > currentHighScore) {
            prefManager.saveHighScore(score)
        }
    }
}
