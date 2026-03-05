package com.example.autocuanumkm.ui.chat

// ═══════════════════════════════════════════════════════════════════════════
//  ChatScreen.kt  — Halaman Chatbot AI (Asisten Usaha UMKM)
//
//  Prinsip desain untuk lansia:
//    ✓ Gelembung pesan besar (min 16sp, padding generous)
//    ✓ Warna kontras: Pengguna = Hijau | Asisten = Putih/Surface
//    ✓ Tombol "Kirim" besar dengan IKON + TEKS (tidak hanya ikon)
//    ✓ Indikator "Asisten sedang mengetik..." dengan animasi tiga titik
//    ✓ Saran cepat (quick chips) untuk pertanyaan umum UMKM
//    ✓ Keyboard otomatis terakomodasi via imePadding()
//
//  Struktur layar:
//    1. TopAppBar       — "Asisten Usaha" + status online
//    2. LazyColumn      — Riwayat gelembung pesan (auto-scroll ke bawah)
//    3. Saran Cepat     — Chips pertanyaan populer (tampil di awal)
//    4. Area Input      — TextField besar + tombol "Kirim"
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autocuanumkm.AutoCuanTheme
import com.example.autocuanumkm.data.model.PengirimPesan
import com.example.autocuanumkm.data.model.PesanChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Daftar Saran Pertanyaan Cepat ────────────────────────────────────────

/**
 * Pertanyaan populer yang ditampilkan sebagai chips di awal percakapan.
 * Memudahkan lansia memulai tanpa harus mengetik dari nol.
 */
private val SARAN_PERTANYAAN = listOf(
    "Cara meningkatkan penjualan?",
    "Bagaimana mencatat keuangan?",
    "Tips promosi lewat WhatsApp?",
    "Cara agar pelanggan kembali?",
    "Stok barang menipis, apa yang harus dilakukan?"
)

// ─── Composable Utama: ChatScreen ─────────────────────────────────────────

/**
 * Layar chatbot untuk tab "Asisten".
 *
 * @param viewModel ViewModel yang mengelola riwayat pesan dan komunikasi dengan Gemini API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {

    // Observasi StateFlow dari ViewModel
    val riwayatPesan by viewModel.riwayatPesan.collectAsState()
    val sedangMemuat by viewModel.sedangMemuat.collectAsState()

    // State lokal untuk teks yang sedang diketik di input field
    var teksInput by remember { mutableStateOf("") }

    // State untuk LazyColumn — digunakan untuk auto-scroll ke bawah
    val lazyListState    = rememberLazyListState()
    val coroutineScope   = rememberCoroutineScope()

    // Dialog konfirmasi hapus percakapan
    var tampilDialogHapus by remember { mutableStateOf(false) }

    // Tampilkan saran cepat hanya jika baru ada 1 pesan (pesan sambutan)
    val tampilSaran = riwayatPesan.size <= 1

    // ── Auto-scroll ke pesan terbaru saat riwayat bertambah ─────────────
    LaunchedEffect(riwayatPesan.size, sedangMemuat) {
        if (riwayatPesan.isNotEmpty()) {
            val targetIndex = if (sedangMemuat) riwayatPesan.size else riwayatPesan.size - 1
            coroutineScope.launch {
                lazyListState.animateScrollToItem(
                    index  = riwayatPesan.size - 1,
                    scrollOffset = 200
                )
            }
        }
    }

    Scaffold(
        // Menambahkan padding otomatis saat keyboard muncul
        modifier = Modifier.imePadding(),
        topBar   = {
            // ── TopAppBar ──────────────────────────────────────────────────
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar robot kecil di TopBar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onPrimary,
                                modifier           = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text       = "Asisten Usaha",
                                style      = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                // Tombol hapus percakapan di pojok kanan TopBar
                actions = {
                    IconButton(onClick = { tampilDialogHapus = true }) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Hapus percakapan",
                            tint               = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // ── Area Pesan (LazyColumn) ──────────────────────────────────
            LazyColumn(
                state           = lazyListState,
                modifier        = Modifier.weight(1f),
                contentPadding  = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tampilkan setiap pesan sebagai gelembung chat
                items(
                    items = riwayatPesan,
                    key   = { pesan -> pesan.id }  // Key unik untuk animasi rekomposisi
                ) { pesan ->
                    when (pesan.pengirim) {
                        PengirimPesan.PENGGUNA -> GelembungPengguna(pesan = pesan)
                        PengirimPesan.ASISTEN  -> GelembungAsisten(pesan = pesan)
                    }
                }

                // Indikator "Asisten sedang mengetik..." — tampil saat loading
                if (sedangMemuat) {
                    item(key = "loading_indicator") {
                        IndikatorMenulis()
                    }
                }
            }

            // ── Saran Cepat (muncul hanya di awal percakapan) ───────────
            if (tampilSaran && !sedangMemuat) {
                SaranCepat(
                    onSaranDipilih = { teks ->
                        teksInput = ""
                        viewModel.kirimPesan(teks)
                    }
                )
            }

            // ── Area Input Teks + Tombol Kirim ───────────────────────────
            AreaInput(
                teksInput    = teksInput,
                onTeksChange = { teksInput = it },
                sedangMemuat = sedangMemuat,
                onKirim      = {
                    if (teksInput.isNotBlank()) {
                        viewModel.kirimPesan(teksInput)
                        teksInput = ""  // Kosongkan field setelah kirim
                    }
                }
            )
        }
    }

    // ── Dialog Konfirmasi Hapus Percakapan ────────────────────────────────
    if (tampilDialogHapus) {
        AlertDialog(
            onDismissRequest = { tampilDialogHapus = false },
            icon  = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text      = "Hapus Percakapan?",
                    style     = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text      = "Semua riwayat chat akan dihapus dan percakapan akan dimulai dari awal.",
                    style     = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        tampilDialogHapus = false
                        viewModel.hapusPercakapan()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Ya, Hapus", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { tampilDialogHapus = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal", style = MaterialTheme.typography.labelLarge)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ─── Gelembung Pesan: Pengguna (kanan, hijau) ──────────────────────────────

/**
 * Gelembung chat untuk pesan yang dikirim oleh pengguna.
 * Diposisikan di KANAN layar dengan latar hijau (warna primary).
 */
