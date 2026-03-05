package com.example.autocuanumkm.data.model

// ═══════════════════════════════════════════════════════════════════════════
//  ChatModels.kt  — Model data untuk fitur Chatbot AI
//
//  Berisi:
//    • PengirimPesan  — Enum pengirim (pengguna atau asisten)
//    • PesanChat      — Satu pesan dalam riwayat percakapan
// ═══════════════════════════════════════════════════════════════════════════

import java.util.UUID

/**
 * Menandai siapa yang mengirim pesan dalam percakapan.
 */
enum class PengirimPesan {
    PENGGUNA,  // Pesan yang dikirim oleh pemilik usaha (pengguna manusia)
    ASISTEN    // Balasan dari AI (Gemini)
}

/**
 * Merepresentasikan satu pesan dalam percakapan chatbot.
 *
 * @param id       ID unik pesan — digunakan sebagai key di LazyColumn agar rekomposisi efisien.
 * @param isi      Teks isi pesan yang ditampilkan dalam gelembung chat.
 * @param pengirim Siapa yang mengirim pesan ini ([PengirimPesan.PENGGUNA] atau [PengirimPesan.ASISTEN]).
 * @param isError  Jika true, pesan ini adalah pesan error dan ditampilkan dengan warna berbeda.
 * @param waktu    Timestamp Unix saat pesan dibuat — digunakan untuk menampilkan jam pesan.
 */
data class PesanChat(
    val id       : String        = UUID.randomUUID().toString(),
    val isi      : String,
    val pengirim : PengirimPesan,
    val isError  : Boolean       = false,
    val waktu    : Long          = System.currentTimeMillis()
)
