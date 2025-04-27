package com.example.tabungan.komponen;

public class TransaksiModel {
    private String kategori, tanggal, keterangan, uid;
    private int nominal;
    private boolean isSetoran;
    private long timestamp;

    public TransaksiModel() {}

    public String getKategori() { return kategori; }
    public String getTanggal() { return tanggal; }
    public String getKeterangan() { return keterangan; }
    public int getNominal() { return nominal; }
    public boolean isSetoran() { return isSetoran; }
    public long getTimestamp() { return timestamp; }
    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
    public void setSetoran(boolean setoran) { this.isSetoran = setoran; }
}
