plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.9.23"
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 28
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += listOf("META-INF/*")
        }
    }

    namespace = "com.ismartcoding.lib"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

//    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(libs.pdfium.android)

    api(libs.gson)

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.guava)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
    api(libs.material)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.extensions)

    api(libs.androidx.fragment.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.core.ktx)
    api(libs.androidx.transition)
//    api("com.squareup.picasso:picasso:2.71828")

    api(libs.guava)
    api(libs.exoplayer)
    // https://developer.android.com/topic/performance/graphics/load-bitmap
    api(libs.glide)
    ksp(libs.ksp)
    // https://github.com/davemorrissey/subsampling-scale-image-view
    api(libs.subsampling.scale.image.view.androidx)

    implementation(libs.ahocorasick) // For pinyin
    implementation(libs.bcprov.jdk15on)
    implementation(libs.bcpkix.jdk15on)
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.logging)

    api(libs.markwon.core)
    api(libs.html)
    api(libs.ext.strikethrough)
    api(libs.ext.tasklist)
    api(libs.ext.tables)
    api(libs.ext.latex)
    api(libs.linkify)
    api(libs.image.glide)
    //api("com.caverock:androidsvg:1.4")
    api(libs.androidsvg.aar)

    implementation(libs.android.gif.drawable)

    // https://coil-kt.github.io/coil/changelog/
    api(libs.coil)
    api(libs.coil.compose)
    api(libs.coil.svg)
    api(libs.coil.gif)
    api(libs.coil.video)

    api(libs.jsoup)

    api(libs.refresh.layout.kernel)
}
