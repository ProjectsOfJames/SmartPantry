package com.example.smartpantry.alerts;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartpantry.MainActivity;
import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.ExpiryUtils;

import java.util.List;

/** Builds and shows the "items expiring soon" notification. */
public final class NotificationHelper {

    private static final String CHANNEL_ID = "expiry_alerts";
    private static final int NOTIFICATION_ID = 1001;
    private static final int MAX_NAMES_IN_SUMMARY = 3;
    private static final int MAX_LINES_IN_DETAIL = 6;

    private NotificationHelper() {
    }

    /**
     * Shows one notification listing the items. Tapping it opens the app.
     *
     * @return false if the user has blocked notifications, so nothing was shown
     */
    @SuppressLint("MissingPermission") // permission state is checked just below
    public static boolean showExpiryNotification(Context context, List<PantryItem> items) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (items.isEmpty() || !manager.areNotificationsEnabled()) {
            return false;
        }
        createChannel(context);

        Intent openApp = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, openApp, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_pantry)
                .setContentTitle(context.getResources().getQuantityString(
                        R.plurals.notif_title, items.size(), items.size()))
                .setContentText(buildSummary(context, items))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(buildDetail(context, items)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(NOTIFICATION_ID, builder.build());
        return true;
    }

    /** Notification channels are required from Android 8 */
    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.notif_channel_desc));
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /** One line: "Milk, Eggs, Cheese +2 more". */
    private static String buildSummary(Context context, List<PantryItem> items) {
        StringBuilder sb = new StringBuilder();
        int shown = Math.min(items.size(), MAX_NAMES_IN_SUMMARY);
        for (int i = 0; i < shown; i++) {
            if (i > 0) sb.append(", ");
            sb.append(items.get(i).getName());
        }
        if (items.size() > shown) {
            sb.append(" ").append(context.getString(R.string.notif_more, items.size() - shown));
        }
        return sb.toString();
    }

    /** Expanded text: one line per item with its status. */
    private static String buildDetail(Context context, List<PantryItem> items) {
        StringBuilder sb = new StringBuilder();
        int shown = Math.min(items.size(), MAX_LINES_IN_DETAIL);
        for (int i = 0; i < shown; i++) {
            PantryItem item = items.get(i);
            if (i > 0) sb.append("\n");
            sb.append(context.getString(R.string.notif_item_line,
                    item.getName(), statusOf(context, item)));
        }
        if (items.size() > shown) {
            sb.append("\n").append(context.getString(R.string.notif_more, items.size() - shown));
        }
        return sb.toString();
    }

    private static String statusOf(Context context, PantryItem item) {
        Integer days = ExpiryUtils.daysUntilExpiry(item.getExpiryDate());
        if (days == null) return "";
        if (days < 0) return context.getString(R.string.expiry_status_expired);
        if (days == 0) return context.getString(R.string.expiry_status_today);
        if (days == 1) return context.getString(R.string.expiry_status_tomorrow);
        return context.getString(R.string.expiry_status_days, days);
    }
}