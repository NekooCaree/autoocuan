package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  LoginScreen.kt  — Halaman Masuk untuk pengguna UMKM
//
//  Fitur UI/UX ramah lansia:
//    ✓ Font besar (18sp+) di semua teks
//    ✓ Tombol "Masuk" tinggi 60dp, teks 20sp tebal
//    ✓ Pesan error langsung di bawah field yang bermasalah
//    ✓ Keyboard otomatis muncul sesuai tipe input (Phone / Password)
//    ✓ FAB "Tanya Bantuan" selalu tersedia di pojok kanan bawah
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onNavigateToRegister      : () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess            : () -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────────
    var identifier      by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var identifierError by rememberSaveable { mutableStateOf("") }
    var passwordError   by rememberSaveable { mutableStateOf("") }

    // ── Validasi Input ─────────────────────────────────────────────────────
    fun validate(): Boolean {
        var valid = true
        val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()
        val isPhone = identifier.all { it.isDigit() || it == '+' || it == '-' }
        identifierError = when {
            identifier.isBlank() -> { valid = false; "Nomor HP atau email tidak boleh kosong" }
            !isEmail && (!isPhone || identifier.length < 10) ->
                { valid = false; "Masukkan nomor HP yang valid (min. 10 angka) atau email yang benar" }
            else -> ""
        }
        passwordError = when {
            password.isBlank()  -> { valid = false; "Kata sandi tidak boleh kosong" }
            password.length < 6 -> { valid = false; "Kata sandi minimal 6 karakter" }
            else                -> ""
        }
        return valid
    }

    // ── Layout ─────────────────────────────────────────────────────────────
    Scaffold(
        containerColor       = MaterialTheme.colorScheme.background,
        floatingActionButton = { ChatbotFab(onClick = { /* TODO: Buka Chatbot */ }) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo & Nama Aplikasi ───────────────────────────────────────
            AppLogoSection()

            Spacer(modifier = Modifier.height(36.dp))

            // ── Judul Halaman ──────────────────────────────────────────────
            Text(
                text      = "Masuk ke Akun Anda",
                style     = MaterialTheme.typography.headlineMedium,
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text      = "Selamat datang kembali! 👋",
                style     = MaterialTheme.typography.bodyLarge,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Kolom Nomor HP atau Email ──────────────────────────────────
            ElderlyOutlinedTextField(
                value         = identifier,
                onValueChange = { identifier = it; if (identifierError.isNotEmpty()) identifierError = "" },
                label         = "Nomor HP atau Email",
                leadingIcon   = Icons.Default.Person,
                keyboardType  = KeyboardType.Email,
                isError       = identifierError.isNotEmpty(),
                errorMessage  = identifierError
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElderlyPasswordTextField(
                value                     = password,
                onValueChange             = { password = it; if (passwordError.isNotEmpty()) passwordError = "" },
                label                     = "Kata Sandi",
                leadingIcon               = Icons.Default.Lock,
                passwordVisible           = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                isError                   = passwordError.isNotEmpty(),
                errorMessage              = passwordError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tautan Lupa Kata Sandi ─────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text(
                        text  = "Lupa Kata Sandi?",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight      = FontWeight.SemiBold,
                            textDecoration  = TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tombol Masuk (besar & menonjol) ───────────────────────────
            Button(
                onClick   = { if (validate()) onLoginSuccess() },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(60.dp),              // Tinggi 60dp agar mudah disentuh
                shape     = RoundedCornerShape(12.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Masuk",
                    fontSize   = 20.sp,          // Font besar agar teks tombol jelas
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tautan ke Halaman Daftar ───────────────────────────────────
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment   = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Belum punya akun?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick        = onNavigateToRegister,
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text  = "Daftar di sini",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight     = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Spacer ekstra agar konten tidak tertutup FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
