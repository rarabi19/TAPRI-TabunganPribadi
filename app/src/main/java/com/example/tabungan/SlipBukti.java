package com.example.tabungan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabungan.komponen.TransaksiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SlipBukti extends AppCompatActivity {

    private TextView tvTanggal, tvKategori, tvKeterangan, tvNominal, tvTotal, tvBiayaAdmin;
    private Button btnOk;
    private DatabaseReference dbRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip_bukti);

        NavigasiBar.setup(this);
        inisialisasiViews();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transaksi");

        muatDataTransaksi();
        aturListenerTombol();
    }

    private void inisialisasiViews() {
        tvTanggal = findViewById(R.id.tanggal);
        tvKategori = findViewById(R.id.kategori);
        tvKeterangan = findViewById(R.id.keterangan);
        tvNominal = findViewById(R.id.nominal);
        tvTotal = findViewById(R.id.total);
        tvBiayaAdmin = findViewById(R.id.biayaAdmin);
        btnOk = findViewById(R.id.btn_ok);
    }

    private void muatDataTransaksi() {
        Query query = dbRef.orderByChild("uid").equalTo(userId).limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TransaksiModel transaksi = data.getValue(TransaksiModel.class);
                        if (transaksi != null) {
                            perbaruiUIDenganTransaksi(transaksi);
                        }
                    }
                } else {
                    tampilkanToast("Data tidak ditemukan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tampilkanToast("Gagal memuat data");
            }
        });
    }

    private void perbaruiUIDenganTransaksi(TransaksiModel transaksi) {
        tvTanggal.setText(transaksi.getTanggal());
        tvKategori.setText(transaksi.getKategori());
        tvKeterangan.setText(transaksi.getKeterangan());

        String formattedNominal = formatRupiah(transaksi.getNominal());
        tvNominal.setText(formattedNominal);
        tvTotal.setText(formattedNominal);
    }

    private String formatRupiah(int jumlah) {
        return "Rp " + String.format("%,d", jumlah).replace(',', '.') + ",-";
    }

    private void aturListenerTombol() {
        btnOk.setOnClickListener(v -> navigasiKeHome());
    }

    private void navigasiKeHome() {
        startActivity(new Intent(SlipBukti.this, Home.class));
        finish();
    }

    private void tampilkanToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}
