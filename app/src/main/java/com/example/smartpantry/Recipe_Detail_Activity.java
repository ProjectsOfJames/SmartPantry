package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.util.IntentKeys;

// TEMPORARY STUB so tapping a suggested recipe already navigates here
public class Recipe_Detail_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(IntentKeys.EXTRA_RECIPE_ID, -1);
        TextView name = findViewById(R.id.tv_detail_name);
        name.setText("Recipe #" + recipeId);
    }
}