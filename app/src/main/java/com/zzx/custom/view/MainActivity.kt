package com.zzx.custom.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<LineAnimationView>(R.id.animation_view)
        findViewById<View>(R.id.btn_start).setOnClickListener {
            view.startCountDown()
        }
    }
}
