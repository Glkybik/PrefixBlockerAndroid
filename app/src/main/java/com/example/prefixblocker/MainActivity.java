package com.example.prefixblocker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prefixblocker.adapters.PrefixAdapter;
import com.example.prefixblocker.data.Prefix;
import com.example.prefixblocker.data.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PrefsManager prefsManager;
    private TextInputEditText etPrefix;
    private MaterialButton btnAdd;
    private RecyclerView rvPrefixes;
    private TextView tvStatus;
    private PrefixAdapter adapter;
    private SwitchMaterial switchTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Применяем тему перед загрузкой layout
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PrefsManager(this);

        etPrefix = findViewById(R.id.et_prefix);
        btnAdd = findViewById(R.id.btn_add);
        rvPrefixes = findViewById(R.id.rv_prefixes);
        tvStatus = findViewById(R.id.tv_status);
        switchTheme = findViewById(R.id.switch_theme);

        // Настройка RecyclerView
        rvPrefixes.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера
        adapter = new PrefixAdapter(prefsManager.getPrefixes(), new PrefixAdapter.OnPrefixActionListener() {
            @Override
            public void onDelete(String prefix) {
                prefsManager.removePrefix(prefix);
                updatePrefixList();
                Toast.makeText(MainActivity.this, "Префикс " + prefix + " удален", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggle(String prefix) {
                prefsManager.togglePrefix(prefix);
                updatePrefixList();
            }
        });
        rvPrefixes.setAdapter(adapter);

        // Кнопка добавления префикса
        btnAdd.setOnClickListener(v -> {
            String prefix = etPrefix.getText().toString().trim();
            if (prefix.isEmpty()) {
                Toast.makeText(this, "Введите префикс (3 цифры)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (prefix.length() != 3) {
                Toast.makeText(this, "Префикс должен состоять из 3 цифр", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!prefix.matches("\\d{3}")) {
                Toast.makeText(this, "Префикс должен содержать только цифры", Toast.LENGTH_SHORT).show();
                return;
            }

            prefsManager.addPrefix(prefix);
            etPrefix.setText("");
            Toast.makeText(this, "Префикс " + prefix + " добавлен", Toast.LENGTH_SHORT).show();
            updatePrefixList();
        });

        checkScreeningStatus();

        Button btnRequestRole = findViewById(R.id.btn_request_role);
        btnRequestRole.setOnClickListener(v -> requestScreeningRole());

        // Настройка переключателя темы
        int currentTheme = prefsManager.getTheme();
        switchTheme.setChecked(currentTheme == 1);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefsManager.saveTheme(1);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefsManager.saveTheme(0);
            }
            recreate(); // Пересоздаем Activity для применения темы
        });

        updatePrefixList();
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

    private void checkScreeningStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tvStatus.setText("Статус: Проверка...");
            tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
        }
    }

    private void requestScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this, "Выберите наше приложение для скрининга звонков", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Требуется Android 10 или выше", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePrefixList() {
        List<Prefix> prefixes = prefsManager.getPrefixes();
        adapter.updateList(prefixes);

        int count = prefixes.size();
        int activeCount = prefsManager.getActivePrefixes().size();
        tvStatus.setText("Префиксов: " + count + " (активных: " + activeCount + ")");
    }
}