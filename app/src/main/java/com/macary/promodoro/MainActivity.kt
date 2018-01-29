package com.macary.promodoro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seekBar: SeekBar? = null
        seekBar?.max = 100
        seekBar?.progress = 10
    }
}
