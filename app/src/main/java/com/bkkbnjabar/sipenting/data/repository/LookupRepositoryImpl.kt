package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.KabupatenDao
import com.bkkbnjabar.sipenting.data.local.dao.KecamatanDao
import com.bkkbnjabar.sipenting.data.local.dao.KelurahanDao
import com.bkkbnjabar.sipenting.data.local.dao.ProvinsiDao
import com.bkkbnjabar.sipenting.data.local.dao.RtDao
import com.bkkbnjabar.sipenting.data.local.dao.RwDao
import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import com.bkkbnjabar.sipenting.data.model.Kabupaten
import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.data.model.Kelurahan
import com.bkkbnjabar.sipenting.data.model.Provinsi
import com.bkkbnjabar.sipenting.data.model.Rt
import com.bkkbnjabar.sipenting.data.model.Rw
import com.bkkbnjabar.sipenting.data.model.lookup.KecamatanDto
import com.bkkbnjabar.sipenting.data.model.lookup.KabupatenDto
import com.bkkbnjabar.sipenting.data.model.lookup.ProvinsiDto
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanDto
import com.bkkbnjabar.sipenting.data.model.lookup.RwDto
import com.bkkbnjabar.sipenting.data.model.lookup.RtDto
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemDto

import com.bkkbnjabar.sipenting.data.model.lookup.ProvinsiListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KabupatenListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KecamatanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.RwListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.RtListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.SingleKelurahanResponse // Digunakan
import com.bkkbnjabar.sipenting.data.model.lookup.SingleKecamatanResponse // Masih diimpor jika ada endpoint lain yang mengembalikan ini
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemListResponse

