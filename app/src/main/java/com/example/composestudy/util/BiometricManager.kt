package com.example.composestudy.util

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.biometric.BiometricManager

class BiometricManager {

    companion object {
        const val LOG_TAG = "BiometricManager.kt"

        const val SUCCESS = 0x0000
        const val ERROR = 0x0001
        const val FAILED = 0x0002
        const val NOT_REGISTERED = 0x0003
        const val EXCEPTION = 0x0004
    }

    interface CallBack {
        fun onBiometricCallBack(evt: Int, callBackData1: Any?, callBackData2: Any?)
    }

    /**
     * Biometric(지문, 페이스, 홍채 등) 이용 가능한 상태인지 체크
     */
    fun canUseFingerPrint(context: Context): Boolean {
        Log.w(LOG_TAG, "canUseFingerPrint")

        //생체(안면)가 가능한기기이나 지문지원이 안되는기기 예외처리
        if ("SM-P615N" == Build.MODEL) {
            return false
        }

        if(!canUseFingerPrintX(context))
            return false

        //권한체크
        var hasPerm: Boolean
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            hasPerm = ContextCompat.checkSelfPermission(context, android.Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED
        else
            hasPerm = ContextCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED

        if(!hasPerm)
            return false

        //지원 OS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
            if (fingerprintManager?.isHardwareDetected == false){
                return false
            }
        }
        else{
            //Hardware check
            if(!context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                return false
            }
        }

        return true
    }

    @SuppressLint("SwitchIntDef")
    private fun canUseFingerPrintX(context: Context) : Boolean {
        Log.w(LOG_TAG, "canUseFingerPrintX")

        var canAuthenticate = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val keyguardManager : KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val packageManager : PackageManager = context.packageManager
                if(!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                    Log.w(LOG_TAG, "checkForBiometrics, Fingerprint Sensor not supported")
                    canAuthenticate = false
                }
                if (!keyguardManager.isKeyguardSecure) {
                    Log.w(LOG_TAG, "checkForBiometrics, Lock screen security not enabled in Settings")
                    canAuthenticate = false
                }
            } else {
                /*
                BIOMETRIC_ERROR_HW_UNAVAILABLE = 1;
                BIOMETRIC_ERROR_NONE_ENROLLED = 11;
                BIOMETRIC_ERROR_NO_HARDWARE = 12;
                BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15;
                BIOMETRIC_SUCCESS = 0;

                NONE : 11
                PIN(PATTERN/PWD) : 11
                PIN(PATTERN/PWD) + FingerPrint : 0
                PIN(PATTERN/PWD) + Face : 0
                PIN(PATTERN/PWD) + FingerPrint + Face : 0
                 */
                val biometricManager = BiometricManager.from(context)
                when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        Log.w(LOG_TAG, "checkForBiometrics, biometrics supported / BIOMETRIC_SUCCESS")
                        canAuthenticate = true
                    }
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Log.w(LOG_TAG, "checkForBiometrics, biometrics not supported / BIOMETRIC_ERROR_HW_UNAVAILABLE")
                        canAuthenticate = false
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Log.w(LOG_TAG, "checkForBiometrics, biometrics not supported / BIOMETRIC_ERROR_NONE_ENROLLED")
                        canAuthenticate = false
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        Log.w(LOG_TAG, "checkForBiometrics, biometrics not supported / BIOMETRIC_ERROR_NO_HARDWARE")
                        canAuthenticate = false
                    }
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                        Log.w(LOG_TAG, "checkForBiometrics, biometrics not supported / BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
                        canAuthenticate = false
                    }
                }
            }
        } else {
            canAuthenticate = false
        }
        Log.w(LOG_TAG, "checkForBiometrics ended, canAuthenticate=$canAuthenticate ")
        return canAuthenticate
    }

    /**
     * Biometric(지문, 페이스, 홍채 등) 이 단말에 설정(등록) 되어 있는지 체크
     */
    fun hasEnrolledFingerprint(context: Context): Boolean {
        Log.w(LOG_TAG, "hasEnrolledFingerprint")

        var enrolled = false
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val ret = BiometricManager.from(context).canAuthenticate()
            if(ret == BiometricManager.BIOMETRIC_SUCCESS)
                enrolled = true
        }
        else{
            enrolled = FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
        }
        return enrolled
    }

}