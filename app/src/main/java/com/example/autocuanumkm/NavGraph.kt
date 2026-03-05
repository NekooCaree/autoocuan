package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  NavGraph.kt  — Pengaturan navigasi antar layar menggunakan Navigation Compose
//
//  Alur navigasi:
//    Login ──── (login sukses) ──→ Main (berisi BottomNav)
//      └────── (daftar dulu) ──→ Register ──── (daftar sukses) ──→ Main
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/** Rute navigasi untuk setiap layar. Tambahkan rute baru di sini jika perlu. */
sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")    // Shell utama dengan BottomNavBar
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        // ─── Layar Login ───────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister       = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { /* TODO: Halaman lupa kata sandi */ },
                onLoginSuccess             = {
                    // Navigasi ke Main dan hapus Login dari back stack
                    // agar pengguna tidak bisa kembali ke Login dengan tombol Back
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Layar Registrasi ──────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    // Setelah daftar berhasil, langsung masuk ke halaman utama
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Layar Utama (dengan Bottom Navigation) ────────────────────────
        composable(Screen.Main.route) {
            MainScreen(
                onKeluar = {
                    // Setelah logout: kembali ke Login dan bersihkan seluruh back stack
                    // Pengguna tidak bisa menekan tombol Back untuk masuk kembali
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
