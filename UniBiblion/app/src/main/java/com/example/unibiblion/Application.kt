package com.example.unibiblion

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate


class App : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        const val PREFS_NAME = "AccessibilityPrefs"
        const val DARK_MODE_KEY = "dark_mode_enabled"
        const val HIGH_CONTRAST_KEY = "high_contrast_enabled"
        const val FONT_SCALE_KEY = "font_scale_factor"
        const val DEFAULT_FONT_SCALE = 1.0f
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(this)

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        applyGlobalNightMode(sharedPrefs)
    }

    private fun applyGlobalNightMode(prefs: SharedPreferences) {
        val isDarkMode = prefs.getBoolean(DARK_MODE_KEY, false)
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isHighContrast = sharedPrefs.getBoolean(HIGH_CONTRAST_KEY, false)

        if (isHighContrast) {
            activity.setTheme(R.style.Theme_Unibiblion_AltoContraste)
        }
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
