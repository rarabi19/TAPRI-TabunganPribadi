package com.example.tabungan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabungan.komponen.Profile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextInputLayout namaLayout, teleponLayout, emailLayout, passwordLayout;
    private TextInputEditText namaEdit, teleponEdit, emailEdit, passwordEdit;
    private Button registerButton, loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initFirebase();
        setupListeners();
    }

    private void initViews() {
        namaLayout = findViewById(R.id.nama);
        teleponLayout = findViewById(R.id.telepon);
        emailLayout = findViewById(R.id.email);
        passwordLayout = findViewById(R.id.password);

        namaEdit = (TextInputEditText) namaLayout.getEditText();
        teleponEdit = (TextInputEditText) teleponLayout.getEditText();
        emailEdit = (TextInputEditText) emailLayout.getEditText();
        passwordEdit = (TextInputEditText) passwordLayout.getEditText();

        registerButton = findViewById(R.id.btn_register);
        loginButton = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> navigateToLogin());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = getText(namaEdit);
        String phone = getText(teleponEdit);
        String email = getText(emailEdit);
        String password = getText(passwordEdit);

        if (username.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Profile newProfile = new Profile(username, phone, email, "", "");  // Alamat dan profesi kosong pada awalnya

                        // Menyimpan data profil ke dalam "Profiles"
                        saveProfileToDatabase(userId, newProfile);

                        navigateToLogin(); // Berpindah ke login setelah registrasi berhasil
                    } else {
                        Toast.makeText(this, "Registrasi gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProfileToDatabase(String userId, Profile profile) {
        FirebaseDatabase.getInstance().getReference("Profiles")
                .child(userId).setValue(profile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Profil berhasil disimpan
                    } else {
                        // Gagal menyimpan profil
                    }
                });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, Login.class));
    }

    private String getText(TextInputEditText editText) {
        return editText != null ? editText.getText().toString().trim() : "";
    }
}
