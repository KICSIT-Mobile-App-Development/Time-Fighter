package com.example.timefighter

import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.timefighter.BuildConfig.VERSION_NAME

class MainActivity : AppCompatActivity() {
    companion object{
        private val TAG: String = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    private var score:Int = 0
    private var gameStarted: Boolean = false

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    private var timeLeft: Long = 20
    private var initialCountDown:Long = timeLeft * 1000
    private var countDownInterval:Long = 1 * 1000

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId == R.id.about_item){
            showInfo()
        }
        return true
    }

    private fun showInfo(){
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.about_title, VERSION_NAME))
            .setMessage(getString(R.string.about_message))
            .create()
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameScoreTextView = findViewById(R.id.game_score_textview)
        timeLeftTextView = findViewById(R.id.time_left_textview)
        tapMeButton = findViewById(R.id.tap_me_button)

        tapMeButton.setOnClickListener{ view ->
            val bouceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bouceAnimation)
            incrementScore()
        }

        if(savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getLong(TIME_LEFT_KEY)

            restoreGame()
        }else{
            resetGame()
        }

        Log.d(TAG, "onCreate called. Score is: $score")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeft)

        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame(){
        score = 0
        gameStarted = false

        gameScoreTextView.text = getString(R.string.your_score, score)
        timeLeftTextView.text = getString(R.string.time_left, timeLeft)

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
    }

    private fun restoreGame(){
        gameScoreTextView.text = getString(R.string.your_score, score)
        timeLeftTextView.text = getString(R.string.time_left, timeLeft)

        countDownTimer = object: CountDownTimer(timeLeft * 1000, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast
            .makeText(applicationContext, getString(R.string.game_over, score), Toast.LENGTH_LONG)
            .show()
        resetGame()
    }

    private fun incrementScore(){
        if(!gameStarted){
            startGame()
        }

        gameScoreTextView.text = getString(R.string.your_score, ++score)
    }
}