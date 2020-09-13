package com.benmohammad.unote

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.benmohammad.unote.model.db.AppDatabase

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initAppDatabase()
    }


    private fun initAppDatabase(): AppDatabase {
        appDatabase = Room.databaseBuilder(applicationContext,
        AppDatabase::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
        return appDatabase
    }



    companion object {
        const val DATABASE_NAME = "MyDb"
        lateinit var appDatabase: AppDatabase

        fun getAppDatabase(context: Context): AppDatabase {

            if(appDatabase != null)
                return appDatabase as AppDatabase
            else
                return Room.databaseBuilder(context,
                    AppDatabase::class.java, Companion.DATABASE_NAME
                ).fallbackToDestructiveMigration().build()

        }
    }

}