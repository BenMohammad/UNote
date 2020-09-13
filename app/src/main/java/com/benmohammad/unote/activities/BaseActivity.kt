package com.benmohammad.unote.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.unote.App
import com.benmohammad.unote.model.db.AppDatabase

open class BaseActivity: AppCompatActivity() {

    lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    fun init() {
        appDatabase = App.getAppDatabase(this)
    }
}