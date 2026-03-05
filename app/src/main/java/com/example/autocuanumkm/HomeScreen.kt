package com.example.autocuanumkm.ui.home

// ═══════════════════════════════════════════════════════════════════════════
//  HomeScreen.kt  — Tampilan Halaman Beranda
//
//  Prinsip desain untuk lansia:
//    ✓ Teks besar & kontras tinggi di semua bagian
//    ✓ Tata letak sederhana — satu konten per bagian, tidak padat
//    ✓ Tombol aksi besar dengan ikon + teks yang jelas
//    ✓ Pesan error dan loading menggunakan bahasa Indonesia yang mudah dipahami
//
//  Struktur layar (atas ke bawah):
//    1. TopAppBar  — Logo & judul aplikasi
//    2. Sapaan Personal — "Selamat Datang, Bapak/Ibu [Nama]"
//    3. Kartu Pendapatan — Ringkasan pendapatan hari ini
//    4. Menu Cepat — Tombol "Catat Penjualan" & "Lihat Barang"
//    5. Banner Asisten — Tombol menuju Chatbot
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autocuanumkm.AutoCuanTheme
import com.example.autocuanumkm.Green700
import com.example.autocuanumkm.Green800
import com.example.autocuanumkm.Green50
import com.example.autocuanumkm.Orange700
import com.example.autocuanumkm.Orange100
import com.example.autocuanumkm.data.model.DataBeranda
import java.text.NumberFormat
import java.util.Locale

// ─── Fungsi Pembantu: Format Rupiah ────────────────────────────────────────

/**
 * Mengubah angka Long menjadi format Rupiah yang mudah dibaca.
 * Contoh: 1750000 → "Rp 1.750.000"
 */
private fun formatRupiah(nominal: Long): String {
    val format = NumberFormat.getInstance(Locale("id", "ID"))
    return "Rp ${format.format(nominal)}"
}

// ─── Composable Utama: HomeScreen ──────────────────────────────────────────

/**
 * Layar utama beranda aplikasi AUTO CUAN.
 *
 * Layar ini mengobservasi [HomeViewModel] dan menampilkan konten
 * sesuai status: Memuat → Loading, Sukses → Konten, Gagal → Error.
 *
 * @param viewModel       ViewModel yang mengelola data & status beranda.
 * @param onNavigasiAsisten Callback saat pengguna mengetuk Banner Asisten.
 * @param onCatatPenjualan  Callback saat tombol "Catat Penjualan" ditekan.
 * @param onLihatBarang     Callback saat tombol "Lihat Barang" ditekan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel          : HomeViewModel = viewModel(),
    onNavigasiAsisten  : () -> Unit = {},
    onCatatPenjualan   : () -> Unit = {},
    onLihatBarang      : () -> Unit = {}
) {
    // Observasi StateFlow dari ViewModel — layar otomatis rekomposisi saat status berubah
    val status by viewModel.statusBeranda.collectAsState()

    Scaffold(
        topBar = {
            // ─── TopAppBar ────────────────────────────────────────────────
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Store,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onPrimary,
                            modifier           = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text      = "AUTO CUAN",
                            style     = MaterialTheme.typography.titleLarge,
                            color     = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        // ─── Konten Utama: Disesuaikan dengan Status ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val s = status) {

                // ── Status: MEMUAT ──────────────────────────────────────────
                is StatusBeranda.Memuat -> {
                    TampilanMemuat()
                }

                // ── Status: SUKSES ──────────────────────────────────────────
                is StatusBeranda.Sukses -> {
                    KontenBeranda(
                        data               = s.data,
                        onNavigasiAsisten  = onNavigasiAsisten,
                        onCatatPenjualan   = onCatatPenjualan,
                        onLihatBarang      = onLihatBarang
                    )
                }

                // ── Status: GAGAL ───────────────────────────────────────────
                is StatusBeranda.Gagal -> {
                    TampilanGagal(
                        pesan     = s.pesan,
                        onCobaLagi = { viewModel.cobaLagi() }
                    )
                }
            }
        }
    }
}

// ─── Tampilan: Loading ─────────────────────────────────────────────────────

/**
 * Ditampilkan saat data sedang diambil dari server.
 * Menggunakan CircularProgressIndicator yang besar agar mudah terlihat lansia.
 */
