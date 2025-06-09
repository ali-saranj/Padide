package com.halion.padide

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PadideApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // koin
        startKoin {
            androidContext(this@PadideApp)
        }
    }
}