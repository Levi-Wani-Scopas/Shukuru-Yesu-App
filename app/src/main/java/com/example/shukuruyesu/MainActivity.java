package com.example.shukuruyesu;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved language before UI loads
        loadSavedLanguage();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.shukuru_yesu);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Setup Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        // Set the icon color here
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load HomeFragment as default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Handle Navigation Item Clicks
// Language
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_language) {
                showLanguageDialog();


// Privacy
            } else if (id == R.id.nav_privacy) {
                loadFragment(new PrivacyFragment());

// Share
            } else if (id == R.id.nav_shareApp) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareMessage = "Check out this app I built:\n\nhttps://github.com/YOUR_GITHUB_LINK";

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                startActivity(Intent.createChooser(shareIntent, "Share via"));

// Help
            } else if (id == R.id.nav_help) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:leviscopas17@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Support Request");

                startActivity(emailIntent);

// About
            } else if (id == R.id.nav_aboutUs) {
                loadFragment(new AboutFragment());
// Exit
            } else if (id == R.id.nav_exit) {
                Toast.makeText(MainActivity.this, R.string.stayed_blessed_see_you, Toast.LENGTH_SHORT).show();
                finishAffinity();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // LANGUAGE SELECTION DIALOG
    private void showLanguageDialog() {

        String[] languages = {"English", "العربية"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_language);

        builder.setItems(languages, (dialog, which) -> {

            if (which == 0) {
                setLocale("en");
            }

            if (which == 1) {
                setLocale("ar");
            }

        });

        builder.show();
    }

    // CHANGE APP LANGUAGE
    private void setLocale(String languageCode) {

        // Save language choice
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_lang", languageCode);
        editor.apply();

        // Apply language
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

    // LOAD SAVED LANGUAGE WHEN APP STARTS
    private void loadSavedLanguage() {

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("app_lang", "");

        if (!language.isEmpty()) {
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(language);
            AppCompatDelegate.setApplicationLocales(appLocale);
        }
    }
}