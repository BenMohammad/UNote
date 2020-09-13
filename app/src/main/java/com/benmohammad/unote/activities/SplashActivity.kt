package com.benmohammad.unote.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.unote.R
import java.lang.Thread.sleep

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sleep(500)
        startActivity(Intent(SplashActivity@this, MainActivity::class.java))
    }
}