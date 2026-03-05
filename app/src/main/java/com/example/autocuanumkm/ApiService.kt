package com.example.autocuanumkm.data.network

// ═══════════════════════════════════════════════════════════════════════════
//  ApiService.kt  — Antarmuka Retrofit untuk semua endpoint API
//
//  Setiap fungsi di sini mewakili satu endpoint HTTP pada server backend.
//  Gunakan `suspend fun` agar bisa dipanggil dari Coroutine (non-blocking).
// ═══════════════════════════════════════════════════════════════════════════

import com.example.autocuanumkm.data.model.DataBeranda
import com.example.autocuanumkm.data.model.ResponApi
import com.example.autocuanumkm.data.model.UpdateProfilRequest
import com.example.autocuanumkm.data.model.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Antarmuka (interface) yang mendeskripsikan semua API endpoint yang tersedia.
 * Retrofit akan membuat implementasinya secara otomatis saat runtime.
 */
interface ApiService {

    // ─── Endpoint: Beranda ─────────────────────────────────────────────────

    /**
     * Mengambil data lengkap untuk halaman beranda:
     * profil pengguna + ringkasan usaha.
     *
     * HTTP Request: GET https://api.autocuan.com/api/v1/beranda/{userId}
     *
     * @param token   Token autentikasi. Format: "Bearer <token_dari_login>"
     * @param userId  ID unik pengguna yang data berandanya ingin diambil
     * @return [ResponApi] yang membungkus [DataBeranda]
     */
    @GET("api/v1/beranda/{userId}")
    suspend fun getDataBeranda(
        @Header("Authorization") token  : String,
        @Path("userId")          userId : Int
    ): ResponApi<DataBeranda>

    // ─── Endpoint: Profil Pengguna ─────────────────────────────────────────

    /**
     * Mengambil data profil lengkap pengguna.
     *
     * HTTP Request: GET https://api.autocuan.com/api/v1/profil/{userId}
     *
     * @param token   Token autentikasi. Format: "Bearer <token>"
     * @param userId  ID pengguna
     */
    @GET("api/v1/profil/{userId}")
    suspend fun getProfil(
        @Header("Authorization") token  : String,
        @Path("userId")          userId : Int
    ): ResponApi<UserProfileResponse>

    /**
     * Memperbarui data profil pengguna.
     *
     * HTTP Request: PUT https://api.autocuan.com/api/v1/profil/{userId}
     *
     * @param token   Token autentikasi
     * @param userId  ID pengguna yang profilnya diperbarui
     * @param request Data profil baru yang ingin disimpan
     */
    @PUT("api/v1/profil/{userId}")
    suspend fun updateProfil(
        @Header("Authorization") token   : String,
        @Path("userId")          userId  : Int,
        @Body                    request : UpdateProfilRequest
    ): ResponApi<UserProfileResponse>

    // ─── Tambahkan endpoint lain di bawah ini sesuai kebutuhan ─────────────
    // @GET("api/v1/produk")
    // suspend fun getDaftarProduk(@Header("Authorization") token: String): ResponApi<List<Produk>>
    //
    // @POST("api/v1/transaksi")
    // suspend fun catatPenjualan(@Header("Authorization") token: String, @Body data: DataPenjualan): ResponApi<Unit>
}
