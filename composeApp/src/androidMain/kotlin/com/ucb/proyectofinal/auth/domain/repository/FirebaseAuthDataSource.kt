package com.ucb.proyectofinal.auth.domain.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ucb.proyectofinal.worker.ABTestingScheduler
import kotlinx.coroutines.tasks.await

actual class FirebaseAuthDataSource actual constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun login(email: String, password: String): AuthRemoteUser {
        return runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: error("No se pudo iniciar sesion")
            val normalizedEmail = firebaseUser.email.orEmpty().ifBlank { email }
            val snapshot = database.child("users").child(firebaseUser.uid).get().await()
            val nameFromDb = snapshot.child("name").getValue(String::class.java)
            val resolvedName = nameFromDb ?: firebaseUser.displayName
                ?: normalizedEmail.substringBefore("@")
            val userAssignedGroup = snapshot.child("abGroup").getValue(String::class.java)
                ?.trim()
                ?.uppercase()
                ?.takeIf { it == "A" || it == "B" }
                ?: "A"

            upsertUserProfile(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = resolvedName,
                includeCreatedAt = !snapshot.exists(),
                userGroup = userAssignedGroup,
                includeAbGroup = !snapshot.child("abGroup").exists()
            )

            // Determina si videojuegos esta activo para este usuario y programa el intervalo
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val videogameFlag = remoteConfig.getBoolean("videogame_category_enabled")
            val videogameTarget = remoteConfig.getString("videogame_target_group")
                .ifBlank { "B" }.uppercase()
            val videogameEnabledForUser = videogameFlag &&
                (videogameTarget == "ALL" || userAssignedGroup == videogameTarget)
            ABTestingScheduler(FirebaseApp.getInstance().applicationContext)
                .scheduleForEnabled(videogameEnabledForUser)

            AuthRemoteUser(uid = firebaseUser.uid, email = normalizedEmail, name = resolvedName)
        }.getOrElse { throwable ->
            throw mapAuthException(throwable)
        }
    }

    actual suspend fun register(email: String, password: String, name: String): AuthRemoteUser {
        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: error("No se pudo crear la cuenta")
            val normalizedEmail = firebaseUser.email.orEmpty().ifBlank { email }
            firebaseUser.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()

            upsertUserProfile(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = name,
                includeCreatedAt = true,
                userGroup = "A",
                includeAbGroup = true
            )

            // Nuevo usuario siempre es grupo A; videojuegos dependen del target configurado
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val videogameFlag = remoteConfig.getBoolean("videogame_category_enabled")
            val videogameTarget = remoteConfig.getString("videogame_target_group")
                .ifBlank { "B" }.uppercase()
            val videogameEnabledForNewUser = videogameFlag &&
                (videogameTarget == "ALL" || videogameTarget == "A")
            ABTestingScheduler(FirebaseApp.getInstance().applicationContext)
                .scheduleForEnabled(videogameEnabledForNewUser)

            AuthRemoteUser(uid = firebaseUser.uid, email = normalizedEmail, name = name)
        }.getOrElse { throwable ->
            throw mapAuthException(throwable)
        }
    }

    actual suspend fun logout() {
        auth.signOut()
    }

    actual fun getCurrentUser(): AuthRemoteUser? {
        val user = auth.currentUser ?: return null
        return AuthRemoteUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            name = user.displayName ?: user.email?.substringBefore("@").orEmpty()
        )
    }

    private suspend fun upsertUserProfile(
        uid: String,
        email: String,
        name: String,
        includeCreatedAt: Boolean,
        userGroup: String,
        includeAbGroup: Boolean
    ) {
        val timestamp = System.currentTimeMillis()
        val updates = mutableMapOf<String, Any>(
            "uid" to uid, "email" to email, "name" to name, "updatedAt" to timestamp
        )
        if (includeCreatedAt) updates["createdAt"] = timestamp
        if (includeAbGroup) updates["abGroup"] = userGroup
        database.child("users").child(uid).updateChildren(updates).await()
    }

    private fun mapAuthException(throwable: Throwable): Throwable {
        val code = (throwable as? FirebaseAuthException)?.errorCode
        val message = throwable.message.orEmpty()
        if (code == "ERROR_INTERNAL_ERROR" && message.contains("CONFIGURATION_NOT_FOUND")) {
            return IllegalStateException(
                "Firebase Auth no esta configurado para esta app (CONFIGURATION_NOT_FOUND)."
            )
        }
        if (code == "ERROR_OPERATION_NOT_ALLOWED") {
            return IllegalStateException(
                "El metodo Email/Password esta deshabilitado en Firebase Authentication."
            )
        }
        return throwable
    }
}
