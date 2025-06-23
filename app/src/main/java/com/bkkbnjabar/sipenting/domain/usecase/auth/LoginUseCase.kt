package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.utils.Resource

/**
 * Interface untuk use case login.
 * Mendefinisikan kontrak untuk menjalankan operasi login.
 */
interface LoginUseCase {
    /**
     * Executes the login operation with the given login request.
     * Mengesekusi operasi login dengan permintaan login yang diberikan.
     *
     * @param request The LoginRequest containing user credentials (e.g., username, password).
     * LoginRequest yang berisi kredensial pengguna (misalnya, username, password).
     * @return A Resource object indicating the success, error, or loading state of the operation,
     * along with the UserSession data if successful.
     * Objek Resource yang menunjukkan status sukses, error, atau loading dari operasi,
     * bersama dengan data UserSession jika berhasil.
     */
    suspend fun execute(request: LoginRequest): Resource<UserSession>
}