package vip.qsos.utils_exception

import android.app.Application
import android.util.Log
import vip.qsos.utils_exception.lib.GlobalExceptionHelper

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHelper.init(this, Log.VERBOSE, Log.VERBOSE)
    }
}