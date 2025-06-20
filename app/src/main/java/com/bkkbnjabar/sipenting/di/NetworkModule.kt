package com.bkkbnjabar.sipenting.di

import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService
import com.bkkbnjabar.sipenting.data.remote.BreastfeedingMotherApiService // Import ini
import com.bkkbnjabar.sipenting.data.remote.ChildApiService // Import ini
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.utils.AuthInterceptor
import com.bkkbnjabar.sipenting.utils.Constants
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
import javax.inject.Provider
import kotlin.jvm.JvmStatic

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            authInterceptor: AuthInterceptor,
            tokenAuthenticator: TokenAuthenticator
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .authenticator(tokenAuthenticator)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providePregnantMotherApiService(retrofit: Retrofit): PregnantMotherApiService {
            return retrofit.create(PregnantMotherApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideBreastfeedingMotherApiService(retrofit: Retrofit): BreastfeedingMotherApiService {
            return retrofit.create(BreastfeedingMotherApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideChildApiService(retrofit: Retrofit): ChildApiService {
            return retrofit.create(ChildApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideLookupApiService(retrofit: Retrofit): LookupApiService {
            return retrofit.create(LookupApiService::class.java)
        }
    }
}