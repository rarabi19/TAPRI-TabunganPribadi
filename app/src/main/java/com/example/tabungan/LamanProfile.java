package com.example.tabungan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.tabungan.komponen.Profile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ValueEventListener;

public class LamanProfile extends AppCompatActivity {

    private TextInputLayout layoutNama, layoutTelepon, layoutEmail, layoutAlamat, layoutProfesi;
    private TextInputEditText inputNama, inputTelepon, inputEmail, inputAlamat, inputProfesi;
    private Button btnEditProfile;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private FirebaseUser currentUser;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initFirebase();
        loadUserProfile();
        setupListeners();
        NavigasiBar.setup(this);
    }

    private void initViews() {
        layoutNama = findViewById(R.id.nama);
        layoutTelepon = findViewById(R.id.telepon);
        layoutEmail = findViewById(R.id.email);
        layoutAlamat = findViewById(R.id.alamat);
        layoutProfesi = findViewById(R.id.profesi);

        inputNama = (TextInputEditText) layoutNama.getEditText();
        inputTelepon = (TextInputEditText) layoutTelepon.getEditText();
        inputEmail = (TextInputEditText) layoutEmail.getEditText();
        inputAlamat = (TextInputEditText) layoutAlamat.getEditText();
        inputProfesi = (TextInputEditText) layoutProfesi.getEditText();

        btnEditProfile = findViewById(R.id.btn_edit_profile);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Profiles");
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            database.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        populateUserProfile(snapshot);
                    } else {
                        showToast("Profil tidak ditemukan, harap isi profil Anda.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Gagal memuat data: " + error.getMessage());
                }
            });
        }
    }

    private void populateUserProfile(DataSnapshot snapshot) {
        Profile profile = snapshot.getValue(Profile.class);
        if (profile != null) {
            inputNama.setText(profile.getNama());
            inputTelepon.setText(profile.getTelpon());
            inputEmail.setText(profile.getEmail());
            inputAlamat.setText(profile.getAlamat());
            inputProfesi.setText(profile.getProfesi());
        }
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            if (isEditing) {
                saveProfileChanges();
            } else {
                enableEditing(true);
                btnEditProfile.setText("Simpan");
                isEditing = true;
            }
        });
    }

    private void enableEditing(boolean enable) {
        inputNama.setEnabled(enable);
        inputTelepon.setEnabled(enable);
        inputEmail.setEnabled(enable);
        inputAlamat.setEnabled(enable);
        inputProfesi.setEnabled(enable);
    }

    private void saveProfileChanges() {
        String nama = getText(inputNama);
        String telpon = getText(inputTelepon);
        String email = getText(inputEmail);
        String alamat = getText(inputAlamat);
        String profesi = getText(inputProfesi);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Profile updatedProfile = new Profile(nama, telpon, email, alamat, profesi);

            database.child(userId).setValue(updatedProfile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Data berhasil diperbarui");
                            resetEditMode();
                        } else {
                            showToast("Gagal memperbarui data");
                        }
                    });
        }
    }

    private String getText(TextInputEditText editText) {
        return editText != null ? editText.getText().toString().trim() : "";
    }

    private void showToast(String message) {
        Toast.makeText(LamanProfile.this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetEditMode() {
        enableEditing(false);
        btnEditProfile.setText("Edit");
        isEditing = false;
    }
}
