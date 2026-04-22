package com.ucb.proyectofinal.feature.notes.domain.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ucb.proyectofinal.worker.LogUploadWorker

class AndroidSyncController(private val context: Context) : SyncController {
    override fun triggerImmediateSync() {
        val request = OneTimeWorkRequest.Builder(LogUploadWorker::class.java)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "immediateSync",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
