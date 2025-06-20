package com.bkkbnjabar.sipenting.utils

import android.content.Context
import android.content.SharedPreferences
import com.bkkbnjabar.sipenting.data.model.Kecamatan // Import domain model Kecamatan
import com.bkkbnjabar.sipenting.data.model.Kelurahan
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    // Inisialisasi Moshi adapter untuk serialisasi/deserialisasi objek kompleks
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val kelurahanAdapter = moshi.adapter(Kelurahan::class.java)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(Constants.KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(Constants.KEY_USER_ID, null)
    }

    // Baru: Menyimpan data lokasi pengguna
    fun saveUserLocation(kelurahan: Kelurahan) {
        val json = kelurahanAdapter.toJson(kelurahan)
        prefs.edit().putString(Constants.KEY_USER_LOCATION_DATA, json).apply()
    }

    // Baru: Mengambil data lokasi pengguna
    fun getUserLocation(): Kelurahan? {
        val json = prefs.getString(Constants.KEY_USER_LOCATION_DATA, null)
        return json?.let {
            kelurahanAdapter.fromJson(it)
        }
    }

    fun clearAuthData() {
        prefs.edit()
            .remove(Constants.KEY_AUTH_TOKEN)
            .remove(Constants.KEY_USER_ID)
            .remove(Constants.KEY_USER_LOCATION_DATA) // Hapus juga data lokasi
            .apply()
    }
}
