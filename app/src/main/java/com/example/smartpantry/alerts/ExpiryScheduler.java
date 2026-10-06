package com.example.smartpantry.alerts;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.smartpantry.util.SettingsManager;

import java.util.concurrent.TimeUnit;

/** Starts and stops the repeating expiry check. */
public final class ExpiryScheduler {

    private static final String WORK_NAME = "expiry_check";
    private static final long REPEAT_HOURS = 12;

    private ExpiryScheduler() {
    }

    /** Runs the check every 12 hours while the device is online. Safe to call repeatedly. */
    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                ExpiryCheckWorker.class, REPEAT_HOURS, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
        // KEEP: if the job is already scheduled, leave it alone
        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(WORK_NAME);
    }

    /** Makes the scheduled job match the saved setting. Called when the app starts. */
    public static void sync(Context context) {
        if (new SettingsManager(context).isExpiryAlertsEnabled()) {
            schedule(context);
        } else {
            cancel(context);
        }
    }
}