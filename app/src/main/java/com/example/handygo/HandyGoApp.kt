package com.example.handygo

import android.app.Application

class HandyGoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CloudinaryManager.init(this)
    }
}