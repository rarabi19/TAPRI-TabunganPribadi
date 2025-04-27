package com.example.tabungan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Pengaturan extends AppCompatActivity {

    private Switch switchTema, switchNotifikasi;
    private boolean areNotificationsEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);

        initializeUIComponents();
        loadUserPreferences();
        NavigasiBar.setup(this);
    }

    private void initializeUIComponents() {
        switchTema = findViewById(R.id.switcTema);
        switchNotifikasi = findViewById(R.id.switchNotifikasi);
        
        switchTema.setOnCheckedChangeListener(this::onThemeChanged);
        switchNotifikasi.setOnCheckedChangeListener(this::onNotificationChanged);

        findViewById(R.id.bahasa).setOnClickListener(v -> showLanguageToast());
        findViewById(R.id.btn_keluar).setOnClickListener(v -> handleLogout());
        findViewById(R.id.editprofile).setOnClickListener(v -> showAkunDialog());
        findViewById(R.id.keamanan).setOnClickListener(v -> showKeamananDialog());
        findViewById(R.id.bantuan).setOnClickListener(v -> showBantuanDialog());
        findViewById(R.id.tentang).setOnClickListener(v -> showTentangDialog());
    }

    private void loadUserPreferences() {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean darkModeEnabled = preferences.getBoolean("isDarkModeEnabled", false);
        switchTema.setChecked(darkModeEnabled);
        switchNotifikasi.setChecked(areNotificationsEnabled);
    }

    private void onThemeChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            enableDarkMode();
        } else {
            enableLightMode();
        }
    }

    private void onNotificationChanged(CompoundButton buttonView, boolean isChecked) {
        areNotificationsEnabled = isChecked;
        showToast(isChecked ? "Notifikasi Diaktifkan" : "Notifikasi Dimatikan");
    }

    private void enableDarkMode() {
        setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
        saveUserThemePreference(true);
        showToast("Tema Gelap Diaktifkan");
    }

    private void enableLightMode() {
        setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
        saveUserThemePreference(false);
        showToast("Tema Terang Diaktifkan");
    }

    private void setAppTheme(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void saveUserThemePreference(boolean isDarkModeEnabled) {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isDarkModeEnabled", isDarkModeEnabled);
        editor.apply();
    }

    private void showLanguageToast() {
        showToast("Bahasa: Indonesia");
    }

    private void handleLogout() {
        showToast("Anda telah keluar");
        navigateToLauncher();
    }

    private void navigateToLauncher() {
        Intent intent = new Intent(Pengaturan.this, Launcher.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showAkunDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pengaturan Akun")
                .setItems(new CharSequence[]{"Ganti Email", "Hapus Akun", "Ganti Kata Sandi"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showToast("Ganti Email");
                                    break;
                                case 1:
                                    showToast("Hapus Akun");
                                    break;
                                case 2:
                                    showToast("Ganti Kata Sandi");
                                    break;
                            }
                        })
                .show();
    }

    private void showKeamananDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pengaturan Keamanan")
                .setItems(new CharSequence[]{"Verifikasi Dua Langkah", "Ubah PIN", "Aktifkan Sidik Jari"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showToast("Verifikasi Dua Langkah");
                                    break;
                                case 1:
                                    showToast("Ubah PIN");
                                    break;
                                case 2:
                                    showToast("Aktifkan Sidik Jari");
                                    break;
                            }
                        })
                .show();
    }

    private void showBantuanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bantuan")
                .setItems(new CharSequence[]{"FAQ", "Kontak Dukungan", "Panduan Pengguna"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showToast("FAQ");
                                    break;
                                case 1:
                                    showToast("Kontak Dukungan");
                                    break;
                                case 2:
                                    showToast("Panduan Pengguna");
                                    break;
                            }
                        })
                .show();
    }

    private void showTentangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tentang Aplikasi")
                .setItems(new CharSequence[]{"Tentang Kami", "Versi Aplikasi", "Kebijakan Privasi"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showToast("Tentang Kami");
                                    break;
                                case 1:
                                    showToast("Versi Aplikasi");
                                    break;
                                case 2:
                                    showToast("Kebijakan Privasi");
                                    break;
                            }
                        })
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(Pengaturan.this, message, Toast.LENGTH_SHORT).show();
    }
}
