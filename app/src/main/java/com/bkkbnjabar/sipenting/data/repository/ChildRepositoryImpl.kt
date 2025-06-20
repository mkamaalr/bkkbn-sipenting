package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.remote.ChildApiService
import com.bkkbnjabar.sipenting.domain.repository.ChildRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepositoryImpl @Inject constructor(
    private val api: ChildApiService
) : ChildRepository {
    // Implementasikan metode repositori khusus Anak di sini
}