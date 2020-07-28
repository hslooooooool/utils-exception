package vip.qsos.utils_exception

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.IntDef
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**全局异常处理帮助类，采用 Timber 日志拦截并进行日志输出
 * 异常文件保存路径 /data/user/0/package-name/files/exception
 * @author : 华清松
 */
object GlobalExceptionHelper : Thread.UncaughtExceptionHandler {

    private val mDayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val mTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    private var appContext: Context? = null
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    var printLevel: Int = Log.ERROR
        private set
    var outputLevel: Int = Log.ERROR
        private set

    @IntDef(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class LEVEL

    /**初始化配置
     * @param context Context
     * @param printLevel 日志打印级别，高于等于此级别的日志将被打印
     * @param outputLevel 日志输出级别，高于等于此级别的日志将被保存为应用缓存文件
     * @param tree 自定义日志打印，可重写默认配置 CatchTree
     * @see CatchTree
     * @see Timber.Tree
     * */
    fun init(
        context: Context,
        @LEVEL printLevel: Int,
        @LEVEL outputLevel: Int,
        tree: Timber.Tree = CatchTree()
    ) {
        appContext = context.applicationContext
        GlobalExceptionHelper.printLevel = printLevel
        GlobalExceptionHelper.outputLevel = outputLevel
        Timber.uprootAll()
        Timber.plant(tree)

        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun uncaughtException(e: Throwable) {
        Timber.tag("全局异常捕获").e(e)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Timber.tag("全局异常捕获").e(e)
        if (mDefaultCrashHandler == null) {
            android.os.Process.killProcess(android.os.Process.myPid())
        } else {
            mDefaultCrashHandler?.uncaughtException(t, e)
        }
    }

    /**Timber日志输出配置*/
    open class CatchTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= printLevel) {
                super.log(priority, tag, message, t)
            }
            if (priority >= outputLevel) {
                if (appContext != null && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
                    saveCatchFile(
                        tag!!,
                        message
                    )
                }
            }
        }
    }

    /**保存日志到文件中*/
    private fun saveCatchFile(type: String, msg: String) {
        val sb = StringBuffer()
        val date = mTimeFormat.format(Date())
        sb.append("[CATCH]")
        sb.append("$date\t$type")
        sb.append("\n")
        sb.append(msg)
        sb.append("\n--------------------------------------------------------")
        sb.append("\n\n\n\n")
        writeFile(sb.toString())
    }

    @SuppressLint("LogNotTimber")
    private fun writeFile(sb: String) {
        try {
            val crash =
                getCatchFile()
            val fos = FileOutputStream(crash, true)
            val osw = OutputStreamWriter(fos, "UTF-8")
            osw.write(sb)
            osw.flush()
            osw.close()
            Log.i("存储异常日志", "异常已记录到: ${crash.absolutePath}")
        } catch (e: Exception) {
            Log.i("存储异常日志", "异常记录失败 \n$sb")
        }
    }

    @SuppressLint("LogNotTimber")
    @Throws(IOException::class)
    private fun getCatchFile(): File {
        val f1 = appContext?.getExternalFilesDir(null)
        f1?.mkdirs()
        val time = mDayFormat.format(Date())
        val f2 = File(f1, "exception/")
        f2.mkdirs()
        val f3 = File(f2, "log-$time.txt")
        if (!f3.exists()) {
            f3.createNewFile()
            try {
                val info =
                    phoneInfo()
                val fos = FileOutputStream(f3, true)
                val osw = OutputStreamWriter(fos, "UTF-8")
                osw.write(info)
                osw.flush()
                osw.close()
            } catch (e: Exception) {
                Log.w("存储异常日志", "初始化信息失败")
            }
        }
        return f3
    }

    private fun phoneInfo(): String {
        val pm = appContext?.packageManager
        val pi = pm?.getPackageInfo(appContext!!.packageName, PackageManager.GET_ACTIVITIES)
            ?: return "未获得设备信息"

        val sb = StringBuilder()

        sb.append("--------------------------------------------------------")
        sb.append("\n")
        // APP 版本
        sb.append("App Version: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            sb.append("${pi.longVersionCode}".trimIndent())
        } else {
            sb.append("${pi.versionCode}".trimIndent())
        }
        sb.append("-")
        sb.append(pi.versionName)
        sb.append("\n")

        // Android版本
        sb.append("Android Version: ")
        sb.append(Build.VERSION.RELEASE)
        sb.append("-")
        sb.append("${Build.VERSION.SDK_INT}".trimIndent())
        sb.append("\n")

        // 手机制造商
        sb.append("Vendor: ")
        sb.append(Build.MANUFACTURER.trimIndent())
        sb.append("\n")

        // 手机型号
        sb.append("Model: ")
        sb.append(Build.MODEL.trimIndent())
        sb.append("\n")

        // CPU架构
        sb.append("CPU: ")
        sb.append(Arrays.toString(Build.SUPPORTED_ABIS))
        sb.append("\n--------------------------------------------------------")
        sb.append("\n\n\n\n")
        return sb.toString()
    }
}