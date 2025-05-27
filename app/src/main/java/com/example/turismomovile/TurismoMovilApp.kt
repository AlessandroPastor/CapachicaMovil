package com.example.turismomovile

import android.app.Application
import com.example.turismomovile.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TurismoMovilApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TurismoMovilApp)
            modules(appModule)
        }
    }
}
