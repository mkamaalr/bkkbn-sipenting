plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // For Hilt
    id("com.google.dagger.hilt.android") // For Hilt
    id("kotlin-parcelize") // Add this line
}

android {
    namespace = "com.bkkbnjabar.sipenting"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bkkbnjabar.sipenting"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // Tambahkan baris ini untuk secara eksplisit mengatur versi bahasa Kotlin
        languageVersion = "1.9" // Memperbaiki masalah kompatibilitas Kapt
    }
    buildFeatures {
        viewBinding = true // Nonaktifkan View Binding jika hanya menggunakan Data Binding
        dataBinding = true // Aktifkan Data Binding
    }
}

dependencies {
    // Core Android KTX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    // UI
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")

    // Navigation Component (highly recommended for multi-screen apps)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.coordinatorlayout)
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0") // Hilt dengan dukungan ViewModel

    // Networking (Retrofit, OkHttp, Moshi for JSON parsing)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // Or converter-gson
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // For Moshi code generation
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.1") // For Moshi code generation
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room (Persistence Library) - Tambahkan ini
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    // Dukungan coroutine untuk Room
    implementation("androidx.room:room-ktx:2.7.2")

    implementation("com.google.code.gson:gson:2.13.1") // Pastikan versi terbaru jika ada

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Metadata
    // Penting: Pastikan kotlin-stdlib-jdk8 versi terbaru agar kotlinx-metadata-jvm juga terbaru.
    // Versi ini harus sesuai dengan versi Kotlin yang digunakan oleh plugin.
    implementation ("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23") // Perbarui versi ini

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}