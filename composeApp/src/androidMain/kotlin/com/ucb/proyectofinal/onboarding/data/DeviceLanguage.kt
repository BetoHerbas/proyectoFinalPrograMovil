package com.ucb.proyectofinal.onboarding.data.datasource

import java.util.Locale

actual fun getDeviceLanguage(): String = Locale.getDefault().language