@Composable
private fun GelembungPengguna(pesan: PesanChat) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End  // Rata kanan
    ) {
        Spacer(modifier = Modifier.weight(0.15f))  // Ruang kosong di kiri

        Column(horizontalAlignment = Alignment.End) {
            // Gelembung pesan
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 4.dp,   // Sudut tajam di kanan atas — seperti "ekor" gelembung
                            bottomStart = 18.dp,
                            bottomEnd   = 18.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text  = pesan.isi,
                    style = MaterialTheme.typography.bodyLarge,  // 18sp
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Waktu pengiriman
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text  = formatWaktu(pesan.waktu),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

// ─── Gelembung Pesan: Asisten (kiri, putih) ───────────────────────────────

/**
 * Gelembung chat untuk pesan dari Asisten AI.
 * Diposisikan di KIRI layar dengan avatar robot + latar putih (surface).
 *
 * Jika [pesan.isError] = true, tampilkan dengan latar merah muda untuk peringatan.
 */
@Composable
private fun GelembungAsisten(pesan: PesanChat) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,  // Rata kiri
        verticalAlignment     = Alignment.Bottom
    ) {
        // Avatar robot kecil di sudut kiri bawah gelembung
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    // Merah jika error, hijau jika normal
                    if (pesan.isError) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primary
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.SmartToy,
                contentDescription = "Asisten",
                tint               = if (pesan.isError) MaterialTheme.colorScheme.onErrorContainer
                                     else MaterialTheme.colorScheme.onPrimary,
                modifier           = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            // Gelembung pesan
            Surface(
                shape     = RoundedCornerShape(
                    topStart    = 4.dp,   // Sudut tajam di kiri atas — menandai "ekor" gelembung
                    topEnd      = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd   = 18.dp
                ),
                color         = if (pesan.isError) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Text(
                    text     = pesan.isi,
                    style    = MaterialTheme.typography.bodyLarge,  // 18sp
                    color    = if (pesan.isError) MaterialTheme.colorScheme.onErrorContainer
                               else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Waktu pengiriman
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text  = formatWaktu(pesan.waktu),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.weight(0.15f))  // Ruang kosong di kanan
    }
}

// ─── Indikator "Asisten Sedang Mengetik..." ───────────────────────────────

/**
 * Animasi tiga titik (. → .. → ...) yang ditampilkan saat menunggu respons AI.
 * Membantu pengguna lansia mengerti bahwa aplikasi sedang bekerja, bukan hang.
 */
@Composable
private fun IndikatorMenulis() {
    // State untuk jumlah titik (1, 2, 3 berulang)
    var jumlahTitik by remember { mutableStateOf(1) }

    // Animasi titik: berubah setiap 500ms
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            jumlahTitik = (jumlahTitik % 3) + 1
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier          = Modifier.padding(vertical = 4.dp)
    ) {
        // Avatar robot kecil
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.SmartToy,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onPrimary,
                modifier           = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Gelembung indikator mengetik
        Surface(
            shape           = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            color           = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Text(
                text     = "Asisten sedang mengetik${"".padEnd(jumlahTitik, '.')}",
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

// ─── Saran Pertanyaan Cepat ────────────────────────────────────────────────

/**
 * Baris chips pertanyaan populer yang ditampilkan di awal percakapan.
 * Memudahkan lansia memulai chat tanpa perlu mengetik dari nol.
 *
 * @param onSaranDipilih Dipanggil dengan teks saran saat chip diketuk.
 */
@Composable
private fun SaranCepat(onSaranDipilih: (String) -> Unit) {
    Column {
        Text(
            text     = "Pertanyaan populer:",
            style    = MaterialTheme.typography.labelMedium,
            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding      = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SARAN_PERTANYAAN) { saran ->
                SuggestionChip(
                    onClick = { onSaranDipilih(saran) },
                    label   = {
                        Text(
                            text  = saran,
                            style = MaterialTheme.typography.bodyMedium,  // 16sp
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor     = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// ─── Area Input Teks + Tombol Kirim ───────────────────────────────────────

/**
 * Area input di bagian bawah layar.
 *
 * Terdiri dari:
 * - [OutlinedTextField] besar (hingga 4 baris) untuk mengetik pesan
 * - Tombol "Kirim" besar dengan IKON + TEKS (penting untuk lansia)
 *
 * @param teksInput    Teks yang sedang diketik pengguna.
 * @param onTeksChange Callback saat teks berubah.
 * @param sedangMemuat Jika true, nonaktifkan input dan tombol kirim.
 * @param onKirim      Callback saat tombol "Kirim" ditekan atau Enter di keyboard.
 */
@Composable
private fun AreaInput(
    teksInput   : String,
    onTeksChange: (String) -> Unit,
    sedangMemuat: Boolean,
    onKirim     : () -> Unit
) {
    Surface(
        color         = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation  = 2.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // ── TextField Input ───────────────────────────────────────────
            OutlinedTextField(
                value         = teksInput,
                onValueChange = onTeksChange,
                modifier      = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 56.dp),
                placeholder   = {
                    Text(
                        text  = "Ketik pertanyaan Bapak/Ibu...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                },
                textStyle     = MaterialTheme.typography.bodyLarge,  // 18sp — mudah dibaca lansia
                enabled       = !sedangMemuat,
                maxLines      = 4,
                shape         = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction      = ImeAction.Send  // Tombol "Enter" di keyboard menjadi "Send"
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onKirim() }
                ),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // ── Tombol Kirim ────────────────────────────────────────────
            Button(
                onClick  = onKirim,
                enabled  = teksInput.isNotBlank() && !sedangMemuat,
                modifier = Modifier
                    .height(56.dp)              // Tinggi sama dengan TextField agar rapi
                    .defaultMinSize(minWidth = 80.dp),
                shape    = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Send,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Teks "Kirim" di samping ikon — wajib untuk UX lansia
                Text(
                    text  = "Kirim",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ─── Fungsi Pembantu ───────────────────────────────────────────────────────

/**
 * Mengubah timestamp Unix menjadi format jam yang mudah dibaca.
 * Contoh: 1704067200000 → "14:30"
 */
private fun formatWaktu(timestampMillis: Long): String {
    return SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date(timestampMillis))
}

// ─── Preview ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewChatScreen() {
    AutoCuanTheme {
        // Preview manual menggunakan data statis (tidak membutuhkan ViewModel nyata)
        val pesanContoh = listOf(
            PesanChat(
                isi      = "Halo Bapak/Ibu! 👋 Saya Asisten Usaha AUTO CUAN.\n\nAda yang bisa saya bantu?",
                pengirim = PengirimPesan.ASISTEN
            ),
            PesanChat(
                isi      = "Cara meningkatkan penjualan?",
                pengirim = PengirimPesan.PENGGUNA
            ),
            PesanChat(
                isi      = "Tentu Bapak/Ibu! 💪 Ada beberapa cara mudah:\n\n" +
                           "1. Sapa pembeli dengan ramah 😊\n" +
                           "2. Beri promo sesekali (misal: beli 2 gratis 1)\n" +
                           "3. Promosi di WhatsApp keluarga & tetangga\n\n" +
                           "Mau tips yang mana lebih dulu?",
                pengirim = PengirimPesan.ASISTEN
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier       = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pesanContoh) { pesan ->
                    when (pesan.pengirim) {
                        PengirimPesan.PENGGUNA -> GelembungPengguna(pesan)
                        PengirimPesan.ASISTEN  -> GelembungAsisten(pesan)
                    }
                }
            }
            AreaInput(
                teksInput    = "",
                onTeksChange = {},
                sedangMemuat = false,
                onKirim      = {}
            )
        }
    }
}
