package com.example.tabungan.komponen;

public class Profile {
    private String nama, telpon, email, alamat, profesi;

    public Profile() {
        // Konstruktor kosong ini diperlukan untuk Firebase
    }

    public Profile(String nama, String telpon, String email, String alamat, String profesi) {
        this.nama = nama;
        this.telpon = telpon;
        this.email = email;
        this.alamat = alamat;
        this.profesi = profesi;
    }

    public String getNama() {
        return nama;
    }

    public String getTelpon() {
        return telpon;
    }

    public String getEmail() {
        return email;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getProfesi() {
        return profesi;
    }
}
