package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    var timerBinder: TimerService.TimerBinder? = null
    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                timerBinder = p1 as TimerService.TimerBinder
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                timerBinder = null
            }

        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if(timerBinder?.isRunning == true) {
                timerBinder!!.pause()
            } else {
                timerBinder?.start(25)
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder?.stop()
        }
    }
}