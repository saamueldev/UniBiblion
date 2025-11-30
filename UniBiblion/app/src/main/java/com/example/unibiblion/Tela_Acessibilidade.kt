package com.example.unibiblion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class Tela_Acessibilidade : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val PREFS_NAME = "AccessibilityPrefs"
    private val DARK_MODE_KEY = "dark_mode_enabled"
    private val HIGH_CONTRAST_KEY = "high_contrast_enabled"
    private val FONT_SCALE_KEY = "font_scale_factor"
    private val DEFAULT_FONT_SCALE = 1.0f

    private val FONT_SCALES = mapOf(
        "12sp (Pequena)" to 0.85f,
        "14sp (Média)" to 1.0f,
        "16sp (Grande)" to 1.15f
    )
    private val FONT_LABELS = FONT_SCALES.keys.toTypedArray()

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var switchModoEscuro: SwitchMaterial
    private lateinit var switchAltoContraste: SwitchMaterial
    private lateinit var spinnerFontSize: Spinner

    override fun attachBaseContext(newBase: Context) {
        sharedPrefs = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val savedScale = sharedPrefs.getFloat(FONT_SCALE_KEY, DEFAULT_FONT_SCALE)

        val config = newBase.resources.configuration
        config.fontScale = savedScale

        val context = newBase.createConfigurationContext(config)

        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_acessibilidade)

        switchModoEscuro = findViewById(R.id.switch_modo_escuro)
        switchAltoContraste = findViewById(R.id.switch_alto_contraste)
        spinnerFontSize = findViewById(R.id.spinner_font_size)

        loadInitialStates()

        switchModoEscuro.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(DARK_MODE_KEY, isChecked).apply()
            Toast.makeText(this, if (isChecked) "Modo Escuro Ativado" else "Modo Claro Ativado", Toast.LENGTH_SHORT).show()
            recreate()
        }

        switchAltoContraste.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(HIGH_CONTRAST_KEY, isChecked).apply()
            Toast.makeText(this, if (isChecked) "Alto Contraste Ativado" else "Alto Contraste Desativado", Toast.LENGTH_SHORT).show()
            recreate()
        }

        setupSpinner()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_chatbot

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, Tela_Abrir_Noticia_Evento::class.java))
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun applyTheme() {
        val isHighContrast = sharedPrefs.getBoolean(HIGH_CONTRAST_KEY, false)
        val isDarkMode = sharedPrefs.getBoolean(DARK_MODE_KEY, false)

        if (isHighContrast) {
            setTheme(R.style.Theme_Unibiblion_AltoContraste)
        } else {
            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }


    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            FONT_LABELS
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFontSize.adapter = adapter
        spinnerFontSize.onItemSelectedListener = this

        val savedScale = sharedPrefs.getFloat(FONT_SCALE_KEY, DEFAULT_FONT_SCALE)
        val savedLabel = FONT_SCALES.entries.find { it.value == savedScale }?.key

        if (savedLabel != null) {
            val position = adapter.getPosition(savedLabel)
            spinnerFontSize.setSelection(position, false)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedLabel = parent?.getItemAtPosition(position).toString()
        val selectedScaleFactor = FONT_SCALES[selectedLabel] ?: DEFAULT_FONT_SCALE

        val currentSavedScale = sharedPrefs.getFloat(FONT_SCALE_KEY, DEFAULT_FONT_SCALE)

        if (selectedScaleFactor != currentSavedScale) {
            saveAndRecreate(selectedScaleFactor, selectedLabel)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun saveAndRecreate(scaleFactor: Float, label: String) {
        sharedPrefs.edit().putFloat(FONT_SCALE_KEY, scaleFactor).apply()
        Toast.makeText(this, "Tamanho da Fonte ajustado para $label", Toast.LENGTH_SHORT).show()
        recreate()
    }

    private fun loadInitialStates() {
        switchModoEscuro.isChecked = sharedPrefs.getBoolean(DARK_MODE_KEY, false)
        switchAltoContraste.isChecked = sharedPrefs.getBoolean(HIGH_CONTRAST_KEY, false)
    }
}