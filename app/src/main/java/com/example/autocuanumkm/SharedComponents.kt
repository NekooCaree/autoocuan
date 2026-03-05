package com.example.autocuanumkm

// ═══════════════════════════════════════════════════════════════════════════
//  SharedComponents.kt  — Komponen UI reusable yang ramah lansia
//
//  Komponen:
//    • AppLogoSection          — Logo & nama aplikasi
//    • ElderlyOutlinedTextField — Input teks ukuran besar dengan label & error
//    • ElderlyPasswordTextField — Input kata sandi dengan tombol tampilkan/sembunyikan
//    • ChatbotFab               — FAB "Tanya Bantuan" di pojok layar
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Logo & Nama Aplikasi ──────────────────────────────────────────────────

/**
 * Bagian atas halaman login/register: ikon bulat "AC" + nama aplikasi.
 */
@Composable
fun AppLogoSection(modifier: Modifier = Modifier) {
    Column(
        modifier           = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ikon sederhana: lingkaran hijau dengan inisial "AC"
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "AC",
                color      = MaterialTheme.colorScheme.onPrimary,
                fontSize   = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text  = "AUTO CUAN",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text  = "Pendamping Usaha UMKM Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

// ─── TextField Ukuran Besar ────────────────────────────────────────────────

/**
 * OutlinedTextField ramah lansia: font besar (18sp), label jelas, pesan error menonjol.
 *
 * @param keyboardType Tentukan [KeyboardType.Phone] untuk input nomor HP.
 */
@Composable
fun ElderlyOutlinedTextField(
    value        : String,
    onValueChange: (String) -> Unit,
    label        : String,
    modifier     : Modifier = Modifier,
    placeholder  : String = "",
    leadingIcon  : ImageVector? = null,
    keyboardType : KeyboardType = KeyboardType.Text,
    isError      : Boolean = false,
    errorMessage : String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 60.dp),
            label = {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text  = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector     = icon,
                        contentDescription = null,
                        tint            = if (isError) MaterialTheme.colorScheme.error
                                          else MaterialTheme.colorScheme.primary,
                        modifier        = Modifier.size(24.dp)
                    )
                }
            },
            isError         = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine      = true,
            shape           = RoundedCornerShape(12.dp),
            textStyle       = MaterialTheme.typography.bodyLarge,
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor     = MaterialTheme.colorScheme.error,
                focusedLabelColor    = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Pesan error — ditampilkan di bawah field jika validasi gagal
        if (isError && errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "⚠ $errorMessage",
                style    = MaterialTheme.typography.labelMedium,
                color    = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ─── Password TextField ────────────────────────────────────────────────────

/**
 * Seperti [ElderlyOutlinedTextField] tetapi menambahkan tombol tampilkan/sembunyikan
 * kata sandi di sisi kanan. Tombol dibuat besar (48.dp) agar mudah disentuh lansia.
 */
@Composable
fun ElderlyPasswordTextField(
    value                    : String,
    onValueChange            : (String) -> Unit,
    label                    : String,
    modifier                 : Modifier = Modifier,
    leadingIcon              : ImageVector? = null,
    passwordVisible          : Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    isError                  : Boolean = false,
    errorMessage             : String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 60.dp),
            label = {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        tint               = if (isError) MaterialTheme.colorScheme.error
                                             else MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            },
            trailingIcon = {
                // Tombol besar (48dp) untuk mempermudah ketukan lansia
                IconButton(
                    onClick  = onPasswordVisibilityToggle,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector        = if (passwordVisible) Icons.Default.VisibilityOff
                                             else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Sembunyikan kata sandi"
                                             else "Tampilkan kata sandi",
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            isError         = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine      = true,
            shape           = RoundedCornerShape(12.dp),
            textStyle       = MaterialTheme.typography.bodyLarge,
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor     = MaterialTheme.colorScheme.error,
                focusedLabelColor    = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        if (isError && errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "⚠ $errorMessage",
                style    = MaterialTheme.typography.labelMedium,
                color    = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ─── Chatbot Floating Action Button ───────────────────────────────────────

/**
 * Tombol mengambang (FAB) oranye di sudut kanan bawah untuk membuka Chatbot.
 *
 * Menggunakan [ExtendedFloatingActionButton] dengan TEKS + IKON sehingga
 * pengguna lansia langsung mengerti fungsinya tanpa harus menebak.
 */
@Composable
fun ChatbotFab(
    onClick : () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick        = onClick,
        modifier       = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor   = MaterialTheme.colorScheme.onSecondary,
        icon = {
            Icon(
                imageVector        = Icons.Default.Chat,
                contentDescription = "Tanya Bantuan",
                modifier           = Modifier.size(26.dp)
            )
        },
        text = {
            Text(
                text  = "Tanya Bantuan",
                style = MaterialTheme.typography.labelLarge
            )
        }
    )
}
