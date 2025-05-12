package com.example.signalization.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.ControlSound
import com.example.signalization.R
import com.example.signalization.LastUndecidedLoader

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val container = findViewById<LinearLayout>(R.id.llLastAlertContainer)

        LastUndecidedLoader(this, layoutInflater, container).load()


        findViewById<Button>(R.id.btnAllAlerts).setOnClickListener {
            startActivity(Intent(this, AllRecordsActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddEmployee).setOnClickListener {
            startActivity(Intent(this, StuffActivity::class.java))
        }

        findViewById<Button>(R.id.btnSimulateIntrusion).setOnClickListener {
            startActivity(Intent(this, AmulationActivity::class.java))
        }

        findViewById<Button>(R.id.btnAlarmToggle).setOnClickListener {
            val controlSound = ControlSound(this)
            controlSound.toggleAlarm()
        }

    }
}
