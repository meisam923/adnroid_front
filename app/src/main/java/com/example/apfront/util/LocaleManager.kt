package com.example.apfront.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("LocalePrefs", Context.MODE_PRIVATE)

    companion object {
        private const val LANGUAGE_KEY = "selected_language"
    }

    fun setLocale(languageCode: String) {
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()
        applyLocale(languageCode)
    }

    fun applyLocaleOnStartup() {
        val languageCode = prefs.getString(LANGUAGE_KEY, "en") ?: "en"
        applyLocale(languageCode)
    }

    private fun applyLocale(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}