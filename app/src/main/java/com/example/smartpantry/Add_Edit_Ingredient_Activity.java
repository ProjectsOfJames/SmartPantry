package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.data.Repository;
import com.example.smartpantry.data.RepositoryImpl;
import com.example.smartpantry.data.ResultCallback;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.FormatUtils;
import com.example.smartpantry.util.IntentKeys;
import com.example.smartpantry.util.SettingsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

/**
 * The Add/Edit Ingredient screen.
 *
 * Add mode:  launched with no extras, saves with a Create call.
 * Edit mode: launched with a PantryItem under IntentKeys.EXTRA_PANTRY_ITEM,
 *            the form is pre-filled and saves with an Update call.
 */
public class Add_Edit_Ingredient_Activity extends AppCompatActivity {

    private static final int MAX_NAME_LENGTH = 50;
    private static final double MAX_QUANTITY = 100000;

    private final Repository repository = RepositoryImpl.getInstance();

    private TextInputLayout tilName;
    private TextInputLayout tilQuantity;
    private TextInputLayout tilExpiry;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiry;
    private Spinner spinnerUnit;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    private MaterialButton btnClearExpiry;
    private View progress;

    /** The item being edited, or null in add mode. */
    private PantryItem editingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        bindViews();
        editingItem = readItemExtra();

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit);
        toolbar.setTitle(editingItem == null
                ? R.string.title_add_ingredient : R.string.title_edit_ingredient);
        toolbar.setNavigationOnClickListener(v -> confirmDiscardOrFinish());

        setUpListeners();

        // The system back button gets the same "discard changes?" check
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmDiscardOrFinish();
            }
        });

        // Only fill the form on first creation. After a rotation Android has
        // already restored whatever the user had typed.
        if (savedInstanceState == null) {
            if (editingItem != null) {
                populateForm(editingItem);
            } else {
                spinnerUnit.setSelection(defaultUnitPosition());
            }
        }
    }

    private void bindViews() {
        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        tilExpiry = findViewById(R.id.til_expiry);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiry = findViewById(R.id.et_expiry);
        spinnerUnit = findViewById(R.id.spinner_unit);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnClearExpiry = findViewById(R.id.btn_clear_expiry);
        progress = findViewById(R.id.progress_save);
    }

    private void setUpListeners() {
        btnSave.setOnClickListener(v -> attemptSave());
        btnCancel.setOnClickListener(v -> confirmDiscardOrFinish());

        // Expiry date: tap the field or its calendar icon to pick, button to clear
        etExpiry.setOnClickListener(v -> showDatePicker());
        tilExpiry.setEndIconOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> etExpiry.setText(""));

        // The keyboard's Done key on the quantity field saves the form
        etQuantity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSave();
                return true;
            }
            return false;
        });

        clearErrorWhenTyping(etName, tilName);
        clearErrorWhenTyping(etQuantity, tilQuantity);
    }

    /** Edit mode: reads the PantryItem sent by the Pantry screen's Intent */
    @SuppressWarnings("deprecation")
    private PantryItem readItemExtra() {
        Serializable extra = getIntent().getSerializableExtra(IntentKeys.EXTRA_PANTRY_ITEM);
        return extra instanceof PantryItem ? (PantryItem) extra : null;
    }

    private void populateForm(PantryItem item) {
        etName.setText(item.getName());
        etQuantity.setText(FormatUtils.formatQuantity(item.getQuantity()));
        spinnerUnit.setSelection(indexOfUnit(item.getUnit()));
        if (item.getExpiryDate() != null) {
            etExpiry.setText(item.getExpiryDate());
        }
    }

    /** The unit type chosen in Settings decides which unit is pre-selected. */
    private int defaultUnitPosition() {
        return indexOfUnit(new SettingsManager(this).getDefaultUnit());
    }

    private int indexOfUnit(String unit) {
        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(unit)) {
                return i;
            }
        }
        return 0;
    }

    // date picker

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String current = textOf(etExpiry);
        if (!current.isEmpty()) {
            try {
                String[] parts = current.split("-");
                calendar.set(Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]) - 1,
                        Integer.parseInt(parts[2]));
            } catch (Exception ignored) {
                // unreadable text: open on today's date instead
            }
        }
        new DatePickerDialog(this,
                (view, year, month, day) -> etExpiry.setText(formatDate(year, month, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** month is zero-based, as DatePickerDialog supplies it. Returns "yyyy-MM-dd" */
    private static String formatDate(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }

    // Unsaved changes

    /** Leaving with edits would lose them, so ask first. Ignored while a save is running. */
    private void confirmDiscardOrFinish() {
        if (!btnSave.isEnabled()) {
            return; // a save is in progress
        }
        if (!hasUnsavedChanges()) {
            finish();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_discard_title)
                .setMessage(R.string.dialog_discard_message)
                .setPositiveButton(R.string.dialog_discard, (dialog, which) -> finish())
                .setNegativeButton(R.string.dialog_keep_editing, null)
                .show();
    }

    /**
     * Add mode: anything typed counts. Edit mode: anything different from the
     * item that was opened counts.
     */
    private boolean hasUnsavedChanges() {
        String name = textOf(etName).replaceAll("\\s+", " ");
        String quantityText = textOf(etQuantity);
        String expiry = textOf(etExpiry);

        if (editingItem == null) {
            return !name.isEmpty() || !quantityText.isEmpty() || !expiry.isEmpty();
        }

        Double quantity = FormatUtils.parseQuantity(quantityText);
        boolean sameQuantity = quantity != null
                && Double.compare(FormatUtils.roundToTwoDecimals(quantity), editingItem.getQuantity()) == 0;
        boolean sameUnit = spinnerUnit.getSelectedItem().toString()
                .equalsIgnoreCase(editingItem.getUnit());
        String originalExpiry = editingItem.getExpiryDate() == null ? "" : editingItem.getExpiryDate();

        return !name.equals(editingItem.getName())
                || !sameQuantity
                || !sameUnit
                || !expiry.equals(originalExpiry);
    }

    // Validation

    /** Validates every field; only if all pass is anything sent to the server. */
    private void attemptSave() {
        tilName.setError(null);
        tilQuantity.setError(null);

        String name = textOf(etName).replaceAll("\\s+", " ");
        boolean nameOk = validateName(name);
        double quantity = validateQuantity(textOf(etQuantity));
        boolean quantityOk = quantity > 0;

        if (!nameOk) {
            etName.requestFocus();
        } else if (!quantityOk) {
            etQuantity.requestFocus();
        }
        if (!nameOk || !quantityOk) {
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = textOf(etExpiry);
        save(name, quantity, unit, expiry.isEmpty() ? null : expiry);
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_required));
            return false;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            tilName.setError(getString(R.string.error_name_too_long));
            return false;
        }
        boolean hasLetter = false;
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLetter(name.charAt(i))) {
                hasLetter = true;
                break;
            }
        }
        if (!hasLetter) {
            tilName.setError(getString(R.string.error_name_invalid));
            return false;
        }
        return true;
    }

    /** @return the validated quantity, or -1 after showing an error on the field */
    private double validateQuantity(String text) {
        if (text.isEmpty()) {
            tilQuantity.setError(getString(R.string.error_quantity_required));
            return -1;
        }
        Double parsed = FormatUtils.parseQuantity(text);
        if (parsed == null) {
            tilQuantity.setError(getString(R.string.error_quantity_not_number));
            return -1;
        }
        double rounded = FormatUtils.roundToTwoDecimals(parsed);
        if (rounded <= 0) {
            tilQuantity.setError(getString(R.string.error_quantity_invalid));
            return -1;
        }
        if (rounded > MAX_QUANTITY) {
            tilQuantity.setError(getString(R.string.error_quantity_too_large));
            return -1;
        }
        return rounded;
    }

    // Save

    /** Create in add mode, Update in edit mode (the id is kept from the edited item). */
    private void save(String name, double quantity, String unit, String expiry) {
        hideKeyboard();
        setSaving(true);

        long id = editingItem != null ? editingItem.getId() : 0;
        PantryItem item = new PantryItem(id, name, quantity, unit, expiry);

        ResultCallback<PantryItem> callback = new ResultCallback<PantryItem>() {
            @Override
            public void onSuccess(PantryItem saved) {
                if (isDestroyed()) return;
                Toast.makeText(Add_Edit_Ingredient_Activity.this,
                        getString(R.string.item_saved, saved.getName()),
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                if (isDestroyed()) return;
                setSaving(false);
                Snackbar.make(findViewById(android.R.id.content), message,
                        Snackbar.LENGTH_LONG).show();
            }
        };

        if (editingItem == null) {
            repository.addPantryItem(item, callback);
        } else {
            repository.updatePantryItem(item, callback);
        }
    }

    /** Disables the form while a request is in flight to prevent double submits. */
    private void setSaving(boolean saving) {
        progress.setVisibility(saving ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!saving);
        btnCancel.setEnabled(!saving);
        btnClearExpiry.setEnabled(!saving);
        etName.setEnabled(!saving);
        etQuantity.setEnabled(!saving);
        spinnerUnit.setEnabled(!saving);
    }

    // Helpers

    private static String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            }
        }
    }

    /** Removes a field's error message as soon as the user starts correcting it. */
    private static void clearErrorWhenTyping(TextInputEditText field, TextInputLayout layout) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                layout.setError(null);
            }
        });
    }
}