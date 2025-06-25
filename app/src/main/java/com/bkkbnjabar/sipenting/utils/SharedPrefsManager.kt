package com.bkkbnjabar.sipenting.utils

import android.content.Context
import android.content.SharedPreferences
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "sipenting_prefs"
        private const val KEY_USER_SESSION = "user_session"
        // Kunci untuk menyimpan daftar opsi dari Lookup API
        const val KEY_DISEASE_HISTORY_OPTIONS = "disease_history_options"
        const val KEY_DRINKING_WATER_OPTIONS = "drinking_water_options"
        const val KEY_DEFECATION_FACILITY_OPTIONS = "defecation_facility_options"
        const val KEY_SOCIAL_ASSISTANCE_OPTIONS = "social_assistance_options"
        // TODO: ADD ANOTHER KEYS HERE
    }

    fun saveUserSession(userSession: UserSession?) {
        val editor = sharedPreferences.edit()
        if (userSession == null) {
            editor.remove(KEY_USER_SESSION)
        } else {
            val json = gson.toJson(userSession)
            editor.putString(KEY_USER_SESSION, json)
        }
        editor.apply()
    }

    fun getUserSession(): UserSession? {
        val json = sharedPreferences.getString(KEY_USER_SESSION, null) ?: return null
        return try {
            gson.fromJson(json, UserSession::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveStringList(key: String, list: List<String>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getStringList(key: String): List<String> {
        val json = sharedPreferences.getString(key, null)
        if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json, type)
        }
        return emptyList()
    }

    /**
     * Membersihkan semua data di SharedPreferences, efektif untuk logout.
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
