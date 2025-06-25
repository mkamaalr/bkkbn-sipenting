package com.bkkbnjabar.sipenting.utils

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An OkHttp Authenticator that handles automatic token refreshing.
 * If an API call fails with a 401 Unauthorized error, this class
 * will attempt to refresh the token and retry the original request.
 *
 * (Note: The refresh logic itself needs to be implemented in AuthRepository)
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager
    // TODO: Inject AuthRepository here later to perform the actual refresh call
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // TODO: Implement token refresh logic here.
        // 1. Get the old refresh token from SharedPreferences.
        // 2. Make a synchronous API call to your refresh token endpoint.
        // 3. If successful, save the new access and refresh tokens.
        // 4. Build a new request with the new access token and return it.
        // 5. If refresh fails, return null to cancel the request chain.

        // For now, we return null, meaning no retry will be attempted.
        return null
    }
}
