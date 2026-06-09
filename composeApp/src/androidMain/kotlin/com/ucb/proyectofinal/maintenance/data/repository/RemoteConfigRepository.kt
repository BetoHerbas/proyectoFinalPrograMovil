package com.ucb.proyectofinal.maintenance.domain.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ucb.proyectofinal.settings.data.AppSettingsStore
import com.ucb.proyectofinal.worker.ABTestingScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

actual class RemoteConfigRepository actual constructor() {

    companion object {
        private const val POLL_INTERVAL_MS = 10_000L

        private val DEFAULT_ONBOARDING_JSON = """
        {
          "onboarding_config": [
            {
              "id": 1,
              "title": { "es": "¡Organiza tu entretenimiento!", "en": "Organize your entertainment!", "fr": "Organisez votre divertissement !" },
              "description": { "es": "Gestiona tus películas, series, libros y videojuegos favoritos en un solo lugar y de forma sencilla.", "en": "Manage your favorite movies, series, books, and video games in one place easily.", "fr": "Gérez facilement vos films, séries, livres et jeux vidéo préférés en un seul endroit." },
              "image_url": { "es": "https://picsum.photos/seed/onboarding1/800/600", "en": "https://picsum.photos/seed/onboarding1/800/600", "fr": "https://picsum.photos/seed/onboarding1/800/600" }
            },
            {
              "id": 2,
              "title": { "es": "Crea listas personalizadas", "en": "Create custom lists", "fr": "Créez des listes personnalisées" },
              "description": { "es": "Clasifica tus colecciones, agrégales portadas atractivas y decide si quieres mantenerlas privadas o compartirlas.", "en": "Categorize your collections, add attractive covers, and decide whether to keep them private or share them.", "fr": "Classez vos collections, ajoutez des couvertures attrayantes et décidez de les garder privées ou de les partager." },
              "image_url": { "es": "https://picsum.photos/seed/onboarding2/800/600", "en": "https://picsum.photos/seed/onboarding2/800/600", "fr": "https://picsum.photos/seed/onboarding2/800/600" }
            },
            {
              "id": 3,
              "title": { "es": "Explora y mide tu progreso", "en": "Explore and track progress", "fr": "Explorez et suivez vos progrès" },
              "description": { "es": "Busca en catálogos globales integrados, añade elementos y registra tu avance al ver, leer o jugar.", "en": "Search integrated global catalogs, add items, and record your progress as you watch, read, or play.", "fr": "Recherchez dans des catalogues mondiaux intégrés, ajoutez des éléments et suivez vos progrès au fur et à mesure." },
              "image_url": { "es": "https://picsum.photos/seed/onboarding3/800/600", "en": "https://picsum.photos/seed/onboarding3/800/600", "fr": "https://picsum.photos/seed/onboarding3/800/600" }
            },
            {
              "id": 4,
              "title": { "es": "¡Todo listo para empezar!", "en": "All ready to start!", "fr": "Tout est prêt !" },
              "description": { "es": "Únete hoy y transforma la manera en la que llevas el registro de tus pasatiempos favoritos.", "en": "Join today and transform the way you keep track of your favorite hobbies.", "fr": "Inscrivez-vous aujourd'hui et transformez votre façon de suivre vos passe-temps favoris." },
              "image_url": { "es": "https://picsum.photos/seed/onboarding4/800/600", "en": "https://picsum.photos/seed/onboarding4/800/600", "fr": "https://picsum.photos/seed/onboarding4/800/600" }
            }
          ]
        }
        """.trimIndent()
    }

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            "mantainence" to false,
            "videogame_category_enabled" to false,
            "videogame_target_group" to "B",
            "onboarding_config" to DEFAULT_ONBOARDING_JSON
        ))
    }

    // ── Videogame feature ──────────────────────────────────────────────────────

    /**
     * Devuelve true si el usuario actual pertenece al grupo objetivo
     * y la flag videogame_category_enabled esta en true.
     */
    private suspend fun isVideogameEnabledForCurrentUser(): Boolean {
        val enabled = remoteConfig.getBoolean("videogame_category_enabled")
        if (!enabled) return false

        val targetGroup = remoteConfig.getString("videogame_target_group")
            .ifBlank { "B" }
            .uppercase()
        if (targetGroup == "ALL") return true

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val userGroupSnapshot = FirebaseDatabase.getInstance().reference
            .child("users").child(userId).child("abGroup")
            .get().await()

        val userGroup = userGroupSnapshot.getValue(String::class.java)
            ?.trim()?.uppercase()?.takeIf { it == "A" || it == "B" } ?: "A"

        return userGroup == targetGroup
    }

    private fun videogameCategoryEnabledFlow(): Flow<Boolean> = callbackFlow {

        /** Emite el valor actual Y reprograma el worker con el intervalo correcto. */
        suspend fun emitAndSchedule(enabled: Boolean) {
            trySend(enabled)
            ABTestingScheduler(FirebaseApp.getInstance().applicationContext)
                .scheduleForEnabled(enabled)
        }

        suspend fun fetchAndEmit() {
            try { remoteConfig.fetchAndActivate().await() } catch (_: Exception) {}
            emitAndSchedule(isVideogameEnabledForCurrentUser())
        }

        fetchAndEmit()

        // Push en tiempo real desde Firebase
        val configListener = object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    launch { emitAndSchedule(isVideogameEnabledForCurrentUser()) }
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        }
        val registration = remoteConfig.addOnConfigUpdateListener(configListener)

        // Escucha cambios en abGroup del usuario en Realtime DB
        val auth = FirebaseAuth.getInstance()
        var userGroupListener: ValueEventListener? = null
        var userGroupRef = auth.currentUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("abGroup")
        }

        userGroupRef?.let { ref ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    launch { emitAndSchedule(isVideogameEnabledForCurrentUser()) }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            userGroupListener = listener
            ref.addValueEventListener(listener)
        }

        // Recalcula cuando cambia el usuario logueado
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userGroupListener?.let { userGroupRef?.removeEventListener(it) }
            userGroupRef = firebaseAuth.currentUser?.uid?.let { uid ->
                FirebaseDatabase.getInstance().reference.child("users").child(uid).child("abGroup")
            }
            userGroupRef?.let { ref ->
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        launch { emitAndSchedule(isVideogameEnabledForCurrentUser()) }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                }
                userGroupListener = listener
                ref.addValueEventListener(listener)
            }
            launch { emitAndSchedule(isVideogameEnabledForCurrentUser()) }
        }
        auth.addAuthStateListener(authListener)

        // Polling periodico como respaldo
        val pollingJob = launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                fetchAndEmit()
            }
        }

        awaitClose {
            registration.remove()
            pollingJob.cancel()
            auth.removeAuthStateListener(authListener)
            userGroupListener?.let { userGroupRef?.removeEventListener(it) }
        }
    }

    private val _videogameEnabled: StateFlow<Boolean> by lazy {
        videogameCategoryEnabledFlow()
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, false)
    }

    actual fun observeVideogameCategoryEnabled(): Flow<Boolean> = _videogameEnabled

    // ── Maintenance ────────────────────────────────────────────────────────────

    actual fun observeMaintenance(): Flow<Boolean> = callbackFlow {

        suspend fun fetchAndEmit() {
            try { remoteConfig.fetchAndActivate().await() } catch (_: Exception) {}
            val isMaintenance = remoteConfig.getBoolean("mantainence")
            AppSettingsStore.setUnderMaintenance(isMaintenance)
            trySend(isMaintenance)
        }

        fetchAndEmit()

        val listener = object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    val isMaintenance = remoteConfig.getBoolean("mantainence")
                    AppSettingsStore.setUnderMaintenance(isMaintenance)
                    trySend(isMaintenance)
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        }
        val registration = remoteConfig.addOnConfigUpdateListener(listener)

        val pollingJob = launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                fetchAndEmit()
            }
        }

        awaitClose {
            registration.remove()
            pollingJob.cancel()
        }
    }

    // ── Onboarding config ───────────────────────────────────────────────────────

    actual suspend fun fetchOnboardingConfig(): String {
        try { remoteConfig.fetchAndActivate().await() } catch (_: Exception) {}
        return remoteConfig.getString("onboarding_config")
    }
}
