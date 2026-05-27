package com.ucb.proyectofinal.onboarding.data

import java.util.Locale

actual fun getDeviceLanguage(): String = Locale.getDefault().language
