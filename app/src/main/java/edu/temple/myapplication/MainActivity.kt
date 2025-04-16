package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var preferences : SharedPreferences
    private lateinit var file : File
    private val fileName = "countdown_info"
    var time : Int = 0

    var timerBinder: TimerService.TimerBinder? = null
    val timerHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            time = msg.what
            findViewById<TextView>(R.id.textView).text = time.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                timerBinder = p1 as TimerService.TimerBinder
                timerBinder?.setHandler(timerHandler)
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
            startTimer()
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopTimer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_start)
        {
            startTimer()
        }
        else if (item.itemId == R.id.action_stop)
        {
            stopTimer()
        } else {
            return false
        }

        return true
    }

    fun startTimer(){

        preferences = getPreferences(MODE_PRIVATE)
        file = File(filesDir, fileName)

        if(file.exists()){
            try {
                val inputStream = FileInputStream(file)
                val time = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                timerBinder?.start(time.toInt())
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }

        } else {
            if(timerBinder?.isRunning == true) {
                timerBinder!!.pause()
            } else {
                timerBinder?.start(25)
            }
        }

    }

    fun stopTimer(){
        timerBinder?.stop()
        saveTime(time)
    }

    private fun saveTime(time : Int) {
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(time.toString().toByteArray())
            outputStream.close()
        } catch (e : IOException) {
            e.printStackTrace()
        }
    }
}