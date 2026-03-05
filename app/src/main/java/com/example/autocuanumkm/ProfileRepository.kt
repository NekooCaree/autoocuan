package com.example.autocuanumkm.data.repository

// ═══════════════════════════════════════════════════════════════════════════
//  ProfileRepository.kt  — Lapisan data untuk fitur Profil / Akun
//
//  Menangani dua operasi utama:
//    1. GET  — Mengambil data profil dari server
//    2. PUT  — Memperbarui data profil ke server
//    3. Demo — Data contoh untuk pengembangan tanpa server
//
//  [HasilData] sealed class sudah didefinisikan di HomeRepository.kt
//  dan otomatis tersedia karena berada dalam package yang sama.
// ═══════════════════════════════════════════════════════════════════════════

import com.example.autocuanumkm.data.model.UpdateProfilRequest
import com.example.autocuanumkm.data.model.UserProfileResponse
import com.example.autocuanumkm.data.network.RetrofitClient

/**
 * Repository untuk mengambil dan memperbarui data profil pengguna.
 *
 * Menggunakan [HasilData] sebagai wrapper status (didefinisikan di HomeRepository.kt,
 * berada dalam package yang sama: com.example.autocuanumkm.data.repository).
 */
class ProfileRepository {

    private val apiService = RetrofitClient.apiService

    // ─── Ambil Profil dari Server ─────────────────────────────────────────

    /**
     * Mengambil data profil pengguna dari API server.
     *
     * @param token   Token autentikasi (format: "Bearer <token>")
     * @param userId  ID pengguna yang ingin diambil profilnya
     * @return [HasilData.Sukses] berisi data profil, atau [HasilData.Gagal] jika error
     */
    suspend fun getProfil(token: String, userId: Int): HasilData<UserProfileResponse> {
        return try {
            val respons = apiService.getProfil("Bearer $token", userId)

            if (respons.sukses && respons.data != null) {
                HasilData.Sukses(respons.data)
            } else {
                HasilData.Gagal(
                    respons.pesan.ifEmpty { "Data profil tidak ditemukan." }
                )
            }
        } catch (e: Exception) {
            HasilData.Gagal(
                "Gagal memuat profil. Periksa koneksi internet Anda."
            )
        }
    }

    // ─── Perbarui Profil ke Server ────────────────────────────────────────

    /**
     * Mengirimkan data profil yang diperbarui ke server.
     *
     * @param token   Token autentikasi
     * @param userId  ID pengguna yang profilnya diperbarui
     * @param request Data baru yang ingin disimpan ([UpdateProfilRequest])
     * @return [HasilData.Sukses] berisi profil terbaru, atau [HasilData.Gagal] jika error
     */
    suspend fun updateProfil(
        token  : String,
        userId : Int,
        request: UpdateProfilRequest
    ): HasilData<UserProfileResponse> {
        return try {
            val respons = apiService.updateProfil("Bearer $token", userId, request)

            if (respons.sukses && respons.data != null) {
                HasilData.Sukses(respons.data)
            } else {
                HasilData.Gagal(
                    respons.pesan.ifEmpty { "Gagal menyimpan perubahan. Coba lagi nanti." }
                )
            }
        } catch (e: Exception) {
            HasilData.Gagal(
                "Gagal menyimpan perubahan. Periksa koneksi internet Anda."
            )
        }
    }

    // ─── Data Demo ────────────────────────────────────────────────────────

    /**
     * Mengembalikan data profil contoh tanpa koneksi internet.
     * Gunakan untuk pengembangan UI atau demonstrasi aplikasi.
     */
    fun getProfilDemo(): HasilData<UserProfileResponse> {
        return HasilData.Sukses(
            UserProfileResponse(
                id         = 1,
                nama       = "Bapak Budi Santoso",
                namaUsaha  = "GIMSEHAP",
                noTelepon  = "0812-3456-7890",
                alamatToko = "Jl. Pahlawan No. 10, Kebayoran Baru, Jakarta Selatan",
                jamBuka    = "07:00 - 21:00 WIB",
                rekening   = "BCA - 1234567890 (Budi Santoso)"
            )
        )
    }
}
