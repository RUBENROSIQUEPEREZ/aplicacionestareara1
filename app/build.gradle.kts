plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    }

android {
    namespace = "com.example.myapplication1"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.myapplication1"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}
val lifecycle_version = "2.7.0"
val nav_version = "2.7.7" // Siempre comprueba la última versión estable
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    // ViewModel:
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle_version}")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycle_version}")
    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${nav_version}")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Autenticación de Firebase (Necesario para Login y Registro)
    implementation("com.google.firebase:firebase-auth")

    // Cloud Firestore (Necesario para la Persistencia de datos)
    implementation("com.google.firebase:firebase-firestore")

//    Dado que la Tarea 4 implica operaciones asíncronas (como pedir datos a internet o validar un usuario) y se recomienda el uso de suspend functions en los DataSources, es muy probable que necesites las librerías de corrutinas si no se han incluido transitivamente. El documento del Tema 6 recomienda añadirlas explícitamente para la programación multihilo.
//    Puedes añadir esto a tus dependencias para asegurarte:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


}