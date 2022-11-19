package com.akokko.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // 获取时间显示TextView
    val playerPosition by lazy { findViewById<TextView>(R.id.player_position) }
    val playerDuration by lazy { findViewById<TextView>(R.id.player_duration) }

    // 获取进度条
    val seekBar by lazy { findViewById<SeekBar>(R.id.seek_bar) }

    // 获取按钮
    val btPrevious by lazy { findViewById<ImageView>(R.id.bt_previous) }
    val btRewind by lazy { findViewById<ImageView>(R.id.bt_rewind) }
    val btPlay by lazy { findViewById<ImageView>(R.id.bt_play) }
    val btPause by lazy { findViewById<ImageView>(R.id.bt_pause) }
    val btForward by lazy { findViewById<ImageView>(R.id.bt_forward) }
    val btNext by lazy { findViewById<ImageView>(R.id.bt_next) }

    // 获取媒体
    lateinit var mediaPlayer: MediaPlayer

    // 获取线程
    val handler by lazy { Handler() }
    val runnable by lazy {
        object: Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 默认代码
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        // 初始化音频
        mediaPlayer = MediaPlayer.create(this, R.raw.music)

        // 初始化进度条和时间
        initialization()

        // 绑定按钮
        btnOnBind()

        // 拖动进度条
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
                playerPosition.setText(convertFormat(mediaPlayer.currentPosition.toLong()))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        // 播放完置零
        mediaPlayer.setOnCompletionListener {
            btPause.visibility = View.GONE
            btPlay.visibility = View.VISIBLE
            mediaPlayer.seekTo(0)
        }
    }

    // 初始化
    private fun initialization() {
        val duration = mediaPlayer.duration
        val sDuration = convertFormat(duration.toLong())
        playerDuration.setText(sDuration)
    }

    // 按钮绑定
    private fun btnOnBind() {
        btPlay.setOnClickListener {
            btPlay.visibility = View.GONE
            btPause.visibility = View.VISIBLE
            mediaPlayer.start()
            seekBar.max = mediaPlayer.duration
            handler.postDelayed(runnable, 0)
        }

        btPause.setOnClickListener {
            btPause.visibility = View.GONE
            btPlay.visibility = View.VISIBLE
            mediaPlayer.pause()
            handler.removeCallbacks(runnable)
        }

        btForward.setOnClickListener {
            var currentPosition = mediaPlayer.currentPosition
            val duration = mediaPlayer.duration
            if (mediaPlayer.isPlaying && duration != currentPosition) {
                currentPosition = currentPosition + 5000
                playerPosition.setText(convertFormat(currentPosition.toLong()))
                mediaPlayer.seekTo(currentPosition)
            }
        }

        btRewind.setOnClickListener {
            var currentPosition = mediaPlayer.currentPosition
            if (mediaPlayer.isPlaying && currentPosition > 5000) {
                currentPosition = currentPosition - 5000
                playerPosition.setText(convertFormat(currentPosition.toLong()))
                mediaPlayer.seekTo(currentPosition)
            }
        }

        btPrevious.setOnClickListener {
            mediaPlayer.stop()
            btPause.visibility = View.GONE
            btPlay.visibility = View.VISIBLE
            mediaPlayer.seekTo(0)
            mediaPlayer = MediaPlayer.create(this, R.raw.music2)
            initialization()
        }

        btNext.setOnClickListener {
            mediaPlayer.stop()
            btPause.visibility = View.GONE
            btPlay.visibility = View.VISIBLE
            mediaPlayer.seekTo(0)
            mediaPlayer = MediaPlayer.create(this, R.raw.music3)
            initialization()
        }
    }

    // 时间格式化
    private fun convertFormat(duration: Long): String {
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))
    }

}