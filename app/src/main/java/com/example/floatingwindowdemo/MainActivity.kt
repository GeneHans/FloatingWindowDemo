package com.example.floatingwindowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.floatingwindowdemo.custom.floatview.FloatTextView
import com.example.floatingwindowdemo.custom.floatview.FloatWindow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var floatTextView:FloatTextView = findViewById(R.id.float_text1)
        var floatWindow:FloatWindow = findViewById(R.id.float_window1)
        floatTextView.setAttachAble(false)
        floatWindow.setAttachAble(false)
    }
}