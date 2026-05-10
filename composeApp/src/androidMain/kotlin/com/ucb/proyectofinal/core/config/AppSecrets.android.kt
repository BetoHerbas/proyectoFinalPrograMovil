package com.ucb.proyectofinal.core.config

import com.ucb.proyectofinal.BuildConfig

actual object AppSecrets {
    actual val tmdbReadToken: String = BuildConfig.TMDB_READ_TOKEN
    actual val googleBooksApiKey: String = BuildConfig.GOOGLE_BOOKS_API_KEY
}
