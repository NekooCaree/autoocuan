package com.example.autocuanumkm.data.network

// ═══════════════════════════════════════════════════════════════════════════
//  RetrofitClient.kt  — Konfigurasi Singleton Retrofit
//
//  Menggunakan pola Singleton (object) agar hanya ada satu instance Retrofit
//  di seluruh aplikasi — efisien dan hemat memori.
// ═══════════════════════════════════════════════════════════════════════════

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton yang menyediakan instance [ApiService] yang sudah dikonfigurasi.
 *
 * Cara pakai:
 * ```
 * val apiService = RetrofitClient.apiService
 * val respons    = apiService.getDataBeranda("Bearer token123", userId = 1)
 * ```
 */
object RetrofitClient {

    // ─── Konfigurasi: Server Utama (Login, Profil, Beranda) ────────────────

    /**
     * WAJIB DIUBAH: Ganti dengan URL server backend Anda.
     * Harus diakhiri dengan garis miring (/), contoh: "https://api.namadomain.com/"
     */
    private const val BASE_URL = "https://api.autocuan.com/"

    // ─── Konfigurasi: Server Proxy Chatbot (Railway/Render) ───────────────

    /**
     * URL server Node.js yang menjadi perantara ke Gemini API.
     *
     * Cara mengisi:
     *   • Lokal (testing)  → "http://10.0.2.2:3000/"  (10.0.2.2 = localhost emulator)
     *   • Produksi         → ganti dengan URL Railway setelah deploy,
     *                        contoh: "https://autocuan-chatbot.railway.app/"
     *
     * ⚠ API Key Gemini DISIMPAN DI SERVER ini — tidak ada di dalam APK.
     */
    const val CHAT_SERVER_URL = "https://autocuan-chatbot.railway.app/"

    // Batas waktu koneksi dalam detik (penting untuk koneksi internet lambat)
    private const val TIMEOUT_DETIK = 30L

    // ─── Gson Converter ─────────────────────────────────────────────────────

    /**
     * Gson dikonfigurasi dengan:
     * - lenient: toleran terhadap format JSON yang tidak sempurna dari server
     * - serializeNulls: nilai null tetap disertakan saat mengirim data
     */
    private val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    // ─── Retrofit Instance ──────────────────────────────────────────────────

    /**
     * Instance Retrofit yang dikonfigurasi dengan BASE_URL, timeout, dan Gson.
     * Dibuat secara lazy (hanya saat pertama kali diakses).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ─── API Service ─────────────────────────────────────────────────────────

    /** Instance [ApiService] yang siap digunakan untuk memanggil endpoint.
     * Retrofit akan secara otomatis mengimplementasikan interface ini.
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // ─── Retrofit Instance: Chat Proxy ───────────────────────────────────────

    /**
     * Instance Retrofit terpisah yang mengarah ke server proxy chatbot.
     * Dipisahkan dari [retrofit] utama karena base URL-nya berbeda.
     */
    private val chatRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CHAT_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Instance [ChatProxyApiService] untuk mengirim pesan ke server proxy chatbot.
     * Server proxy inilah yang menyimpan API Key Gemini dengan aman.
     */
    val chatApiService: ChatProxyApiService by lazy {
        chatRetrofit.create(ChatProxyApiService::class.java)
    }
}
