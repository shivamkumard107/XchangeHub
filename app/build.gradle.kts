plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
}

android {
    namespace = "com.dev.sk.xchangehub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dev.sk.xchangehub"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_KEY", "\"c3bd89e29bd0480284e7d7fff10e9258\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", "\"9a3a50279fca47939c23f604c68ebbe2\"")
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
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.kotlinx.serializer)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    // glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // testing
    testImplementation(libs.android.test.core)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.google.truth.test)
    testImplementation(libs.okhttp.mock.web.server)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}

kapt {
    correctErrorTypes = true
}