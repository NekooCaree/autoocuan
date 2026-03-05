package com.example.autocuanumkm.ui.akun

// ═══════════════════════════════════════════════════════════════════════════
//  AkunScreen.kt  — Halaman Akun Saya (Profil + Pengaturan)
//
//  Prinsip desain untuk lansia:
//    ✓ Font besar (min 16sp biasa, 20sp+ judul)
//    ✓ Setiap baris menu: IKON di kiri + TEKS di kanannya + area ketuk luas (min 64dp)
//    ✓ Semua pengaturan terlihat dalam satu layar gulir (tidak ada menu bersarang)
//    ✓ Tombol Keluar berwarna merah & diberi jarak aman dari menu lain
//    ✓ Dialog konfirmasi sebelum logout mencegah ketukan tidak sengaja
//
//  Struktur layar (atas ke bawah):
//    1. TopAppBar    — "Akun Saya"
//    2. Header Profil — Avatar besar, Nama, Nama Usaha, No. HP
//    3. Tombol "Ubah Data Diri"
//    4. Kategori: Data Usaha    → Informasi Toko | Rekening Bank
//    5. Kategori: Keamanan      → Ganti Kata Sandi
//    6. Kategori: Bantuan       → Tanya Asisten | Cara Pakai | Syarat & Ketentuan
//    7. Tombol Keluar (merah, dengan jarak aman di bawah)
// ═══════════════════════════════════════════════════════════════════════════

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autocuanumkm.AutoCuanTheme
import com.example.autocuanumkm.data.model.UserProfileResponse

// ─── Composable Utama: AkunScreen ─────────────────────────────────────────

/**
 * Layar utama untuk tab "Akun Saya".
 * Menampilkan profil pengguna dan daftar menu pengaturan.
 *
 * @param viewModel         ViewModel yang mengelola data profil & status logout.
 * @param onNavigasiAsisten Callback untuk membuka tab Asisten dari banner di menu.
 * @param onUbahDataDiri    Callback untuk membuka layar edit profil.
 * @param onKeluar          Callback yang dipanggil setelah logout berhasil → navigasi ke Login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkunScreen(
    viewModel         : ProfileViewModel = viewModel(
        // Factory khusus agar AndroidViewModel mendapat Application context
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    ),
    onNavigasiAsisten : () -> Unit = {},
    onUbahDataDiri    : () -> Unit = {},
    onKeluar          : () -> Unit = {}
) {
    // Observasi status profil dari ViewModel
    val status      by viewModel.statusProfil.collectAsState()

    // Observasi sinyal logout — saat true, jalankan navigasi ke Login
    val sudahKeluar by viewModel.sudahKeluar.collectAsState()

    // Kontrol visibilitas dialog konfirmasi keluar
    var tampilDialogKeluar by remember { mutableStateOf(false) }

    // ── Efek samping: navigasi ke Login saat logout selesai ─────────────────
    LaunchedEffect(sudahKeluar) {
        if (sudahKeluar) onKeluar()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text       = "Akun Saya",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Konten utama berdasarkan status ─────────────────────────────
            when (val s = status) {
                is StatusProfil.Memuat -> TampilanMemuat()

                is StatusProfil.Sukses -> KontenAkun(
                    data               = s.data,
                    onNavigasiAsisten  = onNavigasiAsisten,
                    onUbahDataDiri     = onUbahDataDiri,
                    onKeluarDiminta    = { tampilDialogKeluar = true }
                )

                is StatusProfil.Gagal  -> TampilanGagal(
                    pesan      = s.pesan,
                    onCobaLagi = { viewModel.cobaLagi() }
                )
            }
        }
    }

    // ── Dialog Konfirmasi Keluar (ditampilkan di atas semua konten) ──────────
    if (tampilDialogKeluar) {
        DialogKonfirmasiKeluar(
            onKonfirmasi = {
                tampilDialogKeluar = false
                viewModel.keluar()  // Hapus sesi + sinyal navigasi
            },
            onBatal = { tampilDialogKeluar = false }
        )
    }
}

// ─── Tampilan: Loading ─────────────────────────────────────────────────────

@Composable
private fun TampilanMemuat() {
    Column(
        modifier            = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier    = Modifier.size(64.dp),
            color       = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text  = "Sedang memuat data akun...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

// ─── Tampilan: Error ───────────────────────────────────────────────────────

@Composable
private fun TampilanGagal(pesan: String, onCobaLagi: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = Icons.Default.Warning,
            contentDescription = "Terjadi kesalahan",
            tint               = MaterialTheme.colorScheme.error,
            modifier           = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text      = "Gagal Memuat Akun",
            style     = MaterialTheme.typography.headlineSmall,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text      = pesan,
            style     = MaterialTheme.typography.bodyLarge,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick  = onCobaLagi,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Coba Lagi", style = MaterialTheme.typography.labelLarge)
        }
    }
}

// ─── Konten Akun (Status Sukses) ───────────────────────────────────────────

/**
 * Konten utama layar Akun — ditampilkan saat data profil berhasil dimuat.
 */
