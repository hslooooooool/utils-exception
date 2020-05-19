package vip.qsos.utils_exception

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test.setOnClickListener {
            Timber.tag("测试异常").d("点击测试按钮")
            startActivity(Intent(this, ExceptionActivity::class.java))
        }

    }

}
