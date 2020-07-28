package vip.qsos.utils_exception

import android.app.Application
import android.util.Log

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHelper.init(this, Log.VERBOSE, Log.VERBOSE)
    }
}