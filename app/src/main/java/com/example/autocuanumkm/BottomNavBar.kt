package com.example.autocuanumkm.ui.navigation

// ═══════════════════════════════════════════════════════════════════════════
//  BottomNavBar.kt  — Navigasi Bawah (Bottom Navigation Bar)
//
//  Prinsip desain untuk lansia:
//    ✓ Setiap item WAJIB menampilkan IKON + TEKS di bawahnya
//    ✓ Ukuran ikon dan teks cukup besar agar mudah dibaca
//    ✓ Item "Asisten" di tengah dibuat lebih menonjol (warna & ukuran)
//    ✓ Area sentuh yang cukup luas (alwaysShowLabel = true)
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocuanumkm.AutoCuanTheme

// ─── Model Item Navbar ──────────────────────────────────────────────────────

/**
 * Merepresentasikan satu item pada navigasi bawah.
 *
 * @param rute          String unik yang digunakan sebagai kunci navigasi.
 * @param label         Teks keterangan di bawah ikon (wajib ada untuk lansia).
 * @param ikon          Vector icon dari Material Icons.
 * @param deskripsiIkon Teks aksesibilitas untuk pengguna dengan kebutuhan khusus.
 * @param isUtama       Jika true, item ini ditampilkan lebih menonjol (khusus Asisten).
 */
data class ItemNavbar(
    val rute         : String,
    val label        : String,
    val ikon         : ImageVector,
    val deskripsiIkon: String,
    val isUtama      : Boolean = false  // Hanya "Asisten" yang bernilai true
)

// ─── Daftar Item Navigasi ──────────────────────────────────────────────────

/**
 * Tiga menu utama pada navbar bawah aplikasi AUTO CUAN.
 * Urutan: Beranda | Asisten (menonjol) | Akun Saya
 */
val daftarItemNavbar = listOf(
    ItemNavbar(
        rute          = "beranda",
        label         = "Beranda",
        ikon          = Icons.Default.Home,
        deskripsiIkon = "Halaman Beranda"
    ),
    ItemNavbar(
        rute          = "asisten",
        label         = "Asisten",
        ikon          = Icons.Default.SmartToy,
        deskripsiIkon = "Asisten Chatbot Pintar",
        isUtama       = true  // Asisten ditampilkan lebih besar & berwarna oranye
    ),
    ItemNavbar(
        rute          = "akun",
        label         = "Akun Saya",
        ikon          = Icons.Default.AccountCircle,
        deskripsiIkon = "Halaman Akun Saya"
    )
)

// ─── Composable: Bottom Navigation Bar ─────────────────────────────────────

/**
 * Komponen navigasi bawah yang dirancang khusus ramah lansia.
 *
 * Fitur:
 * - Setiap item selalu menampilkan ikon DAN label teks (tidak hanya ikon)
 * - Item "Asisten" ditampilkan lebih besar dan dengan warna oranye agar mudah ditemukan
 * - Ikon berukuran minimal 28dp — mudah ditekan jari lansia
 *
 * @param ruteAktif     Rute navigasi yang sedang aktif/terpilih saat ini.
 * @param onItemDipilih Callback yang dipanggil saat pengguna mengetuk item navbar.
 */
@Composable
fun BottomNavBar(
    ruteAktif    : String,
    onItemDipilih: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp   // Sedikit bayangan agar navbar terasa "mengambang"
    ) {
        daftarItemNavbar.forEach { item ->
            val dipilih = ruteAktif == item.rute

            NavigationBarItem(
                selected = dipilih,
                onClick  = { onItemDipilih(item.rute) },

                // ─── Ikon ────────────────────────────────────────────────────
                icon = {
                    Icon(
                        imageVector        = item.ikon,
                        contentDescription = item.deskripsiIkon,
                        // Ikon "Asisten" lebih besar agar menonjol di tengah
                        modifier = Modifier.size(if (item.isUtama) 34.dp else 28.dp)
                    )
                },

                // ─── Label Teks ───────────────────────────────────────────────
                label = {
                    Text(
                        text       = item.label,
                        fontSize   = if (item.isUtama) 14.sp else 13.sp,
                        fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                },

                // ─── Warna ────────────────────────────────────────────────────
                colors = NavigationBarItemDefaults.colors(
                    // Item "Asisten" memakai warna oranye (secondary) saat dipilih
                    selectedIconColor   = if (item.isUtama) MaterialTheme.colorScheme.secondary
                                         else MaterialTheme.colorScheme.primary,
                    selectedTextColor   = if (item.isUtama) MaterialTheme.colorScheme.secondary
                                         else MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Latar belakang indikator saat dipilih
                    indicatorColor      = if (item.isUtama) MaterialTheme.colorScheme.secondaryContainer
                                         else MaterialTheme.colorScheme.primaryContainer
                ),

                // WAJIB true — label teks selalu tampil meski tidak aktif (prinsip lansia)
                alwaysShowLabel = true
            )
        }
    }
}

// ─── Preview ───────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewBottomNavBar() {
    AutoCuanTheme {
        BottomNavBar(ruteAktif = "beranda", onItemDipilih = {})
    }
}
