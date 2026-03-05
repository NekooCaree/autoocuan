package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  MainScreen.kt  — Shell Utama dengan Bottom Navigation Bar
//
//  MainScreen adalah "wadah" yang mengelola:
//    • NavController dalam (innerNavController) untuk navigasi antar tab
//    • Scaffold dengan BottomNavBar di bagian bawah
//    • NavHost dalam (inner) untuk menampilkan konten setiap tab
//
//  Layar yang tersedia dalam tab:
//    • beranda  → HomeScreen
//    • asisten  → AsistenScreen (placeholder, kembangkan nanti)
//    • akun     → AkunScreen    (profil + pengaturan lengkap)
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.autocuanumkm.ui.akun.AkunScreen
import com.example.autocuanumkm.ui.chat.ChatScreen
import com.example.autocuanumkm.ui.home.HomeScreen
import com.example.autocuanumkm.ui.navigation.BottomNavBar

/**
 * Layar utama aplikasi setelah login berhasil.
 * Mengelola navigasi bottom tab secara mandiri menggunakan [innerNavController].
 *
 * @param onKeluar Callback yang dipanggil setelah logout berhasil → navigasi ke Login.
 */
@Composable
fun MainScreen(onKeluar: () -> Unit = {}) {
    // NavController khusus untuk navigasi antar tab di dalam MainScreen
    val innerNavController = rememberNavController()

    // Pantau rute aktif agar BottomNavBar tahu item mana yang harus di-highlight
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val ruteAktif = navBackStackEntry?.destination?.route ?: "beranda"

    Scaffold(
        bottomBar = {
            // ─── Bottom Navigation Bar ─────────────────────────────────────
            BottomNavBar(
                ruteAktif     = ruteAktif,
                onItemDipilih = { rute ->
                    innerNavController.navigate(rute) {
                        // Kembali ke start destination agar tidak menumpuk back stack
                        popUpTo(innerNavController.graph.findStartDestination().id) {
                            saveState = true   // Simpan state setiap tab
                        }
                        // Hindari duplikat saat mengetuk tab yang sama
                        launchSingleTop = true
                        // Pulihkan state tab yang sebelumnya dibuka
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->

        // ─── NavHost Dalam: Mengelola Konten Setiap Tab ───────────────────
        NavHost(
            navController    = innerNavController,
            startDestination = "beranda",           // Tab yang dibuka pertama kali
            modifier         = Modifier.padding(paddingValues)
        ) {

            // Tab 1: Beranda
            composable("beranda") {
                HomeScreen(
                    onNavigasiAsisten = {
                        // Navigasi ke tab Asisten saat banner diketuk
                        innerNavController.navigate("asisten") {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onCatatPenjualan = { /* TODO: Navigasi ke layar Catat Penjualan */ },
                    onLihatBarang    = { /* TODO: Navigasi ke layar Daftar Barang */   }
                )
            }

            // Tab 2: Asisten Chatbot (full implementation)
            composable("asisten") {
                ChatScreen()
            }

            // Tab 3: Akun Saya (Profil + Pengaturan)
            composable("akun") {
                AkunScreen(
                    onNavigasiAsisten = {
                        // Dari menu Akun, navigasi ke tab Asisten
                        innerNavController.navigate("asisten") {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onUbahDataDiri = { /* TODO: Navigasi ke layar Edit Profil */ },
                    onKeluar       = onKeluar  // Diteruskan dari NavGraph → Login
                )
            }
        }
    }
}

// ─── Preview ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewMainScreen() {
    AutoCuanTheme {
        MainScreen()
    }
}
