import java.util.Properties

// ─── Membaca API Key dari local.properties ────────────────────────────────
//
//  CARA MENAMBAHKAN GEMINI API KEY (AMAN):
//  1. Buka / buat file "local.properties" di ROOT proyek (sejajar build.gradle.kts)
//  2. Tambahkan baris ini:  GEMINI_API_KEY=AIzaSy_...kunci_anda_di_sini...
//  3. File local.properties sudah ada di .gitignore — API key TIDAK akan ter-upload ke GitHub
//  4. Dapatkan API key gratis di: https://aistudio.google.com/app/apikey
//
// ─────────────────────────────────────────────────────────────────────────

val localProperties = Properties().apply {
    // Coba baca local.properties; jika belum ada, gunakan nilai kosong
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.autocuanumkm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.autocuanumkm"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Mengekspos GEMINI_API_KEY ke kode Kotlin melalui BuildConfig.GEMINI_API_KEY
        // Nilai diambil dari local.properties — TIDAK pernah di-hardcode di sini
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose      = true
        buildConfig  = true  // WAJIB: aktifkan agar BuildConfig.GEMINI_API_KEY tersedia
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    // Google Gemini AI SDK — untuk fitur chatbot UMKM
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
}
