package com.bkkbnjabar.sipenting.data.local.entity

enum class SyncStatus {
    PENDING_UPLOAD, // Data baru yang perlu diunggah ke server
    UPLOADED,       // Data telah berhasil diunggah dan tersinkronisasi dengan server
    ERROR_UPLOAD    // Terjadi kesalahan saat mengunggah data ke server
}