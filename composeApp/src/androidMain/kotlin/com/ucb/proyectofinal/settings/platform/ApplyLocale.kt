package com.ucb.proyectofinal.settings.platform

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun applyLocale(languageCode: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
}
