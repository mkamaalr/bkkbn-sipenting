package com.bkkbnjabar.sipenting.di

import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService
import com.bkkbnjabar.sipenting.utils.AuthInterceptor
import com.bkkbnjabar.sipenting.utils.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // BASE URL hanya didefinisikan di sini
    private const val BASE_URL = "http://192.168.1.15:8000/api/" // <<< GANTI DENGAN BASE URL API ANDA!
//    private const val BASE_URL = "https://sipenting.bkkbnjabar.id/api/" // <<< GANTI DENGAN BASE URL API ANDA!

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BASE_URL // Menyediakan BASE_URL sebagai String

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Atur level logging sesuai kebutuhan
        }
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi { // Moshi disediakan di sini karena terkait dengan konversi Retrofit
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor, // Disediakan Hilt via @Inject constructor
        tokenAuthenticator: TokenAuthenticator // Disediakan Hilt via @Inject constructor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String, // Mengambil dari provideBaseUrl()
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    // --- Semua API Services disediakan di NetworkModule ---
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLookupApiService(retrofit: Retrofit): LookupApiService {
        return retrofit.create(LookupApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePregnantMotherApiService(retrofit: Retrofit): PregnantMotherApiService {
        return retrofit.create(PregnantMotherApiService::class.java)
    }
}
