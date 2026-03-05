package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  RegisterScreen.kt  — Halaman Daftar Akun Baru untuk UMKM
//
//  Fitur UI/UX ramah lansia:
//    ✓ TopAppBar dengan tombol kembali berukuran besar (52dp)
//    ✓ 4 kolom input dengan label jelas dan validasi real-time
//    ✓ Petunjuk kata sandi dalam bahasa sederhana
//    ✓ Tombol "Daftar Sekarang" 60dp tinggi, teks 20sp tebal
//    ✓ FAB "Tanya Bantuan" selalu tersedia
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────────
    var fullName        by rememberSaveable { mutableStateOf("") }
    var phoneNumber     by rememberSaveable { mutableStateOf("") }
    var email           by rememberSaveable { mutableStateOf("") }
    var businessName    by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var fullNameError   by rememberSaveable { mutableStateOf("") }
    var phoneError      by rememberSaveable { mutableStateOf("") }
    var emailError      by rememberSaveable { mutableStateOf("") }
    var businessError   by rememberSaveable { mutableStateOf("") }
    var passwordError   by rememberSaveable { mutableStateOf("") }

    // ── Validasi Input ─────────────────────────────────────────────────────
    fun validate(): Boolean {
        var valid = true
        fullNameError = when {
            fullName.isBlank()  -> { valid = false; "Nama lengkap tidak boleh kosong" }
            fullName.length < 3 -> { valid = false; "Nama lengkap terlalu pendek" }
            else                -> ""
        }
        phoneError = when {
            phoneNumber.isBlank()   -> { valid = false; "Nomor handphone tidak boleh kosong" }
            phoneNumber.length < 10 -> { valid = false; "Nomor handphone tidak valid (min. 10 angka)" }
            else                    -> ""
        }
        emailError = when {
            email.isBlank()                       -> { valid = false; "Email tidak boleh kosong" }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                                  -> { valid = false; "Format email tidak valid" }
            else                                  -> ""
        }
        businessError = when {
            businessName.isBlank() -> { valid = false; "Nama usaha tidak boleh kosong" }
            else                   -> ""
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Buat Akun Baru",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Tombol kembali berukuran besar agar mudah disentuh
                    IconButton(
                        onClick  = onNavigateToLogin,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke halaman masuk",
                            tint               = MaterialTheme.colorScheme.onSurface,
                            modifier           = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = { ChatbotFab(onClick = { /* TODO: Buka Chatbot */ }) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Deskripsi Singkat ──────────────────────────────────────────
            Text(
                text      = "Isi data di bawah ini untuk membuat akun.\nSemua kolom wajib diisi.",
                style     = MaterialTheme.typography.bodyLarge,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Kolom Nama Lengkap ─────────────────────────────────────────
            ElderlyOutlinedTextField(
                value         = fullName,
                onValueChange = { fullName = it; if (fullNameError.isNotEmpty()) fullNameError = "" },
                label         = "Nama Lengkap",
                placeholder   = "Contoh: Budi Santoso",
                leadingIcon   = Icons.Default.Person,
                keyboardType  = KeyboardType.Text,
                isError       = fullNameError.isNotEmpty(),
                errorMessage  = fullNameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Kolom Nomor Handphone ──────────────────────────────────────
            ElderlyOutlinedTextField(
                value         = phoneNumber,
                onValueChange = { phoneNumber = it; if (phoneError.isNotEmpty()) phoneError = "" },
                label         = "Nomor Handphone",
                placeholder   = "Contoh: 08123456789",
                leadingIcon   = Icons.Default.Phone,
                keyboardType  = KeyboardType.Phone,
                isError       = phoneError.isNotEmpty(),
                errorMessage  = phoneError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Kolom Email ────────────────────────────────────────────────
            ElderlyOutlinedTextField(
                value         = email,
                onValueChange = { email = it; if (emailError.isNotEmpty()) emailError = "" },
                label         = "Email",
                placeholder   = "Contoh: budi@email.com",
                leadingIcon   = Icons.Default.Email,
                keyboardType  = KeyboardType.Email,
                isError       = emailError.isNotEmpty(),
                errorMessage  = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Kolom Nama Usaha ───────────────────────────────────────────
            ElderlyOutlinedTextField(
                value         = businessName,
                onValueChange = { businessName = it; if (businessError.isNotEmpty()) businessError = "" },
                label         = "Nama Usaha (UMKM)",
                placeholder   = "Contoh: Warung Makan Bu Sari",
                leadingIcon   = Icons.Default.Store,
                keyboardType  = KeyboardType.Text,
                isError       = businessError.isNotEmpty(),
                errorMessage  = businessError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Kolom Kata Sandi ───────────────────────────────────────────
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

            Spacer(modifier = Modifier.height(10.dp))

            // ── Petunjuk Kata Sandi ────────────────────────────────────────
            Text(
                text     = "💡 Kata sandi minimal 6 karakter. Gabungkan huruf dan angka agar lebih aman.",
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Tombol Daftar Sekarang (besar & menonjol) ─────────────────
            Button(
                onClick   = { if (validate()) onRegisterSuccess() },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape     = RoundedCornerShape(12.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Daftar Sekarang",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tautan ke Halaman Masuk ────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Sudah punya akun?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick        = onNavigateToLogin,
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text  = "Masuk di sini",
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
