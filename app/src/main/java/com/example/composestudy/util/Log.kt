package com.example.composestudy.util

import android.annotation.SuppressLint
import android.util.Log
import com.example.composestudy.data.AppConfig
import com.example.composestudy.data.AppConstant
import java.io.PrintWriter
import java.io.StringWriter

object Log {

    // 로그 표시 레벨
    private var LogLevel = Log.VERBOSE
    private var LogTag: String = AppConstant.LOG_TAG
    fun setLogTag(logTag: String) {
        LogTag = logTag
    }

    fun setLogLevel(logLevel: Int) {
        LogLevel = logLevel
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun v(tag: String?, msg: String?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.v(tag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun v(msg: String?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.v(LogTag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun d(tag: String?, msg: String?): Int {
        if (Log.DEBUG < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.d(tag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun d(msg: String?): Int {
        if (Log.DEBUG < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.d(LogTag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun i(tag: String?, msg: String?): Int {
        if (Log.INFO < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.i(tag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun i(msg: String?): Int {
        if (Log.INFO < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.i(LogTag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun w(tag: String?, msg: String?): Int {
        if (Log.WARN < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.w(tag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun w(msg: String?): Int {
        if (Log.WARN < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.w(LogTag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun e(tag: String?, msg: String?): Int {
        if (Log.ERROR < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.e(tag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun e(msg: String?): Int {
        if (Log.ERROR < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) Log.e(LogTag, msg!! + "\n ") else 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun e(tag: String?, exception: Exception?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            exception?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e(LogTag, " ")
            Log.e(LogTag, "EXCEPTION START------------------------------------------------------------------------------------")
            Log.e(LogTag, exceptionAsStrting)
            Log.e(LogTag, "EXCEPTION END--------------------------------------------------------------------------------------")
            Log.e(LogTag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun e(exception: Exception?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            exception?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e(LogTag, " ")
            Log.e(LogTag, "EXCEPTION START------------------------------------------------------------------------------------")
            Log.e(LogTag, exceptionAsStrting)
            Log.e(LogTag, "EXCEPTION END--------------------------------------------------------------------------------------")
            Log.e(LogTag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun exception(tag: String?, exception: Exception?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            exception?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e(tag, " ")
            Log.e(tag, "EXCEPTION START------------------------------------------------------------------------------------")
            Log.e(tag, exceptionAsStrting)
            Log.e(tag, "EXCEPTION END--------------------------------------------------------------------------------------")
            Log.e(tag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun exception(exception: Exception?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            exception?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e(LogTag, " ")
            Log.e(LogTag, "EXCEPTION START------------------------------------------------------------------------------------")
            Log.e(LogTag, exceptionAsStrting)
            Log.e(LogTag, "EXCEPTION END--------------------------------------------------------------------------------------")
            Log.e(LogTag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun exception(throwable: Throwable?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            throwable?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e(LogTag, " ")
            Log.e(LogTag, "START------------------------------------------------------------------------------------")
            Log.e(LogTag, exceptionAsStrting)
            Log.e(LogTag, "END--------------------------------------------------------------------------------------")
            Log.e(LogTag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun wexception(exception: Exception?): Int {
        if (Log.VERBOSE < LogLevel) return 0
        return if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val sw = StringWriter()
            exception?.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.w(LogTag, " ")
            Log.w(LogTag, "EXCEPTION START------------------------------------------------------------------------------------")
            Log.w(LogTag, exceptionAsStrting)
            Log.w(LogTag, "EXCEPTION END--------------------------------------------------------------------------------------")
            Log.w(LogTag, " ")
        } else return 0
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun fullLog(tag: String?, message: String) {
        if (AppConfig.FLAG_ENABLE_LOGCAT) {
            val maxLogSize = 3000
            Log.e(tag, "     ")
            Log.e(tag, "FullLog Start ########################################")
            for (i in 0..message.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > message.length) message.length else end
                Log.e(tag, message.substring(start, end))
            }
            Log.e(tag, "FullLog End ########################################")
            Log.e(tag, "     ")
        }
    }

    @SuppressLint("LongLogTag", "LogNotTimber")
    fun buildLogMsg(message: String?): String? {
        val ste = Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()
        sb.append("[")
        sb.append(ste.fileName.replace(".java", ""))
        sb.append("::")
        sb.append(ste.methodName)
        sb.append("]")
        sb.append(message)
        return sb.toString()
    }
}