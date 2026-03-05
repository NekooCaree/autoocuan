package com.example.autocuanumkm.data.local

// ═══════════════════════════════════════════════════════════════════════════
//  SessionManager.kt  — Pengelola Sesi Login Pengguna
//
//  Menggunakan SharedPreferences untuk menyimpan token autentikasi
//  dan data dasar pengguna secara persisten di perangkat.
//
//  ALTERNATIF MODERN: Untuk keamanan dan fitur yang lebih lengkap,
//  pertimbangkan menggunakan Jetpack DataStore (Preferences DataStore)
//  sebagai pengganti SharedPreferences di masa mendatang.
//
//  Cara pakai:
//    val sessionManager = SessionManager(context)
//    sessionManager.simpanToken("eyJhbGci...")
//    val token = sessionManager.ambilToken()
//    sessionManager.hapusSesi()  // saat logout
// ═══════════════════════════════════════════════════════════════════════════

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton-like class untuk mengelola data sesi pengguna.
 * Dibuat dengan menerima [Context] agar bisa digunakan di luar Composable
 * (misal: di ViewModel atau Repository).
 *
 * @param context Konteks aplikasi. Gunakan [applicationContext] agar tidak terjadi memory leak.
 */
class SessionManager(context: Context) {

    // Menggunakan applicationContext untuk menghindari memory leak
    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences(NAMA_FILE_PREFS, Context.MODE_PRIVATE)

    companion object {
        // Nama file SharedPreferences — unik per aplikasi
        private const val NAMA_FILE_PREFS = "auto_cuan_sesi"

        // Kunci-kunci untuk menyimpan data ke SharedPreferences
        private const val KUNCI_TOKEN     = "auth_token"
        private const val KUNCI_USER_ID   = "user_id"
        private const val KUNCI_NAMA      = "nama_pengguna"
        private const val KUNCI_USAHA     = "nama_usaha"
    }

    // ─── Operasi Token ─────────────────────────────────────────────────────

    /**
     * Menyimpan token autentikasi setelah login berhasil.
     * Token ini dikirimkan di header setiap request API selanjutnya.
     *
     * @param token String JWT atau token dari server
     */
    fun simpanToken(token: String) {
        prefs.edit().putString(KUNCI_TOKEN, token).apply()
    }

    /**
     * Mengambil token yang tersimpan.
     * @return Token sebagai String, atau null jika belum login
     */
    fun ambilToken(): String? = prefs.getString(KUNCI_TOKEN, null)

    // ─── Operasi Data Pengguna ────────────────────────────────────────────

    /** Menyimpan ID pengguna setelah login berhasil */
    fun simpanUserId(id: Int) {
        prefs.edit().putInt(KUNCI_USER_ID, id).apply()
    }

    /** Mengambil ID pengguna. Mengembalikan -1 jika belum tersimpan */
    fun ambilUserId(): Int = prefs.getInt(KUNCI_USER_ID, -1)

    /** Menyimpan nama pengguna untuk ditampilkan di UI tanpa perlu request API */
    fun simpanNama(nama: String) {
        prefs.edit().putString(KUNCI_NAMA, nama).apply()
    }

    /** Mengambil nama pengguna yang tersimpan */
    fun ambilNama(): String = prefs.getString(KUNCI_NAMA, "") ?: ""

    /** Menyimpan nama usaha */
    fun simpanNamaUsaha(namaUsaha: String) {
        prefs.edit().putString(KUNCI_USAHA, namaUsaha).apply()
    }

    /** Mengambil nama usaha yang tersimpan */
    fun ambilNamaUsaha(): String = prefs.getString(KUNCI_USAHA, "") ?: ""

    // ─── Status Login ─────────────────────────────────────────────────────

    /**
     * Memeriksa apakah pengguna sudah login (ada token yang tersimpan).
     * Digunakan saat aplikasi pertama kali dibuka untuk menentukan
     * apakah harus menampilkan Login atau langsung Beranda.
     *
     * @return true jika sudah login, false jika belum
     */
    fun sudahLogin(): Boolean = ambilToken() != null

    // ─── Logout ───────────────────────────────────────────────────────────

    /**
     * Menghapus SEMUA data sesi — dipanggil saat pengguna logout.
     * Setelah ini, [sudahLogin] akan mengembalikan false.
     */
    fun hapusSesi() {
        prefs.edit().clear().apply()
    }
}
