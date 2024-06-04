package com.example.composestudy.util

import android.content.Context
import android.content.SharedPreferences
import java.io.UnsupportedEncodingException
import java.lang.NumberFormatException

class SharedPrefsManager(private val context: Context) {
    companion object {
        const val LOG_TAG = "SharedPrefsManager.kt"

        const val DEFAULT_GROUP_NAME = "SHARED_PREF_DATA"
    }

    private fun get(): SharedPreferences = context.getSharedPreferences(DEFAULT_GROUP_NAME, Context.MODE_PRIVATE)
    private fun get(group: String): SharedPreferences = context.getSharedPreferences(group, Context.MODE_PRIVATE)

    /**
     * has ShredPreference Value
     */
    fun hasShredPref(key: String): Boolean {
        return get().contains(key)
    }
    fun hasShredPref(group: String, key: String): Boolean {
        return get(group).contains(key)
    }

    /**
     * set ShredPreference Value
     */
    fun setSharedPrefString(key:String, value: String?, base64: Boolean = true) {
        var saveValue = value ?: ""
        if(base64) {
            saveValue = AppUtil.getBase64EncodedValue(value ?: "")
        }
        get().edit().also {
            it.putString(key, saveValue)
        }.apply()
    }
    fun setSharedPrefString(group: String, key:String, value: String?, base64: Boolean = true) {
        var saveValue = value ?: ""
        if(base64) {
            saveValue = AppUtil.getBase64EncodedValue(value ?: "")
        }
        get(group.toString()).edit().also {
            it.putString(key, saveValue)
        }.apply()
    }

    /**
     * get ShredPreference Value
     */
    fun getSharedPrefString(key: String, base64: Boolean = true): String {
        var loadValue: String = get().getString(key, "") ?: ""
        if(base64) {
            val base64DecodedValue = AppUtil.getBase64DecodedValue(loadValue)
            loadValue = base64DecodedValue
        }
        return loadValue
    }
    fun getSharedPrefString(group: String, key: String, base64: Boolean = true): String {
        var loadValue: String = get(group).getString(key, "") ?: ""
        if(base64) {
            val base64DecodedValue = AppUtil.getBase64DecodedValue(loadValue)
            loadValue = base64DecodedValue
        }
        return loadValue
    }

    /**
     * set ShredPreference Value
     */
    fun setSharedPrefBoolean(key:String, saveValue: Boolean = false, base64: Boolean = true) {
        val value = let {
            if(base64) {
                AppUtil.getBase64EncodedValue(saveValue.toString())
            } else {
                saveValue.toString()
            }
        }
        get().edit().also {
            it.putString(key, value)
        }.apply()
    }
    fun setSharedPrefBoolean(group: String, key:String, saveValue: Boolean = false, base64: Boolean = true) {
        val value = let {
            if(base64) {
                AppUtil.getBase64EncodedValue(saveValue.toString())
            } else {
                saveValue.toString()
            }
        }
        get(group).edit().also {
            it.putString(key, value)
        }.apply()
    }

    /**
     * get ShredPreference Value
     */
    fun getSharedPrefBoolean(key: String, defaultValue: Boolean = false, base64: Boolean = true): Boolean {
        val loadValue = getSharedPrefString(key, base64)
        var booleanLoadValue: Boolean = defaultValue
        try {
            booleanLoadValue = loadValue.toBoolean()
        } catch (e: UnsupportedEncodingException) {
            Log.exception(e)
        } catch (e: Exception) {
            Log.exception(e)
        }
        return booleanLoadValue
    }
    fun getSharedPrefBoolean(group: String, key: String, defaultValue: Boolean = false, base64: Boolean = true): Boolean {
        val loadValue = getSharedPrefString(group, key, base64)
        var booleanLoadValue: Boolean = defaultValue
        try {
            booleanLoadValue = loadValue.toBoolean()
        } catch (e: UnsupportedEncodingException) {
            Log.exception(e)
        } catch (e: Exception) {
            Log.exception(e)
        }
        return booleanLoadValue
    }

    /**
     * set ShredPreference Value
     */
    fun setSharedPrefInt(key:String, saveValue: Int, base64: Boolean = true) {
        val value = let {
            if(base64) {
                AppUtil.getBase64EncodedValue(saveValue.toString())
            } else {
                saveValue.toString()
            }
        }
        get().edit().also {
            it.putString(key, value)
        }.apply()
    }
    fun setSharedPrefInt(group: String, key:String, saveValue: Int, base64: Boolean = true) {
        val value = let {
            if(base64) {
                AppUtil.getBase64EncodedValue(saveValue.toString())
            } else {
                saveValue.toString()
            }
        }
        get(group).edit().also {
            it.putString(key, value)
        }.apply()
    }

    /**
     * get ShredPreference Value
     */
    fun getSharedPrefInt(key: String, defaultValue: Int = 0, base64: Boolean = true): Int {
        val loadValue = getSharedPrefString(key, base64)
        var booleanLoadValue: Int = defaultValue
        try {
            if(loadValue.isNotBlank()) {
                booleanLoadValue = loadValue.toInt()
            }
        } catch (e: NumberFormatException) {
            Log.exception(e)
        } catch (e: UnsupportedEncodingException) {
            Log.exception(e)
        } catch (e: Exception) {
            Log.exception(e)
        }

        return booleanLoadValue
    }
    fun getSharedPrefInt(group: String, key: String, defaultValue: Int = 0, base64: Boolean = true): Int {
        val loadValue = getSharedPrefString(group, key, base64)
        var booleanLoadValue: Int = defaultValue
        try {
            if(loadValue.isNotBlank()) {
                booleanLoadValue = loadValue.toInt()
            }
        } catch (e: NumberFormatException) {
            Log.exception(e)
        } catch (e: UnsupportedEncodingException) {
            Log.exception(e)
        } catch (e: Exception) {
            Log.exception(e)
        }

        return booleanLoadValue
    }

    fun removeSharedPref(key: String) {
        get().edit().also {
            it.remove(key)
        }.apply()
    }
    fun removeSharedPref(group: String, key: String) {
        get(group).edit().also {
            it.remove(key)
        }.apply()
    }

}