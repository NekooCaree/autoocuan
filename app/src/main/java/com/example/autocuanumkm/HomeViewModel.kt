package com.example.autocuanumkm.ui.home

// ═══════════════════════════════════════════════════════════════════════════
//  HomeViewModel.kt  — ViewModel untuk Halaman Beranda (MVVM Pattern)
//
//  Tanggung jawab ViewModel:
//    1. Mengelola status UI (Memuat / Sukses / Gagal) via StateFlow
//    2. Memanggil Repository untuk mengambil data
//    3. TIDAK boleh langsung mengakses View/Composable
//
//  Status UI dimodelkan dengan Sealed Class [StatusBeranda].
// ═══════════════════════════════════════════════════════════════════════════

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocuanumkm.data.model.DataBeranda
import com.example.autocuanumkm.data.repository.HasilData
import com.example.autocuanumkm.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Sealed Class: Status Tampilan Beranda ─────────────────────────────────

/**
 * Mewakili tiga kemungkinan status tampilan layar beranda.
 * UI akan bereaksi terhadap setiap perubahan status ini.
 */
sealed class StatusBeranda {
    /** Sedang memuat data dari server — tampilkan indikator loading. */
    object Memuat : StatusBeranda()

    /**
     * Data berhasil diambil — tampilkan konten utama beranda.
     * @param data Berisi profil pengguna dan ringkasan usaha.
     */
    data class Sukses(val data: DataBeranda) : StatusBeranda()

    /**
     * Terjadi kesalahan — tampilkan pesan error dan tombol coba lagi.
     * @param pesan Pesan error yang ramah untuk pengguna lansia.
     */
    data class Gagal(val pesan: String) : StatusBeranda()
}

// ─── ViewModel ─────────────────────────────────────────────────────────────

/**
 * ViewModel untuk layar beranda.
 *
 * Menggunakan [viewModelScope] agar Coroutine otomatis dibatalkan saat
 * ViewModel dihancurkan (tidak ada memory leak).
 */
class HomeViewModel(
    private val repository: HomeRepository = HomeRepository()
) : ViewModel() {

    // ─── State: Status Beranda ───────────────────────────────────────────────

    /**
     * State internal yang hanya bisa diubah dari dalam ViewModel.
     * Nilai awal: [StatusBeranda.Memuat] agar layar menampilkan loading dulu.
     */
    private val _statusBeranda = MutableStateFlow<StatusBeranda>(StatusBeranda.Memuat)

    /**
     * State publik yang bisa diobservasi oleh Composable di UI.
     * Menggunakan [asStateFlow] agar UI tidak bisa mengubah state secara langsung.
     */
    val statusBeranda: StateFlow<StatusBeranda> = _statusBeranda.asStateFlow()

    // ─── Inisialisasi ────────────────────────────────────────────────────────

    init {
        // Gunakan muatDataDemo() untuk menampilkan data contoh (tanpa API)
        // Ganti dengan muatDataDariServer() saat server backend sudah siap
        muatDataDemo()
    }

    // ─── Fungsi: Muat Data Demo ──────────────────────────────────────────────

    /**
     * Memuat data contoh langsung dari kode — tidak perlu koneksi internet.
     * Berguna saat pengembangan atau demonstrasi aplikasi.
     */
    fun muatDataDemo() {
        _statusBeranda.value = StatusBeranda.Memuat

        // Gunakan Coroutine agar tidak memblokir main thread
        viewModelScope.launch {
            when (val hasil = repository.getDataDemo()) {
                is HasilData.Sukses -> _statusBeranda.value = StatusBeranda.Sukses(hasil.data)
                is HasilData.Gagal  -> _statusBeranda.value = StatusBeranda.Gagal(hasil.pesan)
                is HasilData.Memuat -> { /* Status ini tidak digunakan di getDataDemo */ }
            }
        }
    }

    // ─── Fungsi: Muat Data dari Server (API) ────────────────────────────────

    /**
     * Mengambil data beranda dari server backend secara asinkron.
     * Panggil fungsi ini saat server sudah siap dan token tersedia.
     *
     * @param token   Token autentikasi dari proses login (simpan di SharedPreferences/DataStore)
     * @param userId  ID pengguna yang sedang login
     */
    fun muatDataDariServer(token: String, userId: Int) {
        // Set status ke Memuat agar UI menampilkan indikator loading
        _statusBeranda.value = StatusBeranda.Memuat

        viewModelScope.launch {
            when (val hasil = repository.getDataBeranda(token, userId)) {
                is HasilData.Sukses -> {
                    // Data berhasil diambil — update status ke Sukses
                    _statusBeranda.value = StatusBeranda.Sukses(hasil.data)
                }
                is HasilData.Gagal  -> {
                    // Terjadi error — update status ke Gagal dengan pesan error
                    _statusBeranda.value = StatusBeranda.Gagal(hasil.pesan)
                }
                is HasilData.Memuat -> { /* Ditangani oleh Repository */ }
            }
        }
    }

    // ─── Fungsi: Coba Lagi ───────────────────────────────────────────────────

    /**
     * Dipanggil saat pengguna menekan tombol "Coba Lagi" pada layar error.
     * Untuk demo, mengulang [muatDataDemo]. Ganti dengan [muatDataDariServer] saat live.
     */
    fun cobaLagi() {
        muatDataDemo()
        // Untuk produksi: muatDataDariServer(savedToken, savedUserId)
    }
}
