package com.rejowan.numberconverter

import android.app.Application
import com.rejowan.numberconverter.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NumberConverterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@NumberConverterApp)
            modules(appModule)
        }
    }
}
