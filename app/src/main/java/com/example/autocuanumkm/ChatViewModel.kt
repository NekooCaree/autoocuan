package com.example.autocuanumkm.ui.chat

// ═══════════════════════════════════════════════════════════════════════════
//  ChatViewModel.kt  — ViewModel untuk Chatbot AI (MVVM + Retrofit Proxy)
//
//  Arsitektur PRODUKSI:
//    Aplikasi Android  →  Server Proxy Anda (Railway)  →  Gemini API
//
//  Keunggulan dibanding koneksi langsung ke Gemini SDK:
//    ✓ API Key TIDAK ada di dalam APK — tidak bisa dicuri
//    ✓ Anda bisa ganti model AI kapan saja tanpa update aplikasi
//    ✓ Bisa tambahkan autentikasi & rate limiting di server
//    ✓ Riwayat percakapan dikirim manual ke server untuk konteks
// ═══════════════════════════════════════════════════════════════════════════

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocuanumkm.data.model.PengirimPesan
import com.example.autocuanumkm.data.model.PesanChat
import com.example.autocuanumkm.data.network.ChatRequest
import com.example.autocuanumkm.data.network.RetrofitClient
import com.example.autocuanumkm.data.network.RiwayatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    // ─── State: Riwayat Pesan (ditampilkan di UI) ────────────────────────────

    /**
     * Daftar semua pesan dalam percakapan.
     * Nilai awal: satu pesan sambutan dari asisten agar layar tidak kosong.
     */
    private val _riwayatPesan = MutableStateFlow<List<PesanChat>>(
        listOf(
            PesanChat(
                isi      = "Halo Bapak/Ibu! 👋 Saya Asisten Usaha AUTO CUAN.\n\n" +
                           "Saya siap membantu urusan usaha Bapak/Ibu — mulai dari cara " +
                           "meningkatkan penjualan 💰, mengatur stok barang 📦, " +
                           "mencatat keuangan 📒, hingga tips promosi murah 📣.\n\n" +
                           "Ada yang bisa saya bantu hari ini? 😊",
                pengirim = PengirimPesan.ASISTEN
            )
        )
    )

    /** State publik riwayat pesan — diobservasi oleh ChatScreen */
    val riwayatPesan: StateFlow<List<PesanChat>> = _riwayatPesan.asStateFlow()

    // ─── State: Status Loading ────────────────────────────────────────────────

    /**
     * true saat sedang menunggu respons dari server proxy.
     * ChatScreen menampilkan "Asisten sedang mengetik..." saat ini true.
     */
    private val _sedangMemuat = MutableStateFlow(false)
    val sedangMemuat: StateFlow<Boolean> = _sedangMemuat.asStateFlow()

    // ─── Fungsi: Kirim Pesan ──────────────────────────────────────────────────

    /**
     * Mengirim pesan pengguna ke server proxy dan menambahkan balasannya ke riwayat.
     *
     * Alur:
     * 1. Validasi input
     * 2. Tambahkan pesan pengguna ke UI
     * 3. Konversi seluruh riwayat ke format [RiwayatRequest] untuk konteks AI
     * 4. Kirim ke server proxy via Retrofit (background thread / Coroutine)
     * 5. Tambahkan balasan asisten ke UI
     *
     * @param teks Teks pesan yang dikirimkan oleh pengguna
     */
    fun kirimPesan(teks: String) {
        // Abaikan jika pesan kosong atau masih menunggu respons sebelumnya
        if (teks.isBlank() || _sedangMemuat.value) return

        // ── Tambahkan pesan pengguna ke UI ────────────────────────────────
        val pesanPengguna = PesanChat(isi = teks.trim(), pengirim = PengirimPesan.PENGGUNA)
        _riwayatPesan.value = _riwayatPesan.value + pesanPengguna

        // ── Panggil server proxy di background thread ─────────────────────
        _sedangMemuat.value = true

        viewModelScope.launch {
            try {
                // Konversi riwayat dari PesanChat ke format RiwayatRequest
                // agar server punya konteks percakapan sebelumnya (tidak termasuk
                // pesan pengguna yang baru saja ditambahkan — sudah ada di field "pesan")
                val riwayatUntukServer = _riwayatPesan.value
                    .dropLast(1) // hapus pesan terakhir (yang baru saja dikirim)
                    .filter { !it.isError } // jangan kirim pesan error ke server
                    .map { pesan ->
                        RiwayatRequest(
                            pengirim = pesan.pengirim.name, // "PENGGUNA" atau "ASISTEN"
                            isi      = pesan.isi
                        )
                    }

                // Kirim request ke server proxy via Retrofit
                val respons = RetrofitClient.chatApiService.kirimPesan(
                    ChatRequest(
                        pesan   = teks.trim(),
                        riwayat = riwayatUntukServer
                    )
                )

                tambahPesanAsisten(isi = respons.balasan)

            } catch (e: Exception) {
                // Error jaringan atau server tidak merespons
                tambahPesanAsisten(
                    isi     = "⚠ Maaf, gagal terhubung ke asisten.\n\n" +
                              "Pastikan koneksi internet Bapak/Ibu aktif, " +
                              "lalu coba kirim pesan lagi. 🙏",
                    isError = true
                )
            } finally {
                // Selalu matikan loading, baik sukses maupun gagal
                _sedangMemuat.value = false
            }
        }
    }

    // ─── Fungsi Pembantu ──────────────────────────────────────────────────────

    /** Menambahkan pesan dari asisten ke riwayat UI */
    private fun tambahPesanAsisten(isi: String, isError: Boolean = false) {
        _riwayatPesan.value = _riwayatPesan.value + PesanChat(
            isi      = isi,
            pengirim = PengirimPesan.ASISTEN,
            isError  = isError
        )
    }

    /** Hapus seluruh riwayat dan mulai percakapan baru */
    fun hapusPercakapan() {
        _riwayatPesan.value = listOf(
            PesanChat(
                isi      = "Percakapan baru dimulai. 👋\n\nAda pertanyaan tentang usaha Bapak/Ibu?",
                pengirim = PengirimPesan.ASISTEN
            )
        )
    }
}

