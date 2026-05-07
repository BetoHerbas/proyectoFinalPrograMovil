package com.ucb.proyectofinal.auth.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

actual class FirebaseAuthDataSource actual constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun login(email: String, password: String): AuthRemoteUser {
        return runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: error("No se pudo iniciar sesión")
            val normalizedEmail = firebaseUser.email.orEmpty().ifBlank { email }
            val snapshot = database.child("users").child(firebaseUser.uid).get().await()
            val nameFromDb = snapshot.child("name").getValue(String::class.java)
            val resolvedName = nameFromDb ?: firebaseUser.displayName ?: normalizedEmail.substringBefore("@")

            upsertUserProfile(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = resolvedName,
                includeCreatedAt = !snapshot.exists()
            )

            AuthRemoteUser(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = resolvedName
            )
        }.getOrElse { throwable ->
            throw mapAuthException(throwable)
        }
    }

    actual suspend fun register(email: String, password: String, name: String): AuthRemoteUser {
        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: error("No se pudo crear la cuenta")
            val normalizedEmail = firebaseUser.email.orEmpty().ifBlank { email }
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()

            upsertUserProfile(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = name,
                includeCreatedAt = true
            )

            AuthRemoteUser(
                uid = firebaseUser.uid,
                email = normalizedEmail,
                name = name
            )
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
        includeCreatedAt: Boolean
    ) {
        val timestamp = System.currentTimeMillis()
        val updates = mutableMapOf<String, Any>(
            "uid" to uid,
            "email" to email,
            "name" to name,
            "updatedAt" to timestamp
        )
        if (includeCreatedAt) {
            updates["createdAt"] = timestamp
        }
        database.child("users").child(uid).updateChildren(updates).await()
    }

    private fun mapAuthException(throwable: Throwable): Throwable {
        val authException = throwable as? FirebaseAuthException
        val code = authException?.errorCode
        val message = throwable.message.orEmpty()

        if (code == "ERROR_INTERNAL_ERROR" && message.contains("CONFIGURATION_NOT_FOUND")) {
            return IllegalStateException(
                "Firebase Auth no está configurado para esta app (CONFIGURATION_NOT_FOUND). " +
                    "Verifica en Firebase Console que exista la app Android com.ucb.proyectofinal " +
                    "y que Email/Password esté habilitado en Authentication > Sign-in method."
            )
        }

        if (code == "ERROR_OPERATION_NOT_ALLOWED") {
            return IllegalStateException(
                "El método Email/Password está deshabilitado en Firebase Authentication."
            )
        }

        return throwable
    }
}
