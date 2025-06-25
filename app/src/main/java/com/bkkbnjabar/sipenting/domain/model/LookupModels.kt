package com.bkkbnjabar.sipenting.domain.model

// Model untuk data lookup lokasi
data class Provinsi(val id: Int, val name: String) {
    override fun toString(): String = name
}
data class Kabupaten(val id: Int, val provinsiId: Int?, val name: String) {
    override fun toString(): String = name
}
data class Kecamatan(val id: Int, val kabupatenId: Int?, val name: String) {
    override fun toString(): String = name
}
data class Kelurahan(val id: Int, val kecamatanId: Int?, val name: String) {
    override fun toString(): String = name
}

data class Rw(val id: Int, val kelurahanId: Int?, val name: String) {
    /**
     * Menimpa toString() agar AutoCompleteTextView menampilkan 'name'
     * bukan representasi objek.
     */
    override fun toString(): String {
        return name
    }
}

data class Rt(val id: Int, val rwId: Int?, val name: String) {
    /**
     * Menimpa toString() agar AutoCompleteTextView menampilkan 'name'
     * bukan representasi objek.
     */
    override fun toString(): String {
        return name
    }
}

data class LookupItem(
    val id: Int?,
    val name: String
) {
    /**
     * Overriding toString() is crucial for UI components like AutoCompleteTextView.
     * The ArrayAdapter uses this method to get the text that should be displayed
     * for each item in the dropdown list.
     */
    override fun toString(): String {
        return name
    }
}