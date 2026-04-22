package com.ucb.proyectofinal.worker

import com.google.firebase.database.FirebaseDatabase
import com.ucb.proyectofinal.core.data.db.TodoDao
import com.ucb.proyectofinal.core.data.db.TodoEntity
import kotlinx.coroutines.tasks.await

/**
 * Caso de uso de sincronización offline.
 *
 * Flujo:
 *  1. Lee todos los [TodoEntity] con isPending=true desde Room.
 *  2. Sube cada uno a Firebase Realtime Database bajo /sync_queue/{id}.
 *  3. Marca el ítem como sincronizado en Room (isPending=false, syncedAt=now).
 *  4. Devuelve el conteo de ítems procesados exitosamente.
 */
class SyncPendingItemsUseCase(
    private val dao: TodoDao
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            val pending = dao.getPendingTodos()
            if (pending.isEmpty()) return Result.success(0)

            val dbRef = FirebaseDatabase.getInstance()
                .getReference("sync_queue")

            var syncedCount = 0
            val now = System.currentTimeMillis()

            for (item in pending) {
                val payload = mapOf(
                    "id"          to item.id,
                    "title"       to item.title,
                    "description" to item.description,
                    "isCompleted" to item.isCompleted,
                    "syncedAt"    to now
                )
                // Sube el ítem a Firebase RTDB
                dbRef.child(item.id.toString()).setValue(payload).await()
                // Marca como sincronizado en Room
                dao.markAsSynced(item.id, now)
                syncedCount++
            }

            Result.success(syncedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
