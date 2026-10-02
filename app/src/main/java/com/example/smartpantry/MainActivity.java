package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.smartpantry.ui.PantryFragment;
import com.example.smartpantry.ui.SuggestedRecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Host activity. Holds the toolbar, the fragment container and the bottom
 * navigation bar. The Pantry and Recipes tabs are fragments swapped into the
 * container; Settings opens as its own activity from the toolbar menu.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                showFragment(new PantryFragment(), R.string.title_pantry);
                return true;
            } else if (id == R.id.nav_recipes) {
                showFragment(new SuggestedRecipesFragment(), R.string.title_recipes);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            // First launch: the Pantry tab is selected by default
            showFragment(new PantryFragment(), R.string.title_pantry);
        } else {
            // After rotation the FragmentManager restores the fragment itself;
            // only the toolbar title needs to be put back.
            updateTitle(bottomNav.getSelectedItemId());
        }
    }

    private void showFragment(Fragment fragment, @StringRes int titleRes) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleRes);
        }
    }

    private void updateTitle(int selectedId) {
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setTitle(selectedId == R.id.nav_recipes
                ? R.string.title_recipes : R.string.title_pantry);
    }

    /** Called by the Recipes screen's "Go to pantry" button */
    public void showPantryTab() {
        bottomNav.setSelectedItemId(R.id.nav_pantry);
    }

    // toolbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, Settings_Activity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}