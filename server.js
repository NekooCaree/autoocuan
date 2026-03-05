// ═══════════════════════════════════════════════════════════════════════════
//  server.js  — Backend Proxy untuk Chatbot AUTO CUAN
//
//  Fungsi: Menjadi "perantara" antara aplikasi Android dan Gemini API
//          sehingga API Key TIDAK pernah ada di dalam APK
//
//  Stack  : Node.js + Express
//  Deploy : Railway / Render / Fly.io (gratis untuk skala kecil)
//
//  Cara pakai:
//    1. npm install express @google/generative-ai cors dotenv
//    2. Buat file .env dengan isi: GEMINI_API_KEY=AIzaSy_...
//    3. node server.js
// ═══════════════════════════════════════════════════════════════════════════

require("dotenv").config(); // Baca variabel dari file .env

const express = require("express");
const cors    = require("cors");
const { GoogleGenerativeAI } = require("@google/generative-ai");

const app  = express();
const PORT = process.env.PORT || 3000;

// ─── Middleware ────────────────────────────────────────────────────────────
app.use(express.json());
app.use(cors()); // Izinkan request dari aplikasi Android

// ─── Inisialisasi Gemini ───────────────────────────────────────────────────

// API Key dibaca dari environment variable — TIDAK pernah ditulis langsung di kode
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

const SYSTEM_PROMPT = `
Kamu adalah "Asisten Usaha AUTO CUAN", asisten bisnis yang ramah dan sabar
untuk membantu Bapak/Ibu lansia pemilik UMKM di Indonesia.
Hanya jawab pertanyaan seputar: penjualan, stok barang, keuangan sederhana,
promosi, dan motivasi usaha. Tolak pertanyaan di luar topik UMKM dengan sopan.
Gunakan Bahasa Indonesia yang mudah dipahami lansia. Kalimat pendek dan jelas.
`.trim();

// ─── Endpoint: Kirim Pesan ke Chatbot ─────────────────────────────────────

/**
 * POST /api/chat
 * Body: { "pesan": "Cara meningkatkan penjualan?", "riwayat": [...] }
 * Response: { "balasan": "Tentu, ada beberapa cara..." }
 */
app.post("/api/chat", async (req, res) => {
  try {
    const { pesan, riwayat = [] } = req.body;

    if (!pesan || typeof pesan !== "string") {
      return res.status(400).json({ error: "Field 'pesan' wajib diisi." });
    }

    // ── TODO Opsional: Tambahkan autentikasi di sini ──────────────────────
    // Contoh: cek Bearer token dari header Authorization
    // const token = req.headers.authorization?.split(" ")[1];
    // if (!validateToken(token)) return res.status(401).json({ error: "Unauthorized" });

    const model = genAI.getGenerativeModel({
      model           : "gemini-1.5-flash",
      systemInstruction: SYSTEM_PROMPT,
      generationConfig : { temperature: 0.7, maxOutputTokens: 512 },
    });

    // Konversi riwayat pesan dari format aplikasi ke format Gemini
    const historyGemini = riwayat.map((p) => ({
      role  : p.pengirim === "PENGGUNA" ? "user" : "model",
      parts : [{ text: p.isi }],
    }));

    const chat    = model.startChat({ history: historyGemini });
    const result  = await chat.sendMessage(pesan);
    const balasan = result.response.text();

    res.json({ balasan });

  } catch (error) {
    console.error("Error saat memanggil Gemini API:", error.message);
    res.status(500).json({
      error: "Gagal memproses permintaan. Coba lagi nanti.",
    });
  }
});

// ─── Health Check ──────────────────────────────────────────────────────────
app.get("/health", (req, res) => {
  res.json({ status: "ok", service: "AUTO CUAN Chatbot API" });
});

// ─── Start Server ──────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log(`✅ Server AUTO CUAN berjalan di port ${PORT}`);
});
