package com.example.perpustakaan;

public class Book {
    private String judul;
    private String pengarang;
    private int tahun_terbit;
    private String kategori;

    public Book(String judul, String pengarang, int tahun_terbit, String kategori) {
        this.judul = judul;
        this.pengarang = pengarang;
        this.tahun_terbit = tahun_terbit;
        this.kategori = kategori;
    }

    public String getJudul() {
        return judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public int getTahunTerbit() {
        return tahun_terbit;
    }

    public String getKategori() {
        return kategori;
    }
}
