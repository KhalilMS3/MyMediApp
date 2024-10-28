plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mymediapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mymediapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation ("androidx.compose.material:material-icons-extended:1.5.1")
    implementation ("androidx.compose.material:material:1.5.1")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) // Firebase BOM
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication

    // Retrofit and Gson converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // For LiveData and ViewModel
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.firestore)

    // Google Maps and Location Services
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.11.2")
    implementation("com.google.android.libraries.places:places:3.2.0")


    /*// For network requests (optional, if fetching data)
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.squareup.okio:okio:3.5.0")*/

    /*// JSON parsing (optional, if fetching data)
    implementation ("com.squareup.moshi:moshi:1.14.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.14.0")*/


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}