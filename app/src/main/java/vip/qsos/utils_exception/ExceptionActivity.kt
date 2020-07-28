package vip.qsos.utils_exception

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exception.*
import java.io.IOException

class ExceptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception)

        io_exception.setOnClickListener {
            throw IOException("IOException")
        }
        null_exception.setOnClickListener {
            throw NullPointerException("NullPointerException")
        }
        other_exception.setOnClickListener {
            try {
                val t: String? = null
                t!!.length
            } catch (e: Exception) {
                GlobalExceptionHelper.uncaughtException(e)
            }
        }

    }
}