@Composable
private fun TampilanMemuat() {
    Column(
        modifier            = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier  = Modifier.size(64.dp),  // Ukuran besar agar mudah terlihat
            color     = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text  = "Sedang memuat data...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

// ─── Tampilan: Error ───────────────────────────────────────────────────────

/**
 * Ditampilkan saat terjadi kesalahan mengambil data.
 * Pesan error dibuat singkat dan mudah dipahami, disertai tombol "Coba Lagi".
 */
@Composable
private fun TampilanGagal(
    pesan     : String,
    onCobaLagi: () -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ikon peringatan
        Icon(
            imageVector        = Icons.Default.Warning,
            contentDescription = "Terjadi kesalahan",
            tint               = MaterialTheme.colorScheme.error,
            modifier           = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text      = "Oops! Ada Masalah",
            style     = MaterialTheme.typography.headlineSmall,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text      = pesan,
            style     = MaterialTheme.typography.bodyLarge,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(28.dp))
        // Tombol "Coba Lagi" — ukuran besar agar mudah ditekan lansia
        Button(
            onClick  = onCobaLagi,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text(
                text  = "Coba Lagi",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// ─── Konten Beranda (Status Sukses) ────────────────────────────────────────

/**
 * Konten utama beranda yang ditampilkan saat data berhasil dimuat.
 * Menggunakan [Column] yang bisa discroll agar konten tidak terpotong.
 */
@Composable
private fun KontenBeranda(
    data              : DataBeranda,
    onNavigasiAsisten : () -> Unit,
    onCatatPenjualan  : () -> Unit,
    onLihatBarang     : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // Bisa discroll jika konten panjang
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ── 1. Sapaan Personal ────────────────────────────────────────────
        BagianSapaan(
            nama      = data.profil.nama,
            namaUsaha = data.profil.namaUsaha
        )

        // ── 2. Kartu Ringkasan Usaha ──────────────────────────────────────
        KartuRingkasanUsaha(
            pendapatanHariIni     = data.ringkasan.pendapatanHariIni,
            totalTransaksiHariIni = data.ringkasan.totalTransaksiHariIni,
            stokMenipis           = data.ringkasan.stokMenipis
        )

        // ── 3. Menu Cepat (Quick Actions) ────────────────────────────────
        BagianMenuCepat(
            onCatatPenjualan = onCatatPenjualan,
            onLihatBarang    = onLihatBarang
        )

        // ── 4. Banner Asisten Chatbot ─────────────────────────────────────
        BannerAsisten(onClick = onNavigasiAsisten)

        // Ruang kosong di bawah agar konten tidak tertutup navbar
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ─── Bagian 1: Sapaan Personal ─────────────────────────────────────────────

/**
 * Menampilkan sapaan hangat kepada pemilik usaha.
 * Nama usaha ditampilkan lebih kecil sebagai sub-judul.
 */
@Composable
private fun BagianSapaan(nama: String, namaUsaha: String) {
    Column {
        Text(
            text  = "Selamat Datang,",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text       = "Bapak/Ibu $nama 👋",
            style      = MaterialTheme.typography.headlineMedium,
            color      = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Default.Store,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text  = namaUsaha,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─── Bagian 2: Kartu Ringkasan Usaha ───────────────────────────────────────

/**
 * Kartu berwarna hijau yang menampilkan pendapatan hari ini secara menonjol.
 * Angka pendapatan dibuat sangat besar dan tebal agar langsung terlihat.
 */
@Composable
private fun KartuRingkasanUsaha(
    pendapatanHariIni     : Long,
    totalTransaksiHariIni : Int,
    stokMenipis           : Int
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    // Gradien hijau dari gelap ke terang
                    brush = Brush.horizontalGradient(
                        colors = listOf(Green800, Green700)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Label kartu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.BarChart,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.8f),
                        modifier           = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text  = "Pendapatan Hari Ini",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Angka pendapatan — dibuat sangat besar dan tebal
                Text(
                    text       = formatRupiah(pendapatanHariIni),
                    fontSize   = 36.sp,         // Lebih besar dari headlineMedium
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(12.dp))

                // Info tambahan: transaksi & stok menipis
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoKecilKartu(
                        label = "Transaksi",
                        nilai = "$totalTransaksiHariIni kali"
                    )
                    InfoKecilKartu(
                        label = "Stok Menipis",
                        nilai = "$stokMenipis produk",
                        // Beri warna peringatan jika ada stok yang menipis
                        warnaWarning = stokMenipis > 0
                    )
                }
            }
        }
    }
}

/**
 * Teks kecil di dalam kartu untuk menampilkan info pendukung.
 */
@Composable
private fun InfoKecilKartu(
    label        : String,
    nilai        : String,
    warnaWarning : Boolean = false
) {
    Column {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text       = nilai,
            style      = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            // Jika peringatan, tampilkan warna kuning cerah
            color      = if (warnaWarning) Color(0xFFFFEB3B) else Color.White
        )
    }
}

// ─── Bagian 3: Menu Cepat ──────────────────────────────────────────────────

/**
 * Dua tombol besar berjejer: "Catat Penjualan" dan "Lihat Barang".
 * Setiap tombol memiliki ikon + teks yang jelas untuk memudahkan lansia.
 */
@Composable
private fun BagianMenuCepat(
    onCatatPenjualan: () -> Unit,
    onLihatBarang   : () -> Unit
) {
    Column {
        Text(
            text       = "Menu Cepat",
            style      = MaterialTheme.typography.titleLarge,
            color      = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tombol: Catat Penjualan
            TombolMenuCepat(
                label    = "Catat\nPenjualan",
                ikon     = Icons.Default.Edit,
                onClick  = onCatatPenjualan,
                modifier = Modifier.weight(1f)
            )
            // Tombol: Lihat Barang
            TombolMenuCepat(
                label    = "Lihat\nBarang",
                ikon     = Icons.Default.Inventory2,
                onClick  = onLihatBarang,
                modifier = Modifier.weight(1f),
                isSecondary = true  // Warna berbeda agar tombol bisa dibedakan
            )
        }
    }
}

/**
 * Tombol besar untuk menu cepat.
 * Menggunakan ElevatedButton dengan ikon di atas teks agar mudah dikenali.
 *
 * @param isSecondary Jika true, tombol menggunakan warna sekunder (oranye).
 */
@Composable
private fun TombolMenuCepat(
    label      : String,
    ikon       : ImageVector,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
    isSecondary: Boolean = false
) {
    val warnaTombol   = if (isSecondary) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.primaryContainer
    val warnaKonten   = if (isSecondary) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer

    ElevatedButton(
        onClick  = onClick,
        modifier = modifier.height(100.dp),   // Tinggi tombol cukup besar untuk sentuhan lansia
        shape    = RoundedCornerShape(16.dp),
        colors   = ButtonDefaults.elevatedButtonColors(
            containerColor = warnaTombol,
            contentColor   = warnaKonten
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 3.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector        = ikon,
                contentDescription = null,
                modifier           = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Bagian 4: Banner Asisten Chatbot ─────────────────────────────────────

/**
 * Banner mengambang di bawah layar untuk mengakses Asisten Chatbot.
 * Menggunakan gradien oranye agar terasa berbeda dari bagian lain.
 * Dibuat cukup besar dan menonjol agar lansia tidak melewatkannya.
 */
@Composable
private fun BannerAsisten(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Orange700, Color(0xFFFF8F00))
                )
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier             = Modifier.fillMaxWidth()
        ) {
            // Teks deskripsi di sisi kiri
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Tanya Asisten Pintar",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "Ketuk di sini untuk bertanya\ntentang usaha Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Ikon robot di sisi kanan — lebih besar agar menarik perhatian
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.SmartToy,
                    contentDescription = "Asisten Chatbot",
                    tint               = Color.White,
                    modifier           = Modifier.size(40.dp)
                )
            }
        }
    }
}

// ─── Preview ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewHomeScreen() {
    AutoCuanTheme {
        HomeScreen()
    }
}
