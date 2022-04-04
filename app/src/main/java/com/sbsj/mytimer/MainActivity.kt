package com.sbsj.mytimer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private  var currentCountDownTimer: CountDownTimer? =null
    private val soundPool =SoundPool.Builder().build()
    private var tickingSoundID :Int? = null
    private var bellSoundID :Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()

    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()

    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
    private fun initSounds() {
        tickingSoundID = soundPool.load(this,R.raw.timer_ticking,1)
        bellSoundID = soundPool.load(this,R.raw.timer_bell,1)
    }

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) { //fromUser 변수는 사용자가 건드렸을떄
                   if(fromUser){
                       updateRemainTime(progress * 60 * 1000L)
                   }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    startCountDown()

                }
            }

        )
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun startCountDown(){
        currentCountDownTimer =createCountDownTimer(seekBar.progress * 60 * 1000L )
        currentCountDownTimer?.start()

        tickingSoundID?.let { soundId ->
            soundPool.play(soundId,1f,1f,0,-1,1f)
        }
    }
    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundID?.let{ soundId->
            soundPool.play(soundId,1f,1f,0,0,1f)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {

        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMills : Long){
        seekBar.progress = (remainMills /1000 / 60).toInt()
    }

}