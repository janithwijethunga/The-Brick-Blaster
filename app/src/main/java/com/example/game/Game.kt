package com.example.game

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.animation.LinearInterpolator


class Game : AppCompatActivity() {
    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var score = 0

    private val brickRows = 10
    private val brickColumns = 9
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4

    private var lives = 3
    private var isBallLaunched = false
    private var ballSpeedIncrement = 0.5f
    private var speedIncreaseInterval = 5000L
    private val constantBallSpeedX = 3f
    private val constantBallSpeedY = -3f

    // function called when activity created
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // initialize views
        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

        // new game button
        val newgame = findViewById<Button>(R.id.newgame)
        newgame.setOnClickListener {
            initializeBricks()
            start()
            newgame.visibility = View.INVISIBLE
        }

        // Set paddle movement
        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }

    // initialize bricks layout
    private fun initializeBricks() {

        brickContainer.removeAllViews()


        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()


        val screenWidth = resources.displayMetrics.widthPixels
        val availableWidth = screenWidth - brickMargin * (brickColumns + 1)
        val brickWidth = availableWidth / brickColumns


        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.drawable.brick)
                rowLayout.addView(brick)
            }

            brickContainer.addView(rowLayout)
        }
    }


    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }

    // move paddle
    private fun movePaddle(x: Float) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val newPaddleX = x - paddle.width / 2
        paddleX = when {
            newPaddleX < 0 -> 0f
            newPaddleX + paddle.width > screenWidth -> screenWidth - paddle.width
            else -> newPaddleX
        }
        paddle.x = paddleX
    }

    // check hit
    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()


        if (ballX <= 0 || ballX + ball.width >= screenWidth) {
            ballSpeedX *= -1
        }


        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        // check for paddle hit
        if (ballY + ball.height >= paddle.y && ballY + ball.height <= paddle.y + paddle.height
            && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width
        ) {
            ballSpeedY *= -1

            scoreText.text = "Score: $score"
        }

        // check for ball falling out of screen
        if (ballY + ball.height >= screenHeight) {
            resetBallPosition()
        }

        // check for brick hit
        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout

            val rowTop = rowLayout.y + brickContainer.y
            val rowBottom = rowTop + rowLayout.height

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom
                    ) {
                        brick.visibility = View.INVISIBLE
                        ballSpeedY *= -1
                        score++
                        scoreText.text = "Score: $score"
                        return
                    }
                }
            }
        }

        // check for ball falling out of screen (bottom)
        if (ballY + ball.height >= screenHeight - 100) {
            lives--

            if (lives > 0) {
                Toast.makeText(this, "$lives balls left", Toast.LENGTH_SHORT).show()
            }

            if (lives <= 0) {
                gameOver()
            } else {
                resetBallPosition()
                start()
            }
        }
    }

    // reset ball position
    private fun resetBallPosition() {
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2 + 525

        ball.x = ballX
        ball.y = ballY

        ballSpeedX = 0 * screenDensity
        ballSpeedY = 0 * screenDensity

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX
    }

    // start game
    private fun start() {
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2

        ballSpeedX = constantBallSpeedX * screenDensity
        ballSpeedY = constantBallSpeedY * screenDensity

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }

    // handle game over
    private fun gameOver() {
        // "Game Over" text
        scoreText.text = "Game Over"

        // store the final score to pass it gameOver activity
        val finalScore = score

        // hide the new game button
        val newgame = findViewById<Button>(R.id.newgame)
        newgame.visibility = View.GONE

        // start the gameOver activity and pass the final score
        val gameOverIntent = Intent(this, gameOver::class.java)
        gameOverIntent.putExtra("finalScore", finalScore)
        startActivity(gameOverIntent)

        // finish game over activity
        finish()
    }
}
