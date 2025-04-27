package com.example.tabungan;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigasiBar {

    public static void setup(final Activity activity) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);

        int id = getNavigationMenuItemId(activity);
        if (id != 0) {
            bottomNavigationView.setSelectedItemId(id);
        }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    if (!(activity instanceof Home)) {
                        activity.startActivity(new Intent(activity, Home.class));
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_pengaturan) {
                    if (!(activity instanceof Pengaturan)) {
                        activity.startActivity(new Intent(activity, Pengaturan.class));
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_tabungan) {
                    if (!(activity instanceof CatatanKeuangan)) {
                        activity.startActivity(new Intent(activity, CatatanKeuangan.class));
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_aktivasi) {
                    if (!(activity instanceof Aktifasi)) {
                        activity.startActivity(new Intent(activity, Aktifasi.class));
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (!(activity instanceof LamanProfile)) {
                        activity.startActivity(new Intent(activity, LamanProfile.class));
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private static int getNavigationMenuItemId(Activity activity) {
        if (activity instanceof Home) {
            return R.id.nav_home;
        } else if (activity instanceof Pengaturan) {
            return R.id.nav_pengaturan;
        } else if (activity instanceof CatatanKeuangan) {
            return R.id.nav_tabungan;
        } else if (activity instanceof Aktifasi) {
            return R.id.nav_aktivasi;
        } else if (activity instanceof LamanProfile) {
            return R.id.nav_profile;
        }
        return 0;
    }
}
