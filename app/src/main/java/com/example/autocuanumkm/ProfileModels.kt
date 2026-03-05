package com.example.autocuanumkm.data.model

// ═══════════════════════════════════════════════════════════════════════════
//  ProfileModels.kt  — Data class untuk fitur Akun / Profil Pengguna
//
//  Berisi model:
//    • UserProfileResponse  — Data profil yang diterima dari API (GET)
//    • UpdateProfilRequest  — Data yang dikirim untuk memperbarui profil (PUT)
// ═══════════════════════════════════════════════════════════════════════════

/**
 * Model data profil pengguna yang diterima dari server.
 *
 * Endpoint: GET /api/v1/profil/{userId}
 *
 * Semua field memiliki nilai default agar Gson tidak crash jika ada field
 * yang tidak disertakan dalam respons JSON dari server.
 */
data class UserProfileResponse(
    val id          : Int    = 0,
    val nama        : String = "",  // Nama lengkap pemilik, misal: "Bapak Budi Santoso"
    val namaUsaha   : String = "",  // Nama toko/UMKM, misal: "GIMSEHAP"
    val noTelepon   : String = "",  // Nomor HP aktif, misal: "0812-3456-7890"
    val alamatToko  : String = "",  // Alamat lengkap toko
    val jamBuka     : String = "",  // Jam operasional, misal: "07:00 - 21:00 WIB"
    val rekening    : String = "",  // Info rekening, misal: "BCA - 1234567890 (Budi S)"
    val fotoProfil  : String = ""   // URL foto profil (kosong = gunakan avatar inisial)
)

/**
 * Model data yang dikirim ke server saat pengguna menyimpan perubahan profil.
 *
 * Endpoint: PUT /api/v1/profil/{userId}
 *
 * Hanya berisi field yang boleh diubah oleh pengguna.
 * Field seperti [id] dan [fotoProfil] diperbarui melalui endpoint terpisah.
 */
data class UpdateProfilRequest(
    val nama       : String,  // Nama yang ingin diperbarui
    val namaUsaha  : String,  // Nama usaha yang ingin diperbarui
    val noTelepon  : String,  // Nomor HP baru
    val alamatToko : String,  // Alamat toko yang diperbarui
    val jamBuka    : String   // Jam buka yang diperbarui
)
