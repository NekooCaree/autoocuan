package com.example.autocuanumkm.data.repository

// ═══════════════════════════════════════════════════════════════════════════
//  HomeRepository.kt  — Lapisan data untuk Halaman Beranda
//
//  Repository bertindak sebagai "jembatan" antara ViewModel dan sumber data
//  (API server). ViewModel tidak perlu tahu dari mana data berasal.
//
//  Menggunakan Sealed Class [HasilData] sebagai pembungkus status:
//    Memuat  → sedang mengambil data
//    Sukses  → data berhasil didapat
//    Gagal   → terjadi kesalahan
// ═══════════════════════════════════════════════════════════════════════════

import com.example.autocuanumkm.data.model.DataBeranda
import com.example.autocuanumkm.data.model.ProfilPengguna
import com.example.autocuanumkm.data.model.RingkasanUsaha
import com.example.autocuanumkm.data.network.RetrofitClient

// ─── Sealed Class Status Data ──────────────────────────────────────────────

/**
 * Mewakili tiga kemungkinan status saat mengambil data dari sumber manapun.
 *
 * @param T Tipe data yang diharapkan saat sukses.
 */
sealed class HasilData<out T> {
    /** Data berhasil diambil. Berisi [data] yang siap ditampilkan. */
    data class Sukses<T>(val data: T) : HasilData<T>()

    /** Terjadi kesalahan. Berisi [pesan] yang akan ditampilkan ke pengguna. */
    data class Gagal(val pesan: String) : HasilData<Nothing>()

    /** Sedang dalam proses pengambilan data (loading). */
    object Memuat : HasilData<Nothing>()
}

// ─── Repository ────────────────────────────────────────────────────────────

/**
 * Repository untuk mengambil data halaman beranda.
 *
 * Semua pemanggilan API dilakukan di sini agar ViewModel tetap bersih
 * dan hanya bertugas mengelola state UI.
 */
class HomeRepository {

    private val apiService = RetrofitClient.apiService

    // ─── Fungsi Utama: Ambil Data dari API ──────────────────────────────────

    /**
     * Mengambil data beranda (profil + ringkasan usaha) dari server.
     *
     * Fungsi ini adalah `suspend` function — harus dipanggil dari dalam Coroutine
     * (misalnya: viewModelScope.launch { ... }).
     *
     * @param token   Token autentikasi pengguna (dari proses login)
     * @param userId  ID pengguna yang ingin dimuat datanya
     * @return [HasilData.Sukses] jika berhasil, atau [HasilData.Gagal] jika gagal
     */
    suspend fun getDataBeranda(token: String, userId: Int): HasilData<DataBeranda> {
        return try {
            val respons = apiService.getDataBeranda("Bearer $token", userId)

            if (respons.sukses && respons.data != null) {
                // Server merespons dengan sukses dan ada data
                HasilData.Sukses(respons.data)
            } else {
                // Server merespons tapi tidak ada data (misal: user tidak ditemukan)
                val pesanError = respons.pesan.ifEmpty { "Data tidak ditemukan di server." }
                HasilData.Gagal(pesanError)
            }
        } catch (e: Exception) {
            // Gagal terhubung ke server — pesan dibuat mudah dimengerti untuk lansia
            HasilData.Gagal(
                "Gagal terhubung ke server.\n\nPastikan koneksi internet Anda aktif, " +
                "lalu ketuk tombol 'Coba Lagi'."
            )
        }
    }

    // ─── Fungsi Cadangan: Data Demo (tanpa koneksi internet) ─────────────────

    /**
     * Mengembalikan data contoh/demo yang sudah dibuat langsung di dalam kode.
     *
     * Gunakan fungsi ini untuk:
     * - Pengembangan & pengujian tampilan (tanpa harus ada server yang berjalan)
     * - Demonstrasi aplikasi kepada klien
     *
     * CATATAN: Hapus atau ganti fungsi ini dengan [getDataBeranda] saat
     *          server backend sudah siap digunakan.
     */
    fun getDataDemo(): HasilData<DataBeranda> {
        val dataDemo = DataBeranda(
            profil = ProfilPengguna(
                id        = 1,
                nama      = "Siti Rahayu",
                namaUsaha = "Warung Makan Bu Siti",
                noTelepon = "08123456789"
            ),
            ringkasan = RingkasanUsaha(
                pendapatanHariIni     = 1_750_000L, // Rp 1.750.000
                totalTransaksiHariIni = 12,
                stokMenipis           = 3
            )
        )
        return HasilData.Sukses(dataDemo)
    }
}
