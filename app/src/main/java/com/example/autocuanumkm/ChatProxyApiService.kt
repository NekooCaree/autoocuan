package com.example.autocuanumkm.data.network

// ═══════════════════════════════════════════════════════════════════════════
//  ChatProxyApiService.kt  — Retrofit interface untuk Backend Proxy Chatbot
//
//  Aplikasi Android memanggil SERVER ANDA sendiri (bukan Gemini langsung).
//  Server Anda yang menyimpan API Key Gemini dengan aman.
//
//  Ganti BASE_URL dengan URL server Anda setelah deploy.
// ═══════════════════════════════════════════════════════════════════════════

import retrofit2.http.Body
import retrofit2.http.POST

// ─── Request & Response Data Class ────────────────────────────────────────

/** Riwayat satu pesan untuk dikirim ke server */
data class RiwayatRequest(
    val pengirim: String, // "PENGGUNA" atau "ASISTEN"
    val isi     : String
)

/** Body yang dikirim ke POST /api/chat */
data class ChatRequest(
    val pesan  : String,               // Pesan baru dari pengguna
    val riwayat: List<RiwayatRequest>  // Riwayat percakapan sebelumnya (untuk konteks)
)

/** Respons dari server */
data class ChatResponse(
    val balasan: String  // Jawaban dari Gemini yang diteruskan server
)

// ─── Retrofit Interface ────────────────────────────────────────────────────

interface ChatProxyApiService {

    /**
     * Kirim pesan ke backend proxy dan terima balasan dari Gemini.
     * API Key Gemini DISIMPAN DI SERVER — tidak ada di APK.
     */
    @POST("api/chat")
    suspend fun kirimPesan(@Body body: ChatRequest): ChatResponse
}
