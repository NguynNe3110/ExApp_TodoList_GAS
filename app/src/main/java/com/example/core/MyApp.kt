package com.example.core

import android.app.Application
import com.example.core.di.AppContainer

class MyApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
