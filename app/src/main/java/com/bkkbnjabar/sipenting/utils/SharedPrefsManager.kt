package com.bkkbnjabar.sipenting.utils

import android.content.Context
import android.content.SharedPreferences
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalStdlibApi::class)
class SharedPrefsManager @Inject constructor(private val context: Context) {

    private val PREF_NAME = "sipenting_prefs"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val userSessionAdapter: JsonAdapter<UserSession> = moshi.adapter<UserSession>()

    // Mengaktifkan dan menggunakan adapter ini untuk List<String>
    private val listOfStringsType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter: JsonAdapter<List<String>> = moshi.adapter(listOfStringsType)

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email" // Perbaikan typo 'val val'
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_SESSION_FULL = "user_session_full"
        const val KEY_USER_KELURAHAN_ID = "user_kelurahan_id"
        const val DEFAULT_INT_VALUE = -1
        const val KEY_DISEASE_HISTORY_OPTIONS = "disease_history_options"
        const val KEY_DRINKING_WATER_OPTIONS = "drinking_water_options"
        const val KEY_DEFECATION_FACILITY_OPTIONS = "defecation_facility_options"
        const val KEY_SOCIAL_ASSISTANCE_OPTIONS = "social_ass_options"
        const val KEY_BIRTH_ASSISTANTS_OPTIONS = "birth_assistants_options"
        const val KEY_CONTRACEPTION_OPTIONS = "contraception_options"
        const val KEY_COUNSELING_TYPES_OPTIONS = "counseling_types_options"
        const val KEY_DELIVERY_PLACES_OPTIONS = "delivery_places_options"
        const val KEY_GIVEN_BIRTH_STATUSES_OPTIONS = "given_birth_statuses_options"
        const val KEY_IMMUNIZATION_OPTIONS = "immunization_options"
        const val KEY_POSTPARTUM_COMPLICATION_OPTIONS = "postpartum_complication_options"
        const val KEY_PREGNANT_MOTHER_STATUSES_OPTIONS = "pregnant_mother_statuses_options"
    }

    // Metode bantuan untuk melakukan operasi edit secara sinkron di thread I/O
    private suspend fun SharedPreferences.Editor.commitInIO(): Boolean =
        withContext(Dispatchers.IO) {
            commit() // Melakukan commit secara sinkron
        }

    // --- Save Methods ---
    suspend fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).commitInIO()
    }

    suspend fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).commitInIO()
    }

    suspend fun saveUserId(id: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, id).commitInIO()
    }

    suspend fun saveUserName(name: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).commitInIO()
    }

    suspend fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).commitInIO()
    }

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).commitInIO()
    }

    suspend fun saveStringList(key: String, list: List<String>) {
        val json = stringListAdapter.toJson(list) // Menggunakan stringListAdapter yang sudah didefinisikan
        sharedPreferences.edit().putString(key, json).commitInIO()
    }

    /**
     * Metode untuk menyimpan detail pengguna lengkap dari UserSession secara sinkron.
     * Operasi ini akan memblokir koroutine sampai data ditulis ke disk.
     */
    suspend fun saveUserSession(userSession: UserSession) {
        val userSessionJson = userSessionAdapter.toJson(userSession)
        withContext(Dispatchers.IO) {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_USER_SESSION_FULL, userSessionJson)
            // Simpan juga secara individual untuk kemudahan akses dan backward compatibility
            editor.putString(KEY_ACCESS_TOKEN, userSession.accessToken)
//            userSession.refreshToken?.let { editor.putString(KEY_REFRESH_TOKEN, it) }
            editor.putString(KEY_USER_ID, userSession.userId)
            editor.putString(KEY_USER_NAME, userSession.userName)
            editor.putBoolean(KEY_IS_LOGGED_IN, userSession.isLoggedIn)
            userSession.kelurahanId?.let { editor.putInt(KEY_USER_KELURAHAN_ID, it) }
            userSession.email?.let { editor.putString(KEY_USER_EMAIL, it) }
            editor.commit() // Menggunakan commit() untuk memastikan write selesai secara sinkron
        }
    }

    // --- Get Methods ---
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getStringList(key: String): List<String> {
        val json = sharedPreferences.getString(key, null)
        // Menggunakan stringListAdapter yang sudah didefinisikan
        return json?.let { stringListAdapter.fromJson(it) } ?: emptyList()
    }

    fun getUserSession(): UserSession? {
        val userSessionJson = sharedPreferences.getString(KEY_USER_SESSION_FULL, null)
        return userSessionJson?.let { userSessionAdapter.fromJson(it) }
    }

    suspend fun saveUserKelurahanId(kelurahanId: Int?) {
        withContext(Dispatchers.IO) {
            if (kelurahanId != null) {
                sharedPreferences.edit().putInt(KEY_USER_KELURAHAN_ID, kelurahanId).commit()
            } else {
                sharedPreferences.edit().remove(KEY_USER_KELURAHAN_ID).commit()
            }
        }
    }

    fun getUserKelurahanId(): Int? {
        return if (sharedPreferences.contains(KEY_USER_KELURAHAN_ID)) {
            val id = sharedPreferences.getInt(KEY_USER_KELURAHAN_ID, DEFAULT_INT_VALUE)
            if (id == DEFAULT_INT_VALUE) null else id
        } else {
            null
        }
    }

    /**
     * Membersihkan semua data sesi secara sinkron.
     */
    suspend fun clearAllSessionData() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().clear().commit() // Menggunakan commit untuk jaminan sinkron
        }
    }
}
