# Android 异常日志捕获与存储

依赖于 [Timber](https://github.com/JakeWharton/timber) 日志框架，版本： `com.jakewharton.timber:timber:4.7.1` 。

通过实现 `Thread.UncaughtExceptionHandler` 接口，使用 `Timber` 对未处理的异常进行打印，并通过设置 `Timber.Tree` 实现打印后的日志文件输出。
输出路径默认为： `/data/user/0/package-name/files/exception` ，输出文件以当天时间命名，如： `log-2020-05-19.txt` 。

## 使用说明

### 1.使用前初始化 [GlobalExceptionHelper](\lib\src\main\java\vip\qsos\utils_exception\lib\GlobalExceptionHelper.kt)

```kotlin
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 GlobalExceptionHelper
        GlobalExceptionHelper.init(this, Log.VERBOSE, Log.VERBOSE)
    }
}
```

初始化参数说明：

```kotlin
    /**初始化配置
     * @param context Context
     * @param printLevel 日志打印级别，高于等于此级别的日志将被打印
     * @param outputLevel 日志输出级别，高于等于此级别的日志将被保存为应用缓存文件
     * @param tree 自定义日志打印，可重写默认配置 CatchTree
     * */
    fun init(
        context: Context,
        @LEVEL printLevel: Int,
        @LEVEL outputLevel: Int,
        tree: Timber.Tree = CatchTree()
    )
    
    @IntDef(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class LEVEL
```

### 2.使用方式一，直接使用 Timber 日志输出

```kotlin
Timber.tag("测试异常").d("点击测试按钮")
```

### 3.使用方式二，直接抛出异常

```kotlin
throw IOException("IOException")
```

### 4.使用方式三，主动处理异常

```kotlin
GlobalExceptionHelper.uncaughtException(e)
```

```kotlin
try {
    val t: String? = null
    t!!.length
} catch (e: Exception) {
    GlobalExceptionHelper.uncaughtException(e)
}
```

## 日志内容格式

每个日志文件开头都将打印当前设备和应用基本信息，随后打印异常日志信息。

比如： `/data/user/0/package-name/files/exception/log-2020-05-19.txt` 日志内容如下：
```text
--------------------------------------------------------
App Version: 1-1.0
Android Version: 10-29
Vendor: samsung
Model: SM-N9760
CPU: [arm64-v8a, armeabi-v7a, armeabi]
--------------------------------------------------------



[CATCH]2020-05-19 14:11:33	全局异常捕获
java.lang.RuntimeException: java.lang.reflect.InvocationTargetException
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:503)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1076)
Caused by: java.lang.reflect.InvocationTargetException
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
	... 1 more
Caused by: java.io.IOException: IOException
	at vip.qsos.utils_exception.ExceptionActivity$onCreate$1.onClick(ExceptionActivity.kt:15)
	at android.view.View.performClick(View.java:7869)
	at android.widget.TextView.performClick(TextView.java:14958)
	at android.view.View.performClickInternal(View.java:7838)
	at android.view.View.access$3600(View.java:886)
	at android.view.View$PerformClick.run(View.java:29362)
	at android.os.Handler.handleCallback(Handler.java:883)
	at android.os.Handler.dispatchMessage(Handler.java:100)
	at android.os.Looper.loop(Looper.java:237)
	at android.app.ActivityThread.main(ActivityThread.java:8016)
	... 3 more

--------------------------------------------------------



[CATCH]2020-05-19 14:11:34	全局异常捕获
java.lang.NullPointerException: NullPointerException
	at vip.qsos.utils_exception.ExceptionActivity$onCreate$2.onClick(ExceptionActivity.kt:18)
	at android.view.View.performClick(View.java:7869)
	at android.widget.TextView.performClick(TextView.java:14958)
	at android.view.View.performClickInternal(View.java:7838)
	at android.view.View.access$3600(View.java:886)
	at android.view.View$PerformClick.run(View.java:29362)
	at android.os.Handler.handleCallback(Handler.java:883)
	at android.os.Handler.dispatchMessage(Handler.java:100)
	at android.os.Looper.loop(Looper.java:237)
	at android.app.ActivityThread.main(ActivityThread.java:8016)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1076)

--------------------------------------------------------



[CATCH]2020-05-19 14:11:38	全局异常捕获
kotlin.KotlinNullPointerException
	at vip.qsos.utils_exception.ExceptionActivity$onCreate$3.onClick(ExceptionActivity.kt:22)
	at android.view.View.performClick(View.java:7869)
	at android.widget.TextView.performClick(TextView.java:14958)
	at android.view.View.performClickInternal(View.java:7838)
	at android.view.View.access$3600(View.java:886)
	at android.view.View$PerformClick.run(View.java:29362)
	at android.os.Handler.handleCallback(Handler.java:883)
	at android.os.Handler.dispatchMessage(Handler.java:100)
	at android.os.Looper.loop(Looper.java:237)
	at android.app.ActivityThread.main(ActivityThread.java:8016)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1076)

--------------------------------------------------------



[CATCH]2020-05-19 14:17:23	测试异常
点击测试按钮
--------------------------------------------------------

```