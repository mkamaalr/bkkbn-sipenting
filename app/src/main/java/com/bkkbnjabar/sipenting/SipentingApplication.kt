package com.bkkbnjabar.sipenting

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bkkbnjabar.sipenting.workers.SyncWorker
import com.google.firebase.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
// ============================ THE FIX IS HERE (Part 1) ============================
// The class must declare that it implements Configuration.Provider
class SipentingApplication : Application(), Configuration.Provider {
// ==================================================================================

    // Hilt will inject the factory for your workers
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Schedule the background sync task
        setupRecurringWork()
    }

    // ============================ THE FIX IS HERE (Part 2) ============================
    // This override is now correct because the class implements the interface
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    // ==================================================================================

    private fun setupRecurringWork() {
        // Define constraints for the sync task
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when there's internet
            .build()

        // Create a periodic work request to run every 6 hours
        val repeatingRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "app-sync",
            ExistingPeriodicWorkPolicy.KEEP, // Keep the existing work if it's already scheduled
            repeatingRequest
        )
    }
}
