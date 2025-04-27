package com.example.tabungan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailEditText, passwordEditText;
    private Button btnLogin, btnRegister;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initializeUI();
        applySystemBarInsets();
        setButtonListeners();
    }

    private void initializeUI() {
        emailLayout = findViewById(R.id.email);
        passwordLayout = findViewById(R.id.password);
        emailEditText = (TextInputEditText) emailLayout.getEditText();
        passwordEditText = (TextInputEditText) passwordLayout.getEditText();
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
    }

    private void applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setButtonListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void attemptLogin() {
        String email = getTextInput(emailEditText);
        String password = getTextInput(passwordEditText);

        if (isValidInput(email, password)) {
            showProgress(true);
            loginUser(email, password);
        }
    }

    private boolean isValidInput(String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email wajib diisi");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password wajib diisi");
            isValid = false;
        }

        return isValid;
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        handleLoginSuccess();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private void handleLoginSuccess() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            showToast("Login berhasil!");
            navigateToHome();
        }
    }

    private void handleLoginFailure(Exception exception) {
        String errorMessage = (exception != null) ? exception.getMessage() : "Coba Beberapa saat lagi";
        showToast("Login gagal: " + errorMessage);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void navigateToHome() {
        startActivity(new Intent(Login.this, Home.class));
        finishAffinity();
    }

    private void navigateToRegister() {
        startActivity(new Intent(Login.this, Register.class));
        finish();
    }

    private String getTextInput(TextInputEditText editText) {
        return (editText != null) ? editText.getText().toString().trim() : "";
    }
}
