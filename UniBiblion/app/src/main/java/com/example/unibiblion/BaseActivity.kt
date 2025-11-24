package com.example.unibiblion

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        val savedScale = sharedPrefs.getFloat(App.FONT_SCALE_KEY, App.DEFAULT_FONT_SCALE)

        if (savedScale != App.DEFAULT_FONT_SCALE) {
            val config = newBase.resources.configuration
            config.fontScale = savedScale
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }
}
