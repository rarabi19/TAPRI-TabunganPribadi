package com.example.tabungan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CatatanKeuangan extends AppCompatActivity {

    private TextView tvKategori;
    private TextInputEditText etKeterangan, etTanggal, etNominal;
    private Button btnOk;
    private String kategori = "";

    private DatabaseReference dbRef;
    private String userId;
    private int currentSaldo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan_keuangan);

        NavigasiBar.setup(this);
        tvKategori = findViewById(R.id.pilihKategori);
        etKeterangan = findViewById(R.id.keterangan);
        etTanggal = findViewById(R.id.tanggal);
        etNominal = findViewById(R.id.nominal);
        btnOk = findViewById(R.id.btn_ok);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        findViewById(R.id.setoran).setOnClickListener(v -> setKategori("Setoran"));
        findViewById(R.id.penarikan).setOnClickListener(v -> setKategori("Penarikan"));
        etTanggal.setOnClickListener(v -> showDatePicker());
        btnOk.setOnClickListener(v -> submitTransaction());

        // Memuat saldo saat aplikasi dimulai
        loadSaldo();
    }

    private void setKategori(String selectedKategori) {
        kategori = selectedKategori;
        tvKategori.setText("Kategori: " + selectedKategori);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                etTanggal.setText(dayOfMonth + "/" + (month + 1) + "/" + year)
                , cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitTransaction() {
        String keterangan = etKeterangan.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
        String nominalStr = etNominal.getText().toString().trim();

        if (kategori.isEmpty() || keterangan.isEmpty() || tanggal.isEmpty() || nominalStr.isEmpty()) {
            showToast("Lengkapi semua data!");
            return;
        }

        int nominal;
        try {
            nominal = Integer.parseInt(nominalStr);
        } catch (NumberFormatException e) {
            showToast("Nominal tidak valid");
            return;
        }

        boolean isSetoran = kategori.equals("Setoran");
        int updatedSaldo = isSetoran ? currentSaldo + nominal : currentSaldo - nominal;

        if (updatedSaldo < 0) {
            showToast("Saldo tidak cukup untuk penarikan");
            return;
        }

        String trxId = dbRef.child("transaksi").push().getKey();
        if (trxId == null) {
            showToast("Gagal membuat ID transaksi");
            return;
        }

        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("kategori", kategori);
        transaksi.put("tanggal", tanggal);
        transaksi.put("keterangan", keterangan);
        transaksi.put("nominal", nominal);
        transaksi.put("isSetoran", isSetoran);
        transaksi.put("timestamp", System.currentTimeMillis());
        transaksi.put("uid", userId);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("transaksi/" + trxId, transaksi);
        updateMap.put("saldo", updatedSaldo); // Update saldo in database

        dbRef.updateChildren(updateMap).addOnSuccessListener(aVoid -> {
            showToast("Transaksi berhasil");
            loadSaldo();  // Reload updated saldo
            startActivity(new Intent(this, SlipBukti.class));
            finish();
        }).addOnFailureListener(e -> showToast("Gagal menyimpan transaksi"));
    }

    private void loadSaldo() {
        dbRef.child("saldo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentSaldo = snapshot.getValue(Integer.class);
                } else {
                    currentSaldo = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Gagal memuat saldo");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