@Composable
private fun KontenAkun(
    data             : UserProfileResponse,
    onNavigasiAsisten: () -> Unit,
    onUbahDataDiri   : () -> Unit,
    onKeluarDiminta  : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── 1. Header Profil ───────────────────────────────────────────────
        HeaderProfil(
            nama      = data.nama,
            namaUsaha = data.namaUsaha,
            noTelepon = data.noTelepon
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── 2. Tombol Ubah Data Diri ───────────────────────────────────────
        TombolUbahDataDiri(onClick = onUbahDataDiri)

        Spacer(modifier = Modifier.height(28.dp))

        // ── 3. Kategori: Data Usaha ────────────────────────────────────────
        LabelKategori(judul = "Data Usaha")
        Spacer(modifier = Modifier.height(8.dp))
        KartuGrupMenu {
            ItemMenu(
                ikon    = Icons.Default.Store,
                judul   = "Informasi Toko",
                sub     = if (data.alamatToko.isNotEmpty()) data.alamatToko else "Alamat & jam buka toko",
                onClick = { /* TODO: Navigasi ke layar Informasi Toko */ }
            )
            HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
            ItemMenu(
                ikon    = Icons.Default.AccountBalance,
                judul   = "Rekening Bank / e-Wallet",
                sub     = if (data.rekening.isNotEmpty()) data.rekening else "Belum ada rekening",
                onClick = { /* TODO: Navigasi ke layar Rekening */ }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 4. Kategori: Keamanan ──────────────────────────────────────────
        LabelKategori(judul = "Keamanan")
        Spacer(modifier = Modifier.height(8.dp))
        KartuGrupMenu {
            ItemMenu(
                ikon    = Icons.Default.Lock,
                judul   = "Ganti Kata Sandi",
                sub     = "Ubah kata sandi akun Anda",
                onClick = { /* TODO: Navigasi ke layar Ganti Kata Sandi */ }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 5. Kategori: Bantuan & Lainnya ────────────────────────────────
        LabelKategori(judul = "Bantuan & Lainnya")
        Spacer(modifier = Modifier.height(8.dp))
        KartuGrupMenu {
            ItemMenu(
                ikon       = Icons.Default.SmartToy,
                judul      = "Tanya Asisten (Chatbot)",
                sub        = "Chat dengan asisten pintar kami",
                onClick    = onNavigasiAsisten,
                warnaIkon  = MaterialTheme.colorScheme.secondary  // Warna oranye agar menonjol
            )
            HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
            ItemMenu(
                ikon    = Icons.Default.MenuBook,
                judul   = "Cara Pakai Aplikasi",
                sub     = "Panduan penggunaan AUTO CUAN",
                onClick = { /* TODO: Navigasi ke layar Tutorial */ }
            )
            HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
            ItemMenu(
                ikon    = Icons.Default.Description,
                judul   = "Syarat dan Ketentuan",
                sub     = "Kebijakan privasi & ketentuan layanan",
                onClick = { /* TODO: Navigasi ke layar Syarat & Ketentuan */ }
            )
        }

        // ── 6. Tombol Keluar ──────────────────────────────────────────────
        // Jarak yang cukup besar agar tidak terpencet tidak sengaja
        Spacer(modifier = Modifier.height(40.dp))
        TombolKeluar(onClick = onKeluarDiminta)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Komponen: Header Profil ───────────────────────────────────────────────

/**
 * Bagian atas halaman akun — menampilkan avatar, nama, usaha, dan nomor HP.
 *
 * Avatar menggunakan inisial nama karena tidak ada upload foto di tahap ini.
 */
@Composable
private fun HeaderProfil(nama: String, namaUsaha: String, noTelepon: String) {
    Column(
        modifier           = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar: lingkaran hijau besar dengan inisial nama
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = inisialNama(nama),
                fontSize   = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Nama pemilik — teks paling besar di halaman ini
        Text(
            text       = nama,
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color      = MaterialTheme.colorScheme.onBackground,
            textAlign  = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Nama usaha
        Text(
            text       = namaUsaha,
            style      = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nomor HP
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Default.Phone,
                contentDescription = "Nomor HP",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text  = noTelepon,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Komponen: Tombol Ubah Data Diri ──────────────────────────────────────

/**
 * Tombol besar di bawah header profil untuk membuka form edit profil.
 */
@Composable
private fun TombolUbahDataDiri(onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape    = RoundedCornerShape(12.dp),
        border   = ButtonDefaults.outlinedButtonBorder.copy(
            width = ButtonDefaults.outlinedButtonBorder.width
        ),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector        = Icons.Default.Edit,
            contentDescription = null,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text  = "Ubah Data Diri",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ─── Komponen: Label Kategori ─────────────────────────────────────────────

/**
 * Judul kategori (misal: "Data Usaha", "Keamanan") di atas grup menu.
 * Ukuran font 16sp, warna abu-abu — tidak terlalu mencolok tapi tetap terbaca.
 */
@Composable
private fun LabelKategori(judul: String) {
    Text(
        text       = judul.uppercase(),
        style      = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp,
        modifier   = Modifier.padding(horizontal = 4.dp)
    )
}

// ─── Komponen: Kartu Grup Menu ────────────────────────────────────────────

/**
 * Kartu putih dengan sudut membulat yang membungkus sekumpulan [ItemMenu].
 * Menggunakan slot pattern (lambda content) agar fleksibel.
 */
@Composable
private fun KartuGrupMenu(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(content = content)
    }
}

// ─── Komponen: Item Menu Pengaturan ───────────────────────────────────────

/**
 * Satu baris menu pengaturan yang terdiri dari:
 *   [Ikon] | [Judul + Sub-judul] | [Panah >]
 *
 * Tinggi minimum 64dp agar area sentuh cukup luas untuk jari lansia.
 *
 * @param ikon      Ikon Material dari sisi kiri
 * @param judul     Teks utama menu (font besar, 18sp)
 * @param sub       Teks deskripsi kecil di bawah judul
 * @param warnaIkon Warna ikon — default hijau, bisa diubah (misal: oranye untuk Asisten)
 * @param onClick   Aksi saat baris diketuk
 */
@Composable
private fun ItemMenu(
    ikon     : ImageVector,
    judul    : String,
    sub      : String    = "",
    warnaIkon: Color     = MaterialTheme.colorScheme.primary,
    onClick  : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Area ketuk mencakup seluruh baris — penting untuk lansia
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Kotak Ikon (48dp × 48dp, latar kontainer) ──────────────────────
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(warnaIkon.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = ikon,
                contentDescription = judul,
                tint               = warnaIkon,
                modifier           = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // ── Teks Judul + Sub ────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = judul,
                style      = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            if (sub.isNotEmpty()) {
                Text(
                    text   = sub,
                    style  = MaterialTheme.typography.bodyMedium,
                    color  = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── Ikon Panah Kanan ────────────────────────────────────────────────
        Icon(
            imageVector        = Icons.Default.ArrowForwardIos,
            contentDescription = "Buka $judul",
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier           = Modifier.size(16.dp)
        )
    }
}

// ─── Komponen: Tombol Keluar ──────────────────────────────────────────────

/**
 * Tombol logout berwarna merah.
 *
 * Diletakkan paling bawah dengan jarak 40dp dari menu terakhir agar
 * tidak terpencet secara tidak sengaja oleh pengguna lansia.
 */
@Composable
private fun TombolKeluar(onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),           // Tinggi besar agar mudah ditekan
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,  // MERAH
            contentColor   = MaterialTheme.colorScheme.onError
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(
            imageVector        = Icons.Default.ExitToApp,
            contentDescription = null,
            modifier           = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text  = "Keluar Akun (Logout)",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ─── Komponen: Dialog Konfirmasi Keluar ───────────────────────────────────

/**
 * Dialog konfirmasi sebelum logout — mencegah pengguna tidak sengaja keluar.
 *
 * Dua pilihan:
 *  - "Ya, Keluar"  → merah, menjalankan logout
 *  - "Batal"       → abu-abu, menutup dialog
 */
@Composable
private fun DialogKonfirmasiKeluar(
    onKonfirmasi: () -> Unit,
    onBatal     : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onBatal,
        icon = {
            Icon(
                imageVector        = Icons.Default.ExitToApp,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.error,
                modifier           = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text      = "Keluar dari Akun?",
                style     = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text      = "Apakah Anda yakin ingin keluar dari aplikasi AUTO CUAN?\n\n" +
                            "Anda perlu masuk kembali dengan kata sandi untuk menggunakan aplikasi.",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        },
        confirmButton = {
            // Tombol konfirmasi — merah karena aksi kritis
            Button(
                onClick = onKonfirmasi,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor   = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = "Ya, Keluar", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            // Tombol batal — tampilan lebih tenang agar tidak bingung lansia
            OutlinedButton(
                onClick  = onBatal,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Batal", style = MaterialTheme.typography.labelLarge)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

// ─── Fungsi Pembantu: Inisial Nama ─────────────────────────────────────────

/**
 * Mengambil maksimal 2 inisial dari nama lengkap.
 * Contoh: "Bapak Budi Santoso" → "BS"  |  "Siti" → "S"
 */
private fun inisialNama(nama: String): String =
    nama.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

// ─── Preview ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewAkunScreen() {
    AutoCuanTheme {
        // Preview menggunakan data langsung karena ViewModel butuh konteks nyata
        KontenAkun(
            data = UserProfileResponse(
                id         = 1,
                nama       = "Bapak Budi Santoso",
                namaUsaha  = "GIMSEHAP",
                noTelepon  = "0812-3456-7890",
                alamatToko = "Jl. Pahlawan No. 10, Jakarta",
                jamBuka    = "07:00 - 21:00 WIB",
                rekening   = "BCA - 1234567890"
            ),
            onNavigasiAsisten = {},
            onUbahDataDiri    = {},
            onKeluarDiminta   = {}
        )
    }
}
