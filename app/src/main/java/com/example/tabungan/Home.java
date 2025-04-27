package com.example.tabungan;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.tabungan.komponen.TransaksiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private TextView saldoTv;
    private ImageView iconSaldo;
    private boolean isSaldoVisible = false;
    private DatabaseReference dbRef;
    private String userId;
    private NestedScrollView scrollContainer;
    private ArrayList<TransaksiModel> listTransaksi = new ArrayList<>();
    private int totalSetoran = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        initializeFirebase();
        setupListeners();
        loadTransaksi(); // Load transaksi duluan supaya total saldo ketemu
        NavigasiBar.setup(this);
    }

    private void initializeViews() {
        saldoTv = findViewById(R.id.saldo);
        iconSaldo = findViewById(R.id.iconSaldo);
        scrollContainer = findViewById(R.id.scrolltransaksi);
        findViewById(R.id.setoran).setOnClickListener(v -> filterTransactions(true));
        findViewById(R.id.penarikan).setOnClickListener(v -> filterTransactions(false));
    }

    private void initializeFirebase() {
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (userId == null) {
            showError("User tidak ditemukan");
            finish();
        } else {
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        }
    }

    private void setupListeners() {
        saldoTv.setOnClickListener(v -> toggleSaldoVisibility());
    }

    private void toggleSaldoVisibility() {
        isSaldoVisible = !isSaldoVisible;
        tampilkanSaldo();
    }

    private void tampilkanSaldo() {
        if (isSaldoVisible) {
            saldoTv.setText(formatRupiah(totalSetoran));
        } else {
            saldoTv.setText("Rp ********");
        }
    }

    private void loadTransaksi() {
        if (dbRef == null) return;
        dbRef.child("transaksi").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTransaksi.clear();
                totalSetoran = 0;  // Reset saldo every time data is loaded
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TransaksiModel trx = data.getValue(TransaksiModel.class);
                        if (trx != null && userId.equals(trx.getUid())) {
                            listTransaksi.add(trx);
                            // Adjust totalSetoran based on category
                            if (trx.getKategori() != null) {
                                if (trx.getKategori().equalsIgnoreCase("Setoran")) {
                                    totalSetoran += trx.getNominal();
                                } else if (trx.getKategori().equalsIgnoreCase("Penarikan")) {
                                    totalSetoran -= trx.getNominal();
                                }
                            }
                        }
                    }
                    // Sort transactions by timestamp
                    Collections.sort(listTransaksi, (trx1, trx2) -> Long.compare(trx2.getTimestamp(), trx1.getTimestamp()));
                    tampilkanSaldo();
                    displayTransactions(listTransaksi);
                } else {
                    tampilkanSaldo();
                    LinearLayout transactionContainer = (LinearLayout) scrollContainer.getChildAt(0);
                    if (transactionContainer != null) {
                        transactionContainer.removeAllViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Gagal memuat transaksi");
            }
        });
    }

    private void displayTransactions(ArrayList<TransaksiModel> transactions) {
        LinearLayout transactionContainer = (LinearLayout) scrollContainer.getChildAt(0);
        if (transactionContainer != null) {
            transactionContainer.removeAllViews();
            for (TransaksiModel trx : transactions) {
                View view = getLayoutInflater().inflate(R.layout.item_transaksi, transactionContainer, false);
                setupTransactionView(view, trx);
                transactionContainer.addView(view);
            }
        }
    }

    private void setupTransactionView(View view, TransaksiModel trx) {
        ImageView icon = view.findViewById(R.id.ic_transaksi);
        TextView label = view.findViewById(R.id.tvkategori);
        TextView tanggal = view.findViewById(R.id.tanggal);
        TextView amount = view.findViewById(R.id.nominal);

        tanggal.setText(trx.getTanggal());
        amount.setText(formatRupiah(trx.getNominal()));

        if (trx.getKategori() != null && trx.getKategori().equalsIgnoreCase("Setoran")) {
            icon.setImageResource(R.drawable.panah2);
            icon.setBackgroundResource(R.drawable.bg_circle_hijau);
            amount.setTextColor(Color.parseColor("#008000"));
            label.setText("Setoran");
        } else {
            icon.setImageResource(R.drawable.panah);
            icon.setBackgroundResource(R.drawable.bg_circle_merah);
            amount.setTextColor(Color.parseColor("#A80000"));
            label.setText("Penarikan");
        }
    }

    private void filterTransactions(boolean isSetoran) {
        ArrayList<TransaksiModel> filteredList = new ArrayList<>();
        for (TransaksiModel trx : listTransaksi) {
            if ((trx.getKategori() != null && trx.getKategori().equalsIgnoreCase("Setoran")) == isSetoran) {
                filteredList.add(trx);
            }
        }
        displayTransactions(filteredList);
    }

    private String formatRupiah(int amount) {
        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format(amount);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
