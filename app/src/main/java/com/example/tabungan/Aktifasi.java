package com.example.tabungan;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.tabungan.komponen.TransaksiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Aktifasi extends AppCompatActivity {

    private DatabaseReference dbRef;
    private String userId;
    private NestedScrollView scrollContainer;
    private ArrayList<TransaksiModel> listTransaksi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktifasi);

        // Initialize views and Firebase
        scrollContainer = findViewById(R.id.scrolltransaksi);
        NavigasiBar.setup(this);

        initializeFirebase();
        loadTransaksi();  // Load transaksi saat aplikasi dibuka
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

    private void loadTransaksi() {
        if (dbRef == null) return;

        dbRef.child("transaksi").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listTransaksi.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TransaksiModel trx = data.getValue(TransaksiModel.class);
                        if (trx != null && userId.equals(trx.getUid())) {
                            listTransaksi.add(trx);
                        }
                    }

                    // Sort transactions by timestamp
                    Collections.sort(listTransaksi, (trx1, trx2) -> Long.compare(trx2.getTimestamp(), trx1.getTimestamp()));
                    displayTransactions(listTransaksi);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showError("Gagal memuat transaksi");
            }
        });
    }

    private void displayTransactions(ArrayList<TransaksiModel> transactions) {
        LinearLayout transactionContainer = findViewById(R.id.transactionContainer);
        if (transactionContainer != null) {
            transactionContainer.removeAllViews();
            for (TransaksiModel trx : transactions) {
                View view = getLayoutInflater().inflate(R.layout.item_activitas, transactionContainer, false);
                setupTransactionView(view, trx);
                transactionContainer.addView(view);
            }
        }
    }

    private void setupTransactionView(View view, TransaksiModel trx) {
        TextView kategori = view.findViewById(R.id.tvkategori);
        TextView tanggal = view.findViewById(R.id.tanggal);

        kategori.setText(trx.getKategori());
        tanggal.setText(trx.getTanggal());
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
