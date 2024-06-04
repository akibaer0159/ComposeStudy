package com.example.composestudy.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DownloadManager
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Context.ACCESSIBILITY_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.TypedValue
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.composestudy.BuildConfig
import com.example.composestudy.data.AppConstant
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.net.InetAddress
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {
    const val LOG_TAG = "AppUtil.kt"

    interface CallBack {
        fun onAppUtilCallBack(evt: Int, callBackData: Any?)
    }

    fun getDisplayWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getDisplayHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 전화번호 가져오기
     */
    @SuppressLint("HardwareIds", "MissingPermission")
    fun getPhoneNumber(context: Context): String {
        var phoneNumber = ""
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            phoneNumber = telephonyManager.line1Number ?: ""
        } catch (e: Exception) {
            Log.exception(e)
        }

        if(phoneNumber.isNotBlank()) {
            phoneNumber = PhoneNumberUtils.stripSeparators(phoneNumber).replace("+82", "0")
        }
        return phoneNumber
    }

    /**
     * 디바이스 아이디 가져오기
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceId(context: Context): String {
        var deviceId = ""
        try {
            val telephonyManager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            deviceId = telephonyManager.deviceId
        } catch (e: Exception) {
            Log.exception(e)
        }
        return deviceId
    }

    /**
     * 안드로이드 아이디 가져오기
     */
    @SuppressLint("HardwareIds")
    fun getSSAID(context: Context): String {
        var ssaid = ""
        try {
            ssaid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            Log.exception(e)
        }
        return ssaid
    }

    fun getFirstInstallTime(_context: Context): Long {
        val firstInstallTime = _context.packageManager.getPackageInfo(_context.packageName, 0).firstInstallTime
        return firstInstallTime
    }

    fun appUUIDFirstInstall(_context: Context): String {
        val appUUID = UUID.nameUUIDFromBytes("${getSSAID(_context)} ${getFirstInstallTime(_context)}".toByteArray()).toString()
        return appUUID
    }

    @SuppressLint("NewApi")
    fun getWideVindID(): String {
        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

        val wvDrm = try {
            MediaDrm(WIDEVINE_UUID)
        } catch (e: UnsupportedSchemeException) {
            //WIDEVINE is not available
            null
        }

        wvDrm!!.apply {
            val widevineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            val encodedWidevineId = Base64.encodeToString(widevineId, Base64.NO_WRAP).trim()

            Log.w(LOG_TAG, "Widevine ID:$encodedWidevineId")
            wvDrm.close()
            return encodedWidevineId
        }
    }

    fun getDeviceOsVersion(): Int {
        return Build.VERSION.SDK_INT
    }

    fun getDeviceModelName(): String? {
        return Build.MODEL
    }

    fun getDeviceManufacturer(): String? {
        return Build.MANUFACTURER
    }

    fun getOSVersionName(): String? {
        return Build.VERSION.RELEASE
    }

    fun getOSVersionCode(): String? {
        return Build.VERSION.SDK
    }

    fun getAppVersionName(context: Context): String? {
        var pi: PackageInfo? = null
        try {
            pi = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        val appVersionName = pi!!.versionName
        return appVersionName
    }

    fun getAppVersionCode(context: Context): String? {
        var pi: PackageInfo? = null
        try {
            pi = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        val appVersionCdoe = pi!!.versionCode.toString()
        return appVersionCdoe
    }

    /**
     * TalkBack 실행중인지 체크
     */
    fun isTalkbackRunning(context: Context): Boolean? {
        val TALKBACK_PACKAGE_NAME = "com.google.android.marvin.talkback"
        val manager = context.getSystemService(AppCompatActivity.ACCESSIBILITY_SERVICE) as AccessibilityManager
        var isTalkbackRunning = false
        try {
            val services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
            for (service in services) {
                if (service.id.contains(TALKBACK_PACKAGE_NAME)) {
                    isTalkbackRunning = true
                }
            }
        } catch (e: NullPointerException) {
            Log.exception(e)
            return isTalkbackRunning
        }
        return isTalkbackRunning
    }

    fun isTalkbackRunningDefault(context: Context): Boolean? {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        var result = am.isEnabled
        return am.isEnabled
    }

    fun checkNetworkState(context: Context): Boolean {
        var isConnected = false
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val connected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            isConnected = connected
        } catch (e: Exception) {
            Log.exception(e)
        }
        return isConnected
    }

    fun checkNetworkState2(context: Context): Boolean {
        var isConnected = false
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isMetered = cm.isActiveNetworkMetered
            isConnected = isMetered
        } catch (e: Exception) {
            Log.exception(e)
        }
        return isConnected
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun isInternetAvailable(_callback: CallBack?) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    val ipAddr: InetAddress = InetAddress.getByName("google.com")
                    _callback?.onAppUtilCallBack(AppConstant.SUCCESS, !ipAddr.equals(""))
                } catch (e: Exception) {
                    Log.exception(e)
                    _callback?.onAppUtilCallBack(AppConstant.FAIL, false)
                }
            }
        }
    }

    fun isOnline(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("ping -c 1 google.com")
            val exitValue = ipProcess.waitFor()

            /*
            0: 성공, 1: 실패, 2:에러
             */
            return exitValue == 0
        } catch (e: IOException) {
            Log.exception(e)
        } catch (e: InterruptedException) {
            Log.exception(e)
        }
        return false
    }

    fun getStringFromJSON(jsonObj: JSONObject, key: String): String {
        var value = ""
        try {
            if(jsonObj.has(key)) {
                value = jsonObj.getString(key)
            }
        } catch (e: JSONException) {
            Log.wexception(e)
        }
        return value
    }

    fun getBooleanFromJSON(jsonObj: JSONObject, key: String): Boolean {
        var value = false
        try {
            if(jsonObj.has(key)) {
                value = jsonObj.getBoolean(key)
            }
        } catch (e: JSONException) {
            Log.wexception(e)
        }

        return value
    }

    fun putStringFromJSON(jsonObj: JSONObject, key: String, value: String): JSONObject {
        try {
            jsonObj.put(key, value)
        } catch (e: JSONException) {
            Log.wexception(e)
        }
        return jsonObj
    }

    fun getLongFromJSON(jsonObj: JSONObject, key: String): Long {
        var value = 0L
        try {
            if(jsonObj.has(key)) {
                value = jsonObj.getLong(key)
            }
        } catch (e: JSONException) {
            Log.wexception(e)
        }
        return value
    }

    fun getJSONArrayFormJSON(jsonObj: JSONObject, key: String): JSONArray {
        var jsonArray = JSONArray()
        try {
            if(jsonObj.has(key)) {
                jsonArray = jsonObj.getJSONArray(key)
            }
        } catch (e: JSONException) {
            Log.wexception(e)
        }
        return jsonArray
    }

    fun getJSONObjectFormJSON(jsonObj: JSONObject, key: String): JSONObject {
        var jsonObject = JSONObject()
        try {
            if(jsonObj.has(key)) {
                jsonObject = jsonObj.getJSONObject(key)
            }
        } catch (e: JSONException) {
            Log.wexception(e)
        }
        return jsonObject
    }

    fun vibrate(context: Context) {
        /*
        <uses-permission android:name="android.permission.VIBRATE" />
         */
        val VIBRATE_DURATION = 100L
        val VIBRATE_STRENGTH = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val vibrationEffect = VibrationEffect.createOneShot(VIBRATE_DURATION, VIBRATE_STRENGTH)
            vibrator.vibrate(vibrationEffect)
        } else {
            val vibe = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibe.vibrate(VIBRATE_DURATION)
        }
    }

    fun vibrate(context: Context, milliSeconds: Long) {
        /*
        <uses-permission android:name="android.permission.VIBRATE" />
         */
        val VIBRATE_DURATION = milliSeconds
        val VIBRATE_STRENGTH = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val vibrationEffect = VibrationEffect.createOneShot(VIBRATE_DURATION, VIBRATE_STRENGTH)
            vibrator.vibrate(vibrationEffect)
        } else {
            val vibe = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibe.vibrate(VIBRATE_DURATION)
        }
    }

    fun getTimeFormat(value: String): String {
        var stringBuilder = StringBuilder()
        try {
            var time = value.toInt()
            var hour = time / (60 * 60)
            var minute = time / 60
            var second = time % 60

            /*
            if(hour > 0) {
                stringBuilder.append(hour.toString())
                stringBuilder.append(":")
            } else {
                stringBuilder.append("00:")
            }
             */

            if (minute > 0) {
                if (minute < 10) {
                    stringBuilder.append("0")
                }
                stringBuilder.append(minute.toString())
                stringBuilder.append(":")
            } else {
                stringBuilder.append("00:")
            }

            if (second > 0) {
                if (second < 10) {
                    stringBuilder.append("0")
                }
                stringBuilder.append(second.toString())
            } else {
                stringBuilder.append("00")
            }
        } catch (e: Exception) {
            Log.exception(e)
        }
        return stringBuilder.toString()
    }


    fun moveSecuritySettings(context: Context) {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        context.startActivity(intent)
    }

    fun moveSecuritySettings(_activityResult: ActivityResultLauncher<Intent>?) {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        _activityResult?.launch(intent)
    }

    fun checkInstallAppFromPackage(context: Context, appPackageName: String): Boolean {
        var result = true
        val packMgr: PackageManager = context.applicationContext.packageManager
        try {
            packMgr.getPackageInfo(appPackageName, PackageManager.GET_CONFIGURATIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            result = false
        }
        return result
    }

    fun moveCategoryHome(context: Context, delayTime: Long? = 0L): Boolean {
        return try {
            CoroutineScope(Dispatchers.Main).launch {
                delay(delayTime!!)

                val intent = Intent()
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_HOME)
                context.startActivity(intent)
            }
            true
        } catch (e: Exception) {
            Log.exception(e)
            false
        }
    }

    fun runOtherAppUrl(context: Context, url: String? = null, delayTime: Long? = 0L): Boolean {
        return try {
            CoroutineScope(Dispatchers.Main).launch {
                delay(delayTime!!)

                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if(url != null)
                    intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
            true
        } catch (e: Exception) {
            Log.exception(e)
            false
        }
    }

    fun runOtherApp(context: Context, urlScheme: String?): Boolean {
        return try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(urlScheme)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.exception(e)
            false
        }
    }

    fun runOtherAppPackage(context: Context, packageName: String?): Boolean {
        return try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName!!)
            context.startActivity(launchIntent)
            true
        } catch (e: java.lang.Exception) {
            Log.exception(e)
            false
        }
    }

    fun openPlayStore(context: Context, appPackageName: String): Boolean {
        try {
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse(
//                    "https://play.google.com/store/apps/details?id=$appPackageName")
//                setPackage(appPackageName)
//            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.exception(e)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
//            val uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
//                .buildUpon()
//                .appendQueryParameter("id", appPackageName)
//                .appendQueryParameter("launch", "true")
//
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = uriBuilder.build()
//                setPackage(appPackageName)
//            }
            context.startActivity(intent)
            return false
        }
    }

    fun openPlayStore(activity: Activity, appPackageName: String, marketUrl: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            activity.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.exception(e)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl))
            activity.startActivity(intent)
            return false
        }
    }

    fun openPlayStoreUrl(context: Context, marketUrl: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl))
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.exception(e)
            return false
        }
    }

    fun mergeJSONObject(jsonObject1: JSONObject, jsonObject2: JSONObject): JSONObject {
        val jsonObjectMerge = JSONObject()
        try {
            val keys1: Iterator<String> = jsonObject1.keys()
            while (keys1.hasNext()) {
                val key = keys1.next()
                val value = jsonObject1.get(key)
                jsonObjectMerge.put(key, value)
            }

            val keys2: Iterator<String> = jsonObject2.keys()
            while (keys2.hasNext()) {
                val key = keys2.next()
                val value = jsonObject2.get(key)

                if (jsonObjectMerge.has(key)) {
                    jsonObjectMerge.put("${key}_2", value)
                } else {
                    jsonObjectMerge.put(key, value)
                }
            }
        } catch (e: JSONException) {
            Log.exception(e)
        } catch (e: Exception) {
            Log.exception(e)
        }
        return jsonObjectMerge
    }

    fun getBase64EncodedValue(value: String): String {
        return String(Base64.encode(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP))
    }

    fun getBase64EncodedValue(value: ByteArray): String {
        return String(Base64.encode(value, Base64.NO_WRAP))
    }

    fun getBase64DecodedValue(value: ByteArray): String {
        return String(Base64.decode(value, Base64.NO_WRAP))
    }

    fun getBase64DecodedValue(value: String): String {
        return String(Base64.decode(value, Base64.NO_WRAP))
    }

    fun getBase64DecodedByteArray(value: String): ByteArray {
        return Base64.decode(value, Base64.NO_WRAP)
    }

    fun getURLEncodedValue(value: String?): String {
        var encodedValue = ""
        try {
//            encodedValue = UrlEscapers.urlFragmentEscaper().escape(value)
            encodedValue = URLEncoder.encode(value, Charsets.UTF_8.toString()).toString()
        } catch (e: Exception) {
            Log.exception(e)
            encodedValue = URLEncoder.encode(value, "UTF-8")
        }
//        encodedValue = encodedValue.replace("+", "%20")
        return encodedValue
    }

    fun getURLDecodedValue(value: String?): String {

//        Log.w(LOG_TAG, "getURLDecodedValue() / value = $value")

        var decodedValue = ""
        var tmpValue = value

        //#$%^& 으로 검색 시 문제발생
        tmpValue = tmpValue?.replace("%22", "\"")
        tmpValue = tmpValue?.replace("%E2%82%A9", "\\₩")

        try {
            decodedValue = URLDecoder.decode(tmpValue, Charsets.UTF_8.toString()).toString()
        } catch (e: Exception) {
            Log.exception(e)
            decodedValue = tmpValue ?: ""
        }

//        Log.w(LOG_TAG, "getURLDecodedValue() / decodedValue = $decodedValue")

        return decodedValue
    }

    fun actionSend(_context: Context, _subTitle: String?, _text: String?, _uri: Uri?) {
        try {
            val tempIntent = Intent(Intent.ACTION_SEND)
            if (_text != null && _text.isNotEmpty()) {
                tempIntent.type = "text/plain"
                tempIntent.putExtra(Intent.EXTRA_TEXT, _text)
            } else if (_uri != null) {
                tempIntent.type = "image/png"
                tempIntent.putExtra(Intent.EXTRA_STREAM, _uri)
                tempIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val subTitle = if (_subTitle.isNullOrEmpty()) "공유하기" else _subTitle
            val intent = Intent.createChooser(tempIntent, subTitle)
            _context.startActivity(intent)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun actionSendText(_context: Context, _text: String?, _uri: Uri?) {
        try {
            val tempIntent = Intent(Intent.ACTION_SEND)
            tempIntent.type = "text/plain"
            tempIntent.putExtra(Intent.EXTRA_TEXT, _text)
            val subTitle = "공유하기"
            val intent = Intent.createChooser(tempIntent, subTitle)
            _context.startActivity(intent)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun actionSendPdf(_context: Context, _path: String, _fileName: String) {
        try {
            val outputFile = File("${Environment.getExternalStorageDirectory()}/$_path", _fileName)
            val uri = Uri.fromFile(outputFile)
            val tempIntent = Intent(Intent.ACTION_SEND)
            tempIntent.type = "application/pdf"
            tempIntent.putExtra(Intent.EXTRA_STREAM, uri)
            val subTitle = "공유하기"
            val intent = Intent.createChooser(tempIntent, subTitle)
            _context.startActivity(intent)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun actionSendPdf(_context: Context, _uri: Uri) {
        try {
            val tempIntent = Intent(Intent.ACTION_SEND)
            tempIntent.type = "application/pdf"
            tempIntent.putExtra(Intent.EXTRA_STREAM, _uri)
            val subTitle = "공유하기"
            val intent = Intent.createChooser(tempIntent, subTitle)
            _context.startActivity(intent)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun actionSendToSMS(_context: Context, _num: String, _message: String) {
        try {
            val smsUri = Uri.parse("sms:$_num")
            val sendIntent = Intent(Intent.ACTION_SENDTO, smsUri)
            sendIntent.putExtra("sms_body", _message)
            _context.startActivity(sendIntent)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun setClipBoard(_context: Context, _value: String) {
        try {
            val clipboard = _context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clip = ClipData.newPlainText("label", _value)
            clipboard.setPrimaryClip(clip)
        } catch (e: Exception) {
            Log.exception(e)
        }
    }

    fun getClipBoard(_context: Context): String {
        val clipboard = _context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (!clipboard.hasPrimaryClip()) {
            //클립보드에 데이터가 없거나 텍스트 타입이 아닌 경우
            return ""
        } else if (!clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN)!!) {
            //클립보드의 값이 텍스트가 아니면
            return ""
        } else {
            //클립보드의 값이 있으면
            val item: ClipData.Item = clipboard.primaryClip!!.getItemAt(0)
            var clipBoardData = item.text.toString()
            //크로스사이트 관련 replace 처리
            clipBoardData = clipBoardData.replace("<script", "")
            clipBoardData = clipBoardData.replace("</script", "")
            clipBoardData = clipBoardData.replace("/", "")
            clipBoardData = clipBoardData.replace(";", "")
            clipBoardData = clipBoardData.replace(",", "")
            clipBoardData = clipBoardData.replace("!", "")
            clipBoardData = clipBoardData.replace("-", "")
            clipBoardData = clipBoardData.replace("<", "")
            clipBoardData = clipBoardData.replace(">", "")
            return clipBoardData
        }
    }

    fun clearClipboard(_context: Context) {
        val clipboard = _context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (clipboard.hasPrimaryClip()) {
            //클립보드에 데이터가 있다면
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboard.clearPrimaryClip()
            }
            else {
                setClipBoard(_context, "")
            }
        }
    }

    fun getByteArrayToBase64EncodeString(_value: ByteArray): String {
        if (_value == null)
            return ""
        return Base64.encodeToString(_value, Base64.NO_WRAP)
    }

    fun getBitmapToPNGBase64String(_bitmap: Bitmap?, _quality: Int): String {
        if(_bitmap == null) return ""
        val byteArrayOutputStream = ByteArrayOutputStream()
        _bitmap.compress(Bitmap.CompressFormat.PNG, _quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val value = getByteArrayToBase64EncodeString(byteArray)
        return value
    }

    fun getBitmapToJPEGBase64String(_bitmap: Bitmap?, _quality: Int): String {
        if(_bitmap == null) return ""
        val byteArrayOutputStream = ByteArrayOutputStream()
        _bitmap.compress(Bitmap.CompressFormat.JPEG, _quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val value = getByteArrayToBase64EncodeString(byteArray)
        return value
    }

    fun isDigit(_value: String): Boolean {
        try {
            for (index in 0 until _value.length) {
                val tmp = _value.toCharArray()
                if (!tmp[index].isDigit()) {
                    return false
                }
            }
        } catch (e: Exception) {
            Log.exception(e)
        }
        return true
    }

    /**
     * App 버전 비교
     * ver1 : 최신버전
     * ver2 : 햔재버전
     */
    fun compareAppVersion(ver1: String, ver2: String): Int {
        val vals1: Array<String> = ver1.split(".").toTypedArray()
        val vals2: Array<String> = ver2.split(".").toTypedArray()

        if (vals1.size != vals2.size) {
            /*
            버전 체계가 다르다
            비교 할 수 없다.
             */
            return -1
        }

        for (index in vals1.indices) {
            try {
                val arg1 = vals1[index].toInt()
                val arg2 = vals2[index].toInt()

                if(arg1 > arg2) {
                    /*
                    1번이 버전이 더 높다.
                    몇번째 자리수 버전이 높은지 반환해준다.
                     */
                    return index + 1
                }

//                if (vals1.size != vals2.size) {
//                    /*
//                    1번이 버전이 더 높다.
//                    몇번째 자리수 버전이 높은지 반환해준다.
//                     */
//                    return index + 1
//                } else if (arg2 > arg1) {
//                    /*
//                    버전이 동일하거나
//                    2번이 버전이 더 높다.
//                     */
//                    return 0
//                }
            } catch (e: NumberFormatException) {
                Log.exception(e)
            } catch (e: java.lang.Exception) {
                Log.exception(e)
            }
//            return 0
        }
        /*
        버전이 동일하거나
        2번이 버전이 더 높다.
         */
        return 0
    }

    /**
     * App 버전 비교
     * ver1 : 최신버전
     * ver2 : 햔재버전
     *
     * return
     * -1 : 버전 체계가 다르다. 비교 할 수 없다.
     * 0 : 버전이 동일하거나 현재버전이 더 높다.
     * n : 서버버전이 더 높다. (n --> 높은 버전의 자리수 정보)
     */
    fun compareAppVersion2(_serverVersion: String, _appVersion: String): Int {
        val serverVersion: Array<String> = _serverVersion.split(".").toTypedArray()
        val appVersion: Array<String> = _appVersion.split(".").toTypedArray()

        if (serverVersion.size != appVersion.size) {
            /*
            버전 체계가 다르다
            비교 할 수 없다.
             */
            return -1
        }

        for(index in 0 until appVersion.size) {
            try {
                val tmpServerVersion = serverVersion[index].toInt()
                val tmpAppVersion = appVersion[index].toInt()

                if(tmpServerVersion > tmpAppVersion) {
                    /*
                    1번이 버전이 더 높다.
                    몇번째 자리수 버전이 높은지 반환해준다.
                     */
                    return index + 1
                } else if(tmpAppVersion > tmpServerVersion) {
                    return 0
                }
            } catch (e: NumberFormatException) {
                Log.exception(e)
                return 0
            } catch (e: java.lang.Exception) {
                Log.exception(e)
                return 0
            }
        }
        /*
        버전이 동일하거나
        2번이 버전이 더 높다.
         */
        return 0
    }

    fun getMimeType(fileName: String): String {
        val lowerFileName = fileName.lowercase(Locale.getDefault())
        return if (lowerFileName.endsWith("jpg")) {
            "image/jpeg"
        } else if (lowerFileName.endsWith("gif")) {
            "image/gif"
        } else if (lowerFileName.endsWith("png")) {
            "image/png"
        } else if (lowerFileName.endsWith("doc")) {
            "application/msword"
        } else if (lowerFileName.endsWith("docx")) {
            "application/msword"
        } else if (lowerFileName.endsWith("xls")) {
            "application/vnd.ms-excel"
        } else if (lowerFileName.endsWith("xls")) {
            "application/vnd.ms-excel"
        } else if (lowerFileName.endsWith("xlsx")) {
            "application/vnd.ms-excel"
        } else if (lowerFileName.endsWith("csv")) {
            "text/csv"
        } else if (lowerFileName.endsWith("pdf")) {
            "application/pdf"
        } else if (lowerFileName.endsWith("hwp")) {
            "application/hwp"
        } else if (lowerFileName.endsWith("txt")) {
            "text/plain"
        } else if (lowerFileName.endsWith("zip")) {
            "application/zip"
        } else {
            ""
        }
    }

    fun getFileName(_content: String): String {
        var fileName = ""
        if (_content.isNotEmpty()) {
            var offset = _content.lastIndexOf("/")
            fileName = _content.substring(++offset, _content.length)

        }
        return fileName
    }

    @SuppressLint("Range")
    fun onDownloadCompleteBroadcastReceiver(_downloadManager: DownloadManager, _filePath: String, _downloadId: Long, _callback: CallBack?): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.action)) {
                    if (_downloadId == id) {
                        val query: DownloadManager.Query = DownloadManager.Query()
                        query.setFilterById(id)
                        val cursor = _downloadManager.query(query)
                        if (!cursor.moveToFirst()) {
                            return
                        }

                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(columnIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT).show()

                            val downloadFileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            var uriFilePath = Uri.parse(downloadFileName)

                            if (Build.VERSION.SDK_INT >= 24) {
                                val filePath = File(uriFilePath.path)
                                uriFilePath = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", filePath)
                            }
                            _callback?.onAppUtilCallBack(DownloadManager.STATUS_SUCCESSFUL, uriFilePath)

//                            var offset = 0
//                            offset = _filePath.lastIndexOf("/")
//                            val path = _filePath.subSequence(0, offset)
//                            val name = _filePath.subSequence(offset + 1, _filePath.length)
//                            val jsonObject = JSONObject()
//                            jsonObject.put("path", path)
//                            jsonObject.put("name", name)
//                            jsonObject.put("fullPath", _filePath)
//                            _callback.onAppUtilCallBack(DownloadManager.STATUS_SUCCESSFUL, jsonObject)
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                            _callback?.onAppUtilCallBack(DownloadManager.STATUS_FAILED, null)
                        }
                    }
                } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.action)) {
//                    Toast.makeText(context, "Notification clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun downloadFile(_context: Context, _url: String, _callback: CallBack?) {
        val mimeType = getMimeType(_url)
        val downloadManager = _context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(_url)
        var fileName = uri.getQueryParameter("name")
        if (fileName.isNullOrEmpty()) {
            fileName = getFileName(_url)
        }
        val downloadingTxt = "Downloading..."
        val filePath = "${Environment.DIRECTORY_DOWNLOADS}/$fileName"
        val request = DownloadManager.Request(uri)
            .setTitle(fileName)
            .setDescription(downloadingTxt)
            //.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setMimeType(mimeType)
        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(_context, downloadingTxt, Toast.LENGTH_LONG).show()

        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        _context.registerReceiver(onDownloadCompleteBroadcastReceiver(downloadManager, filePath, downloadId, _callback), intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeBytesAsPdf(_context: Context, _bytes : ByteArray, _fileName: String, _callback: CallBack?) {

        try {
            val resolver = _context.contentResolver
            val uriFilePath: Uri?
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File("$path${File.separator}$_fileName.pdf")
            file.writeBytes(_bytes)
            val filePath = File(file.path)
            uriFilePath = FileProvider.getUriForFile(_context, "${BuildConfig.APPLICATION_ID}.fileprovider", filePath)
            resolver.openOutputStream(uriFilePath!!).use {
                it?.write(_bytes)
                it?.close()
                _callback?.onAppUtilCallBack(DownloadManager.STATUS_SUCCESSFUL, uriFilePath)
            }
        } catch (e: Exception) {
            Log.exception(e)
            _callback?.onAppUtilCallBack(DownloadManager.STATUS_FAILED, null)
        }
    }

    fun getCurrentTimeString(): String {
        return System.currentTimeMillis().toString()
    }

    fun getCurrentTimeLong(): Long {
        return System.currentTimeMillis()
    }

    fun getMillToDate(mills: Long): String? {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(Timestamp(mills)) as String
    }

    fun getYYYYMMDD(): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        val calendar = Calendar.getInstance()
        val strToday = simpleDateFormat.format(calendar.time)
        return strToday
    }

    fun runExternalBrowser(_context: Context, _url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, _url.toUri())
            _context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
        }
    }

    fun hashSHA256(msg: String): String {
        var hash: ByteArray? = null
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            Log.exception(e)
            return ""
        }
        return String(hash)
    }

    fun asciiToText(_value: String): String {
        var sb = StringBuffer()
        val splitValue = _value.split("\n").toTypedArray()
        for(index in splitValue.indices) {
            splitValue[index] = splitValue[index].replace(" ", "").trim()
            val length = splitValue[index].length
            for(index2 in 0 until length / 2) {
                var offset = 0
                val tempValue2 = splitValue[index].substring(offset, offset + 2)
                offset += 2
                val text = tempValue2.toInt().toChar()
                sb.append(text)
            }
        }
        return sb.toString()
    }

    /**
     * ADID
     * 보통 안드로이드에서 ADID 란 GAID(Google Advertising ID)를 의미한다.
     * 구글에서 제공하는 Google Play Service의 API를 이용하여 Ad ID를 얻을 수 있는데,
     * Google Play Service가 없는 디바이스에서는 사용이 불가능하다.
     * 해당 GAID는 유저식별용으로 사용하기에 아주 적합하다.
     *
     * 비동기 방식으로 진행하며 (통신)
     * adid 값을 가져오는데 평균 0.2~0.3sec 걸린다. (최대 10sec)
     */
    suspend fun getGoogleAdverisingInfo(_context: Context): String {
        Log.w(LOG_TAG, "getGoogleAdverisingInfo()")

        try {
            val adidDeferred : Deferred<String> = CoroutineScope(Dispatchers.IO).async {
                AdvertisingIdClient.getAdvertisingIdInfo(_context).id.toString()
            }
            val adid = adidDeferred.await()
            return adid
        } catch (e: IllegalStateException) {
            Log.exception(e)
        } catch (e: IOException) {
            Log.exception(e)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.exception(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.exception(e)
        }
        return ""
    }

    fun equals(_orgValue: String, _other: String, _ignoreCase: Boolean): Boolean {
        return _orgValue.equals(other = _other, ignoreCase = _ignoreCase)
    }

    fun equalsY(_orgValue: String, _ignoreCase: Boolean): Boolean {
        return equals(_orgValue = _orgValue, _other = "Y", _ignoreCase = _ignoreCase)
    }

    fun equalsYIgnoreCase(_orgValue: String): Boolean {
        return equalsY(_orgValue = _orgValue, _ignoreCase = true)
    }

    fun gotoSystemWebViewUpdate(_context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", "com.google.android.webview", null)
        intent.data = uri
        _context.startActivity(intent)
    }

    fun gotoAcpplicationDevelopmentSettings(_activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        _activity.startActivity(intent)
    }

    /**
     * 특정 String 문자열 값의 size 를 변경한다.
     *
     * _textView : TextView
     * _defaultSize : 전체 문자열의 기본 size
     * _strValue1 : size를 변경할 특정 문자열
     * _ratioValue1 : 변경할 size (기본 size 대비 비율)
     */
    fun getSpannableStringValueSize(_textView: TextView, _defaultSize: Float, _strValue1: String, _ratioValue1: Float): SpannableString {
        _textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, _defaultSize)
        val fullValue = _textView.text
        val startOffset = fullValue.indexOf(_strValue1)
        val endOffset = _strValue1.length
        val spannableString = SpannableString(fullValue)
        val sizeSpan = RelativeSizeSpan(_ratioValue1)
        spannableString.setSpan(sizeSpan, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /**
     * 특정 String 문자열 값의 size/bold 를 변경한다.
     *
     * _textView : TextView
     * _defaultSize : 전체 문자열의 기본 size
     * _strValue1 : size 변경 bold 처리 할 특정 문자열
     * _ratioValue1 : 변경할 size (기본 size 대비 비율)
     */
    fun getSpannableStringValueSizeBold(_textView: TextView, _defaultSize: Float, _strValue1: String, _ratioValue1: Float): SpannableString {
        _textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, _defaultSize)
        val fullValue = _textView.text
        val startOffset = fullValue.indexOf(_strValue1)
        val endOffset = _strValue1.length
        val spannableString = SpannableString(fullValue)
        val styleSpan = StyleSpan(Typeface.BOLD)

        spannableString.setSpan(styleSpan, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val sizeSpan = RelativeSizeSpan(_ratioValue1)
        spannableString.setSpan(sizeSpan, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /**
     * 특정 String 문자열 값의 size/color 를 변경한다.
     *
     * _textView : TextView
     * _defaultSize : 전체 문자열의 기본 size
     * _strValue1 : size 변경 color 변경할 특정 문자열
     * _ratioValue1 : 변경할 size (기본 size 대비 비율)
     * _colorValue1 : 변경할 생상정보
     */
    fun getSpannableStringValueSizeColor(_textView: TextView, _defaultSize: Float, _strValue1: String, _ratioValue1: Float, _colorValue1: Int): SpannableString {
        _textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, _defaultSize)
        val fullValue = _textView.text
        val startOffset = fullValue.indexOf(_strValue1)
        val endOffset = _strValue1.length
        val spannableString = SpannableString(fullValue)
        val sizeSpan = RelativeSizeSpan(_ratioValue1)
        spannableString.setSpan(sizeSpan, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val foregroundColorSpan = ForegroundColorSpan(_colorValue1)
        spannableString.setSpan(foregroundColorSpan, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /**
     * 서비스 실행 여부 확인
     */
    fun isRunningServcies(context: Context, className: String): Boolean {
        Log.w(LOG_TAG, "isRunningServcies()")
        val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list: List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(50)

        for (info in list) {
            if (info.service.className == className) {
                Log.w(
                    LOG_TAG,
                    "isRunningServcies() / isRunningService.getClassName = ${info.service.className}"
                )
                return true
            }
        }
        return false
    }

    /**
     * Badge Count Update
     */
    fun updateBadgeCount(context: Context, activity: Activity, badgeCount: Int) {
        Log.w(LOG_TAG, "updateBadgeCount()")
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
        intent.putExtra("badge_count", badgeCount)
        intent.putExtra("badge_count_package_name", context.packageName)
        intent.putExtra("badge_count_class_name", activity::class.java.name)
        context.sendBroadcast(intent)
    }


    fun checkProxySimple(): Boolean {
        Log.w(LOG_TAG, "checkProxySimple()")
        val result = System.getProperty("http.proxyHost")
        Log.w(LOG_TAG, "checkProxySimple() / result = $result")
//        if(result != null) {
//            return true
//        } else {
//            return false
//        }
        return result != null
    }

    enum class ProxySetting {
        /* No proxy is to be used. Any existing proxy settings
         * should be cleared. */
        NONE,
        /* Use statically configured proxy. Configuration can be accessed
         * with httpProxy. */
        STATIC,
        /* no proxy details are assigned, this is used to indicate
         * that any existing proxy settings should be retained */
        UNASSIGNED,
        /* Use a Pac based proxy.
         */
        PAC
    }

    @SuppressLint("MissingPermission", "WifiManagerPotentialLeak")
    fun checkProxy(context: Context): Boolean {
        Log.w(LOG_TAG, "checkProxy()")
        var result: Boolean = false

        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!

        if(networkInfo.isConnected) {
            val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val configList: List<WifiConfiguration>? = wifiManager.configuredNetworks
            if(configList != null) {
                for (wifiConfiguration: WifiConfiguration in configList) {
                    val ssidWifiConfiguration = wifiConfiguration.SSID.replace("\"", "")
                    val ssidConnectionInfo = wifiManager.connectionInfo.ssid.replace("\"", "")

                    if(ssidWifiConfiguration.equals(ssidConnectionInfo, ignoreCase = true)) {
                        var settingObj: Any? = null
                        try {
                            settingObj = getProxySetting(wifiConfiguration)
                        } catch (e: Exception) {
                            Log.exception(e)
                        }

                        val ordinal = (settingObj as Enum<*>).ordinal
                        if (ordinal == ProxySetting.STATIC.ordinal) {
                            result = true
                        }

                        break
                    }
                }
            }
        }

        Log.w(LOG_TAG, "checkProxy() / result = $result")
        return result
    }

    @SuppressLint("ObsoleteSdkInt")
    @Throws(Exception::class)
    private fun getProxySetting(wifiConf: WifiConfiguration): Any? {
        Log.w(LOG_TAG, "getProxySetting()")
        var proxySettingsField: Field? = null
        var proxySettings: Any? = null
        if (Build.VERSION.SDK_INT >= 20) {
            val mIpConfigurationField: Field? = getField(wifiConf.javaClass.declaredFields, "mIpConfiguration")
            if (mIpConfigurationField != null) {
                mIpConfigurationField.isAccessible = true
                val mIpConfiguration = mIpConfigurationField[wifiConf]
                if (mIpConfiguration != null) {
                    proxySettingsField = getField(mIpConfiguration.javaClass.fields, "proxySettings")
                    proxySettings = proxySettingsField?.get(mIpConfiguration)
                }
            }
        } else {
            proxySettingsField = getField(wifiConf.javaClass.fields, "proxySettings")
            proxySettings = proxySettingsField?.get(wifiConf)
        }
        return proxySettings
    }

    @Throws(Exception::class)
    private fun getField(fields: Array<Field>, fieldName: String): Field? {
        Log.w(LOG_TAG, "getField()")
        var field: Field? = null
        for (listField in fields) {
            val currentFieldName = listField.name
            if (currentFieldName == fieldName) {
                field = listField
                break
            }
        }
        if (field == null) throw Exception("$fieldName field not found!")
        return field
    }

    fun getDateLong(milliSeconds: Long, dateFormat: String?, year: Int): Long {
        if (milliSeconds <= 0) return 0
        val date: String = getDate(milliSeconds, dateFormat, year)
        var milliTime: Long = 0
        try {
            milliTime = date.toLong()
        } catch (e: java.lang.NumberFormatException) {
            e.printStackTrace()
        }
        return milliTime
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        return getDate(milliSeconds, dateFormat, 0)
    }

    fun getDate(milliSeconds: Long, dateFormat: String?, year: Int): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        if (year != 0) {
            calendar.add(Calendar.YEAR, year)
        }
        return formatter.format(calendar.time)
    }
}