import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookupRepositoryImpl @Inject constructor(
    private val api: LookupApiService,
    private val provinsiDao: ProvinsiDao,
    private val kabupatenDao: KabupatenDao,
    private val kecamatanDao: KecamatanDao,
    private val kelurahanDao: KelurahanDao,
    private val rwDao: RwDao,
    private val rtDao: RtDao
) : LookupRepository {

    /**
     * Helper function untuk memanggil API yang mengembalikan LIST data dan memetakan ke domain model,
     * lalu menyimpan ke Room.
     * @param apiCall Fungsi suspend yang memanggil API dan mengembalikan Response<T> (T bisa berupa List<DTO> atau Wrapper DTO).
     * @param dtoExtractor Fungsi untuk mengekstrak List<Any> dari body respons T.
     * @param dtoToDomainMapper Mapper dari Any (tipe item DTO generik) ke domain model R.
     * @param domainToEntityMapper Mapper dari domain model R ke Room Entity E.
     * @param insertToRoom Fungsi suspend untuk menyimpan List<E> ke Room DAO.
     * @return Resource<List<R>> yang berisi daftar domain model atau error.
     */
    private suspend inline fun <T, R, E> safeApiCallAndMapList(
        apiCall: suspend () -> retrofit2.Response<T>,
        crossinline dtoExtractor: (T) -> List<Any>?, // Fungsi untuk mengekstrak List<Any> dari respons
        crossinline dtoToDomainMapper: (Any) -> R, // Mapper dari Any (tipe item DTO generik) ke domain model
        crossinline domainToEntityMapper: (R) -> E,
        crossinline insertToRoom: suspend (List<E>) -> Unit // Menggunakan crossinline untuk fungsi suspend
    ): Resource<List<R>> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val dtoList = dtoExtractor(responseBody)
                    dtoList?.filterIsInstance<Any>()?.map(dtoToDomainMapper)?.let { domainList ->
                        val entityList = domainList.map(domainToEntityMapper)
                        insertToRoom(entityList)
                        Resource.Success(domainList)
                    } ?: Resource.Success(emptyList())
                } ?: Resource.Success(emptyList())
            } else {
                Resource.Error("Gagal mengambil data: ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Kesalahan jaringan: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Kesalahan API: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Kesalahan umum: ${e.message}")
        }
    }

    /**
     * Helper function untuk memanggil API yang mengembalikan SINGLE data dan memetakan ke domain model.
     * @param apiCall Fungsi suspend yang memanggil API dan mengembalikan Response<T> (T bisa berupa DTO atau Wrapper DTO).
     * @param dtoExtractor Fungsi untuk mengekstrak single DTO dari body respons T.
     * @param mapper Mapper dari Any (tipe DTO generik) ke domain model R.
     * @return Resource<R> yang berisi domain model tunggal atau error.
     */
    private suspend inline fun <T, R> safeApiCallAndMapSingle(
        apiCall: suspend () -> retrofit2.Response<T>,
        crossinline dtoExtractor: (T) -> Any?, // Fungsi untuk mengekstrak single DTO dari respons
        crossinline mapper: (Any) -> R // Mapper dari Any (tipe item DTO generik) ke domain model
    ): Resource<R> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val dto = dtoExtractor(responseBody)
                    dto?.let {
                        Resource.Success(mapper(it))
                    } ?: Resource.Error("Data kosong atau tidak valid.")
                } ?: Resource.Error("Data kosong.")
            } else {
                Resource.Error("Gagal mengambil data: ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Kesalahan jaringan: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Kesalahan API: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Kesalahan umum: ${e.message}")
        }
    }


    // --- API Fetch and Save to Room Methods ---

    /**
     * Mengambil daftar Provinsi dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `ProvinsiDto` yang dibungkus dalam `ProvinsiListResponse` (`{"data": [...]}`).
     */
    override suspend fun getProvinsisFromApi(): Resource<List<Provinsi>> {
        return safeApiCallAndMapList(
            apiCall = { api.getProvinsis() },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as ProvinsiDto
                Provinsi(id = dto.id, name = dto.name)
            },
            domainToEntityMapper = { domain ->
                ProvinsiEntity(id = domain.id, name = domain.name)
            },
            insertToRoom = { entities -> provinsiDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil daftar Kabupaten dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `KabupatenDto` yang dibungkus dalam `KabupatenListResponse` (`{"data": [...]}`).
     */
    override suspend fun getKabupatensFromApi(provinsiId: Int?): Resource<List<Kabupaten>> {
        return safeApiCallAndMapList(
            apiCall = { api.getKabupatens(provinsiId) },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as KabupatenDto
                Kabupaten(
                    id = dto.id,
                    name = dto.name,
                    provinsiId = dto.provinsiId,
                    provinsi = dto.provinsi?.let { provDto ->
                        Provinsi(id = provDto.id, name = provDto.name)
                    }
                )
            },
            domainToEntityMapper = { domain ->
                KabupatenEntity(id = domain.id, name = domain.name, provinsiId = domain.provinsiId)
            },
            insertToRoom = { entities -> kabupatenDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil daftar Kecamatan dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `KecamatanDto` yang dibungkus dalam `KecamatanListResponse` (`{"data": [...]}`).
     */
    override suspend fun getKecamatansFromApi(kabupatenId: Int?): Resource<List<Kecamatan>> {
        return safeApiCallAndMapList(
            apiCall = { api.getKecamatans(kabupatenId) }, // Menggunakan parameter kabupatenId
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as KecamatanDto
                Kecamatan(
                    id = dto.id, name = dto.name, // Menggunakan 'name' dari DTO Kecamatan
                    kabupatenId = dto.kabupatenId,
                    kabupaten = dto.kabupaten?.let { kabDto ->
                        Kabupaten(
                            id = kabDto.id, name = kabDto.name,
                            provinsiId = kabDto.provinsiId,
                            provinsi = kabDto.provinsi?.let { provDto ->
                                Provinsi(id = provDto.id, name = provDto.name)
                            }
                        )
                    }
                )
            },
            domainToEntityMapper = { domain ->
                KecamatanEntity(id = domain.id, name = domain.name, kabupatenId = domain.kabupatenId)
            },
            insertToRoom = { entities -> kecamatanDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil daftar Kelurahan dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `KelurahanDto` yang dibungkus dalam `KelurahanListResponse` (`{"data": [...]}`).
     */
    override suspend fun getKelurahansFromApi(kecamatanId: Int?): Resource<List<Kelurahan>> {
        return safeApiCallAndMapList(
            apiCall = { api.getKelurahans(kecamatanId) },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as KelurahanDto
                Kelurahan(
                    id = dto.id, name = dto.name, // Menggunakan 'name' dari DTO Kelurahan
                    kecamatanId = dto.kecamatanId,
                    kecamatan = dto.kecamatan?.let { kecDto ->
                        Kecamatan(
                            id = kecDto.id, name = kecDto.name,  // Menggunakan 'name' Kecamatan
                            kabupatenId = kecDto.kabupatenId,
                            kabupaten = kecDto.kabupaten?.let { kabDto ->
                                Kabupaten(id = kabDto.id, name = kabDto.name,
                                    provinsiId = kabDto.provinsiId,
                                    provinsi = kabDto.provinsi?.let { provDto ->
                                        Provinsi(id = provDto.id, name = provDto.name)
                                    }
                                )
                            }
                        )
                    }
                )
            },
            domainToEntityMapper = { domain ->
                KelurahanEntity(id = domain.id, name = domain.name, kecamatanId = domain.kecamatanId)
            },
            insertToRoom = { entities -> kelurahanDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil daftar RW dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `RwDto` yang dibungkus dalam `RwListResponse` (`{"data": [...]}`).
     */
    override suspend fun getRWSFromApi(kelurahanId: Int?): Resource<List<Rw>> {
        return safeApiCallAndMapList(
            apiCall = { api.getRWS(kelurahanId) },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as RwDto
                Rw(id = dto.id, name = dto.name, kelurahanId = dto.kelurahanId) // Menggunakan 'name' dari DTO Rw
            },
            domainToEntityMapper = { domain ->
                RwEntity(id = domain.id, name = domain.name, kelurahanId = domain.kelurahanId)
            },
            insertToRoom = { entities -> rwDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil daftar RT dari API dan menyimpannya ke database lokal.
     * Menggunakan DTO `RtDto` yang dibungkus dalam `RtListResponse` (`{"data": [...]}`).
     */
    override suspend fun getRTSFromApi(rwId: Int?): Resource<List<Rt>> {
        return safeApiCallAndMapList(
            apiCall = { api.getRTS(rwId) },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data'
            dtoToDomainMapper = { dto ->
                dto as RtDto
                Rt(id = dto.id, name = dto.name, rwId = dto.rwId) // Menggunakan 'name' dari DTO Rt
            },
            domainToEntityMapper = { domain ->
                RtEntity(id = domain.id, name = domain.name, rwId = domain.rwId)
            },
            insertToRoom = { entities -> rtDao.insertAll(entities) }
        )
    }

    /**
     * Mengambil data lokasi pengguna (Kelurahan) dari API.
     * Menggunakan DTO `SingleKelurahanResponse` (`{"data": {}}`) dan memetakan ke `Kelurahan` domain model.
     *
     * PERBAIKAN: Mengembalikan `Resource<Kelurahan>` karena API user/location mengembalikan data Kelurahan.
     */
    override suspend fun getUserLocationDataFromApi(): Resource<Kelurahan> {
        return safeApiCallAndMapSingle(
            apiCall = { api.getUserLocationDataFromApi() }, // Memanggil API yang mengembalikan SingleKelurahanResponse
            dtoExtractor = { response -> response.data }, // Mengekstrak data KelurahanDto
            mapper = { dto ->
                dto as KelurahanDto
                Kelurahan(
                    id = dto.id,
                    name = dto.name,
                    kecamatanId = dto.kecamatanId,
                    kecamatan = dto.kecamatan?.let { kecDto ->
                        Kecamatan(
                            id = kecDto.id,
                            name = kecDto.name,
                            kabupatenId = kecDto.kabupatenId,
                            kabupaten = kecDto.kabupaten?.let { kabDto ->
                                Kabupaten(
                                    id = kabDto.id,
                                    name = kabDto.name,
                                    provinsiId = kabDto.provinsiId,
                                    provinsi = kabDto.provinsi?.let { provDto ->
                                        Provinsi(id = provDto.id, name = provDto.name)
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    // --- Other Lookup Items (Existing) ---
    override suspend fun getBirthAssistants(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getBirthAssistants() },
            dtoExtractor = { response -> response.data }, // Mengambil dari 'data' di LookupItemListResponse
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain ->
                LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) // Dummy untuk kompilasi
            },
            insertToRoom = { entities -> /* Tidak ada Room DAO spesifik untuk ini secara default, kosongkan */ }
        )
    }

    override suspend fun getContraceptionOptions(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getContraceptionOptions() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getCounselingTypes(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getCounselingTypes() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getDefecationFacilities(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getDefecationFacilities() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getDeliveryPlaces(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getDeliveryPlaces() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getDiseaseHistories(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getDiseaseHistories() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getGivenBirthStatuses(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getGivenBirthStatuses() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getImmunizationOptions(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getImmunizationOptions() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getMainSourceOfDrinkingWaters(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getMainSourceOfDrinkingWaters() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getPostpartumComplicationOptions(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getPostpartumComplicationOptions() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getPregnantMotherStatuses(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getPregnantMotherStatuses() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }

    override suspend fun getSocialAssistanceFacilitationOptions(): Resource<List<LookupItemDto>> {
        return safeApiCallAndMapList(
            apiCall = { api.getSocialAssistanceFacilitationOptions() },
            dtoExtractor = { response -> response.data },
            dtoToDomainMapper = { dto ->
                dto as LookupItemDto
                LookupItemDto(id = dto.id, name = dto.name, isActive = dto.isActive, sortOrder = dto.sortOrder)
            },
            domainToEntityMapper = { domain -> LookupItemDto(id = domain.id, name = domain.name, isActive = domain.isActive, sortOrder = domain.sortOrder) },
            insertToRoom = { entities -> }
        )
    }


    // --- Room Data Methods (Implementasi) ---
    override suspend fun saveProvinsisToRoom(provinsis: List<Provinsi>) {
        val entities = provinsis.map { ProvinsiEntity(it.id, it.name) }
        provinsiDao.insertAll(entities)
    }
    override fun getAllProvinsisFromRoom(): Flow<List<Provinsi>> {
        return provinsiDao.getAllProvinsis().map { entities ->
            entities.map { Provinsi(it.id, it.name) }
        }
    }

    override suspend fun saveKabupatensToRoom(kabupatens: List<Kabupaten>) {
        val entities = kabupatens.map { KabupatenEntity(it.id, it.name, it.provinsiId) }
        kabupatenDao.insertAll(entities)
    }
    override fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>> {
        return kabupatenDao.getAllKabupatens().map { entities ->
            entities.map { Kabupaten(it.id, it.name,  it.provinsiId, null) }
        }
    }

    override suspend fun saveKecamatansToRoom(kecamatans: List<Kecamatan>) {
        val entities = kecamatans.map { KecamatanEntity(it.id, it.name, it.kabupatenId) }
        kecamatanDao.insertAll(entities)
    }
    override fun getAllKecamatansFromRoom(): Flow<List<Kecamatan>> {
        return kecamatanDao.getAllKecamatans().map { entities ->
            entities.map { Kecamatan(it.id, it.name,  it.kabupatenId, null) }
        }
    }
    override fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>> {
        return kecamatanDao.getKecamatansByKabupaten(kabupatenId).map { entities ->
            entities.map { Kecamatan(it.id, it.name,  it.kabupatenId, null) }
        }
    }

    override suspend fun saveKelurahansToRoom(kelurahans: List<Kelurahan>) {
        val entities = kelurahans.map { KelurahanEntity(it.id, it.name, it.kecamatanId) }
        kelurahanDao.insertAll(entities)
    }
    override fun getAllKelurahansFromRoom(): Flow<List<Kelurahan>> {
        return kelurahanDao.getAllKelurahans().map { entities ->
            entities.map { Kelurahan(it.id, it.name,  it.kecamatanId, null) }
        }
    }
    override fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>> {
        return kelurahanDao.getKelurahansByKecamatan(kecamatanId).map { entities ->
            entities.map { Kelurahan(it.id, it.name,  it.kecamatanId, null) }
        }
    }

    override suspend fun saveRWSToRoom(rws: List<Rw>) {
        val entities = rws.map { RwEntity(it.id, it.name, it.kelurahanId) }
        rwDao.insertAll(entities)
    }
    override fun getAllRWSFromRoom(): Flow<List<Rw>> {
        return rwDao.getAllRWS().map { entities ->
            entities.map { Rw(it.id, it.name, it.kelurahanId) }
        }
    }
    override fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>> {
        return rwDao.getRWSByKelurahan(kelurahanId).map { entities ->
            entities.map { Rw(it.id, it.name, it.kelurahanId) }
        }
    }

    override suspend fun saveRTSToRoom(rts: List<Rt>) {
        val entities = rts.map { RtEntity(it.id, it.name, it.rwId) }
        rtDao.insertAll(entities)
    }
    override fun getAllRTSFromRoom(): Flow<List<Rt>> {
        return rtDao.getAllRTS().map { entities ->
            entities.map { Rt(it.id, it.name, it.rwId) }
        }
    }
    override fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>> {
        return rtDao.getRTSByRw(rwId).map { entities ->
            entities.map { Rt(it.id, it.name, it.rwId) }
        }
    }

    override suspend fun preloadAllLocationData(): Resource<Unit> {
        return try {
            val provinsisResource = getProvinsisFromApi()
            if (provinsisResource is Resource.Error) {
                return Resource.Error("Gagal memuat Provinsi: ${provinsisResource.message}")
            }
            // Mengambil kabupaten untuk setiap provinsi jika diperlukan, atau ambil semua
            val kabupatenResource = getKabupatensFromApi(null) // Mengambil semua kabupaten
            if (kabupatenResource is Resource.Error) {
                return Resource.Error("Gagal memuat Kabupaten: ${kabupatenResource.message}")
            }
            val kecamatanResource = getKecamatansFromApi(null) // Mengambil semua kecamatan
            if (kecamatanResource is Resource.Error) {
                return Resource.Error("Gagal memuat Kecamatan: ${kecamatanResource.message}")
            }
            val kelurahanResource = getKelurahansFromApi(null) // Mengambil semua kelurahan
            if (kelurahanResource is Resource.Error) {
                return Resource.Error("Gagal memuat Kelurahan: ${kelurahanResource.message}")
            }
            val rwResource = getRWSFromApi(null) // Mengambil semua RW
            if (rwResource is Resource.Error) {
                return Resource.Error("Gagal memuat RW: ${rwResource.message}")
            }
            val rtResource = getRTSFromApi(null) // Mengambil semua RT
            if (rtResource is Resource.Error) {
                return Resource.Error("Gagal memuat RT: ${rtResource.message}")
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Kesalahan umum saat preloading data lokasi: ${e.message}")
        }
    }
}
