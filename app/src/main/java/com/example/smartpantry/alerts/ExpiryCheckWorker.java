package com.example.smartpantry.alerts;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.smartpantry.data.ApiClient;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.SettingsManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Background job run by WorkManager roughly twice a day, even when the app is
 * closed. It reads the pantry and posts a notification if anything is expiring.
 *
 * A Worker already runs on a background thread, so it uses Retrofit's blocking
 * execute() instead of the callback-based Repository.
 */
public class ExpiryCheckWorker extends Worker {

    public ExpiryCheckWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SettingsManager settings = new SettingsManager(context);

        if (!settings.isExpiryAlertsEnabled()) {
            return Result.success(); // the user turned alerts off
        }

        try {
            Response<List<PantryItem>> response = ApiClient.getService().getPantryItems().execute();
            if (!response.isSuccessful() || response.body() == null) {
                return Result.retry();
            }
            List<PantryItem> expiring =
                    ExpiryChecker.findExpiringSoon(response.body(), settings.getAlertDays());
            if (!expiring.isEmpty()) {
                NotificationHelper.showExpiryNotification(context, expiring);
            }
            return Result.success();
        } catch (IOException e) {
            return Result.retry(); // server unreachable: try again later
        }
    }
}