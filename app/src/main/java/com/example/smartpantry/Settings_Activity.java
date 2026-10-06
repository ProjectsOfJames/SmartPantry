package com.example.smartpantry;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.smartpantry.alerts.ExpiryChecker;
import com.example.smartpantry.alerts.ExpiryScheduler;
import com.example.smartpantry.alerts.NotificationHelper;
import com.example.smartpantry.data.Repository;
import com.example.smartpantry.data.RepositoryImpl;
import com.example.smartpantry.data.ResultCallback;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.SettingsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

/**
 * Settings screen. Every change is saved immediately to SharedPreferences.
 *  - Expiring-soon alerts: switch, plus how many days ahead to warn
 *  - Units preference: which unit type the Add Ingredient form starts on
 */
public class Settings_Activity extends AppCompatActivity {

    private final Repository repository = RepositoryImpl.getInstance();

    private SettingsManager settings;
    private SwitchMaterial switchAlerts;
    private Spinner spinnerDays;
    private RadioGroup rgUnits;
    private MaterialButton btnCheckNow;

    /** True while the screen is filling the controls, so listeners ignore those changes. */
    private boolean loadingUi;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        toolbar.setNavigationOnClickListener(v -> finish());

        settings = new SettingsManager(this);
        switchAlerts = findViewById(R.id.switch_expiry_alerts);
        spinnerDays = findViewById(R.id.spinner_alert_days);
        rgUnits = findViewById(R.id.rg_units);
        btnCheckNow = findViewById(R.id.btn_check_now);

        // Must be registered before the activity starts; used on Android 13+
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) {
                        enableAlerts();
                    } else {
                        setSwitchChecked(false);
                        Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
                    }
                });

        loadSavedSettings();
        setUpListeners();
    }

    private void loadSavedSettings() {
        loadingUi = true;

        boolean alertsOn = settings.isExpiryAlertsEnabled();
        switchAlerts.setChecked(alertsOn);
        spinnerDays.setSelection(indexOfDays(settings.getAlertDays()), false);
        updateAlertControls(alertsOn);

        rgUnits.check(SettingsManager.UNITS_KITCHEN.equals(settings.getUnitSystem())
                ? R.id.rb_kitchen : R.id.rb_metric);

        loadingUi = false;
    }

    private void setUpListeners() {
        switchAlerts.setOnCheckedChangeListener((button, checked) -> {
            if (loadingUi) return;
            if (checked) {
                requestAlertsOn();
            } else {
                disableAlerts();
            }
        });

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (loadingUi) return;
                settings.setAlertDays(Integer.parseInt(
                        getResources().getStringArray(R.array.alert_days)[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        rgUnits.setOnCheckedChangeListener((group, checkedId) -> {
            if (loadingUi) return;
            settings.setUnitSystem(checkedId == R.id.rb_kitchen
                    ? SettingsManager.UNITS_KITCHEN : SettingsManager.UNITS_METRIC);
        });

        btnCheckNow.setOnClickListener(v -> checkNow());
    }

    // alerts

    /** Android 13+ needs the user's permission before any notification can appear. */
    private void requestAlertsOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            enableAlerts();
        }
    }

    private void enableAlerts() {
        settings.setExpiryAlertsEnabled(true);
        ExpiryScheduler.schedule(this);
        setSwitchChecked(true);
        updateAlertControls(true);
    }

    private void disableAlerts() {
        settings.setExpiryAlertsEnabled(false);
        ExpiryScheduler.cancel(this);
        updateAlertControls(false);
    }

    /** The days spinner and test button only make sense while alerts are on. */
    private void updateAlertControls(boolean alertsOn) {
        spinnerDays.setEnabled(alertsOn);
        btnCheckNow.setEnabled(alertsOn);
    }

    /** Changes the switch without triggering its own listener. */
    private void setSwitchChecked(boolean checked) {
        loadingUi = true;
        switchAlerts.setChecked(checked);
        loadingUi = false;
    }

    /** Runs the same check as the background job, immediately, so it can be demonstrated. */
    private void checkNow() {
        int days = settings.getAlertDays();
        btnCheckNow.setEnabled(false);
        repository.getPantryItems(new ResultCallback<List<PantryItem>>() {
            @Override
            public void onSuccess(List<PantryItem> items) {
                if (isDestroyed()) return;
                btnCheckNow.setEnabled(switchAlerts.isChecked());
                List<PantryItem> expiring = ExpiryChecker.findExpiringSoon(items, days);
                if (expiring.isEmpty()) {
                    Toast.makeText(Settings_Activity.this,
                            getString(R.string.toast_no_expiring, days), Toast.LENGTH_SHORT).show();
                } else if (!NotificationHelper.showExpiryNotification(Settings_Activity.this, expiring)) {
                    Toast.makeText(Settings_Activity.this,
                            R.string.toast_notifications_blocked, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                if (isDestroyed()) return;
                btnCheckNow.setEnabled(switchAlerts.isChecked());
                Toast.makeText(Settings_Activity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private int indexOfDays(int days) {
        String[] options = getResources().getStringArray(R.array.alert_days);
        for (int i = 0; i < options.length; i++) {
            if (Integer.parseInt(options[i]) == days) {
                return i;
            }
        }
        return 0;
    }
}