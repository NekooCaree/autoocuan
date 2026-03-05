package com.example.autocuanumkm.data.model

// ═══════════════════════════════════════════════════════════════════════════
//  HomeModels.kt  — Data class / Model untuk Halaman Beranda
//
//  Berisi model:
//    • ProfilPengguna  — Informasi akun pemilik usaha
//    • RingkasanUsaha  — Statistik usaha (pendapatan, transaksi, stok)
//    • DataBeranda     — Gabungan profil + ringkasan (satu respons API)
//    • ResponApi       — Wrapper generik untuk semua respons dari server
// ═══════════════════════════════════════════════════════════════════════════

/**
 * Data profil pemilik usaha yang ditampilkan di layar beranda.
 *
 * Gson akan memetakan otomatis: "nama_usaha" di JSON → [namaUsaha] di Kotlin
 * jika RetrofitClient dikonfigurasi dengan FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.
 */
data class ProfilPengguna(
    val id        : Int    = 0,
    val nama      : String = "",  // Nama pemilik, misal: "Siti Rahayu"
    val namaUsaha : String = "",  // Nama toko/usaha, misal: "Warung Bu Siti"
    val noTelepon : String = "",  // Nomor HP terdaftar
    val token     : String = ""   // Token autentikasi setelah login
)

/**
 * Ringkasan performa usaha untuk ditampilkan dalam kartu di beranda.
 * Data ini diambil dari API setiap kali layar beranda dibuka.
 */
data class RingkasanUsaha(
    val pendapatanHariIni     : Long = 0L, // Dalam satuan Rupiah, misal: 1500000
    val totalTransaksiHariIni : Int  = 0,  // Jumlah transaksi hari ini
    val stokMenipis           : Int  = 0   // Produk dengan stok hampir habis
)

/**
 * Gabungan profil pengguna + ringkasan usaha.
 * Dikembalikan oleh endpoint: GET /api/v1/beranda/{userId}
 */
data class DataBeranda(
    val profil    : ProfilPengguna = ProfilPengguna(),
    val ringkasan : RingkasanUsaha = RingkasanUsaha()
)

/**
 * Wrapper generik untuk semua respons API dari server.
 *
 * Format JSON yang diharapkan:
 * {
 *   "sukses": true,
 *   "pesan": "Data berhasil dimuat",
 *   "data": { ... }
 * }
 *
 * @param T Tipe data payload, misal: [DataBeranda], [ProfilPengguna], dsb.
 */
data class ResponApi<T>(
    val sukses : Boolean = false,
    val pesan  : String  = "",
    val data   : T?      = null  // Nullable — null jika terjadi error di sisi server
)
