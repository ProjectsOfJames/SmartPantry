package com.example.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.util.IntentKeys;

// TEMPORARY so the Pantry screen can already navigate here
public class Add_Edit_Ingredient_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getIntent().hasExtra(IntentKeys.EXTRA_PANTRY_ITEM)) {
            toolbar.setTitle(R.string.title_edit_ingredient);
        }
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }
}