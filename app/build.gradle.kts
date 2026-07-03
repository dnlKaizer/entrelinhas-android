import java.util.Properties

// Carrega o secrets.properties
val secretsProperties = Properties().apply {
    val secretsPropertiesFile = rootProject.file("secrets.properties")
    if (secretsPropertiesFile.exists()) {
        secretsPropertiesFile.inputStream().use { load(it) }
    }
}

// Helper para ler do secrets.properties ou de variáveis de ambiente do sistema (útil para CI/CD)
fun getSecret(key: String): String {
    return secretsProperties.getProperty(key) ?: System.getenv(key) ?: ""
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.br.entrelinhas"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.br.entrelinhas"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Adicione as chaves como campos de BuildConfig
        buildConfigField("String", "SUPABASE_URL", "\"${getSecret("PUBLIC_SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${getSecret("PUBLIC_SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "ADMIN_KEY", "\"${getSecret("PUBLIC_ADMIN_KEY")}\"")
        buildConfigField("String", "ADMIN_EMAIL", "\"${getSecret("PUBLIC_ADMIN_EMAIL")}\"")
        buildConfigField("String", "ADMIN_PASSWORD", "\"${getSecret("PUBLIC_ADMIN_PASSWORD")}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Coil (image loading)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Supabase Kotlin SDK
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.auth.kt)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}