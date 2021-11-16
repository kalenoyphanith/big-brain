package com.oyphanith.bigbrain

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.oyphanith.bigbrain.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private var counterIsActive: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBarWithNavController(findNavController(R.id.fragment))

        // timer
        mediaPlayer = MediaPlayer.create(this, R.raw.sparkle)
        binding.timer.setMax(3600)
        binding.timer.setProgress(1800)
        binding.timer.setOnSeekBarChangeListener(customSeekBarListener)

        binding.startButton.setOnClickListener{startTimer()}
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // timer
    private val customSeekBarListener = object: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            update(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    } // seek bar listener

    fun update(progress: Int) {
        val minutes = progress / 60
        val seconds = progress % 60
        var secondsFinal = ""

        if(seconds <=9) {
            secondsFinal = "0" + seconds
        } else {
            secondsFinal = "" + seconds
        }

        binding.timer.setProgress(progress)
        binding.timerTextView.setText("" + minutes + ":" + secondsFinal)
    }

    fun startTimer() {
        if(!counterIsActive) {
            counterIsActive = true
            binding.timer.setEnabled(false)
            binding.startButton.setBackgroundResource(R.drawable.restart)
            countDownTimer = object : CountDownTimer(
                binding.timer.getProgress().toLong() * 1000,
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    update(millisUntilFinished.toInt() / 1000)
                }

                override fun onFinish() {
                    reset()
                    Toast.makeText(applicationContext, "You have finished studying!", Toast.LENGTH_LONG).show()
                    mediaPlayer?.start()
                }
            }.start()
        } else {
            reset()
        }

    }

    fun reset() {
        binding.timerTextView.setText("30:00")
        binding.timer.setProgress(1800)
        countDownTimer.cancel()
        binding.startButton.setBackgroundResource(R.drawable.play)
        binding.timer.setEnabled(true)
        counterIsActive = false
    }

    override fun onPause() {
        super.onPause()

        if(counterIsActive) {
            countDownTimer.cancel()
        }
    }
}