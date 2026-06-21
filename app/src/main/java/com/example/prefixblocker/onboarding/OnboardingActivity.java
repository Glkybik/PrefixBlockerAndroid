package com.example.prefixblocker.onboarding;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.prefixblocker.MainActivity;
import com.example.prefixblocker.R;
import com.example.prefixblocker.data.PrefsManager;
import com.google.android.material.button.MaterialButton;

public class  OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Применяем тему перед загрузкой layout
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        MaterialButton btnStart = findViewById(R.id.btn_start);

        btnStart.setOnClickListener(v -> {
            PrefsManager prefsManager = new PrefsManager(this);
            prefsManager.setFirstLaunchDone();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                startActivity(intent);
            }

            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void applyTheme() {
        PrefsManager prefs = new PrefsManager(this);
        int theme = prefs.getTheme();
        if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}