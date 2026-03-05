package com.example.autocuanumkm.ui.akun

// ═══════════════════════════════════════════════════════════════════════════
//  ProfileViewModel.kt  — ViewModel untuk Halaman Akun / Profil (MVVM)
//
//  Tanggung jawab:
//    1. Mengelola status UI profil (Memuat / Sukses / Gagal)
//    2. Mengelola proses logout (hapus sesi + sinyal ke UI)
//    3. Memanggil ProfileRepository untuk operasi data
//
//  Menggunakan AndroidViewModel agar bisa mengakses Application context
//  untuk membuat instance SessionManager (SharedPreferences).
// ═══════════════════════════════════════════════════════════════════════════

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocuanumkm.data.local.SessionManager
import com.example.autocuanumkm.data.model.UserProfileResponse
import com.example.autocuanumkm.data.repository.HasilData
import com.example.autocuanumkm.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Sealed Class: Status Tampilan Profil ─────────────────────────────────

/**
 * Mewakili tiga kemungkinan status tampilan layar profil/akun.
 */
sealed class StatusProfil {
    /** Sedang memuat data dari server — tampilkan indikator loading */
    object Memuat : StatusProfil()

    /**
     * Data profil berhasil diambil — tampilkan konten profil.
     * @param data Berisi seluruh informasi profil pengguna.
     */
    data class Sukses(val data: UserProfileResponse) : StatusProfil()

    /**
     * Terjadi kesalahan — tampilkan pesan error yang mudah dipahami.
     * @param pesan Pesan error ramah untuk pengguna lansia.
     */
    data class Gagal(val pesan: String) : StatusProfil()
}

// ─── ViewModel ─────────────────────────────────────────────────────────────

/**
 * ViewModel untuk layar Akun/Profil.
 *
 * Meng-extend [AndroidViewModel] (bukan [ViewModel] biasa) karena membutuhkan
 * [Application] context untuk membuat instance [SessionManager].
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // ─── Dependensi ─────────────────────────────────────────────────────────

    /** Pengelola sesi login (token, userId) menggunakan SharedPreferences */
    private val sessionManager = SessionManager(application.applicationContext)

    /** Repository untuk operasi data profil */
    private val repository = ProfileRepository()

    // ─── State: Status Profil ────────────────────────────────────────────────

    /**
     * State internal — hanya ViewModel yang boleh mengubah nilainya.
     * Nilai awal [StatusProfil.Memuat] agar layar langsung menampilkan loading.
     */
    private val _statusProfil = MutableStateFlow<StatusProfil>(StatusProfil.Memuat)

    /**
     * State publik yang diobservasi oleh Composable.
     * [asStateFlow] mencegah UI mengubah state secara langsung.
     */
    val statusProfil: StateFlow<StatusProfil> = _statusProfil.asStateFlow()

    // ─── State: Status Logout ─────────────────────────────────────────────────

    /**
     * Sinyal satu arah: false → true saat logout berhasil.
     * UI mengobservasi ini dan memanggil [onKeluar] navigasi saat nilainya true.
     */
    private val _sudahKeluar = MutableStateFlow(false)
    val sudahKeluar: StateFlow<Boolean> = _sudahKeluar.asStateFlow()

    // ─── Inisialisasi ────────────────────────────────────────────────────────

    init {
        // Gunakan muatDataDemo() untuk tampilan contoh tanpa API
        // Ganti dengan muatDataDariServer() saat server backend sudah siap
        muatDataDemo()
    }

    // ─── Fungsi: Muat Data Demo ──────────────────────────────────────────────

    /**
     * Memuat data profil contoh tanpa koneksi internet.
     * Berguna untuk pengembangan UI dan demonstrasi aplikasi.
     */
    fun muatDataDemo() {
        _statusProfil.value = StatusProfil.Memuat
        viewModelScope.launch {
            when (val hasil = repository.getProfilDemo()) {
                is HasilData.Sukses -> _statusProfil.value = StatusProfil.Sukses(hasil.data)
                is HasilData.Gagal  -> _statusProfil.value = StatusProfil.Gagal(hasil.pesan)
                is HasilData.Memuat -> { /* Tidak digunakan di getProfilDemo */ }
            }
        }
    }

    // ─── Fungsi: Muat Data dari Server (API) ────────────────────────────────

    /**
     * Mengambil data profil dari server menggunakan token yang tersimpan.
     * Panggil ini saat server sudah siap dan pengguna sudah login.
     */
    fun muatDataDariServer() {
        val token  = sessionManager.ambilToken() ?: run {
            // Token tidak ada — anggap sesi sudah berakhir
            _sudahKeluar.value = true
            return
        }
        val userId = sessionManager.ambilUserId()

        _statusProfil.value = StatusProfil.Memuat
        viewModelScope.launch {
            when (val hasil = repository.getProfil(token, userId)) {
                is HasilData.Sukses -> _statusProfil.value = StatusProfil.Sukses(hasil.data)
                is HasilData.Gagal  -> _statusProfil.value = StatusProfil.Gagal(hasil.pesan)
                is HasilData.Memuat -> { /* Ditangani Repository */ }
            }
        }
    }

    // ─── Fungsi: Coba Lagi ───────────────────────────────────────────────────

    /** Dipanggil saat pengguna menekan tombol "Coba Lagi" di layar error */
    fun cobaLagi() = muatDataDemo()
    // Untuk produksi: fun cobaLagi() = muatDataDariServer()

    // ─── Fungsi: Logout ──────────────────────────────────────────────────────

    /**
     * Proses logout pengguna:
     * 1. Menghapus semua data sesi dari SharedPreferences (token, userId, nama)
     * 2. Mengubah [_sudahKeluar] menjadi true sebagai sinyal ke UI
     *
     * UI yang mengobservasi [sudahKeluar] akan memanggil callback navigasi
     * untuk kembali ke layar Login.
     */
    fun keluar() {
        // Hapus semua data sesi tersimpan di perangkat
        sessionManager.hapusSesi()

        // Sinyal ke UI bahwa logout berhasil — UI akan navigasi ke Login
        _sudahKeluar.value = true
    }
}
