plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.demofabrikacoda"
    compileSdk {
        version =
            release(36) {
                minorApiLevel = 1
            }
    }

    defaultConfig {
        applicationId = "com.example.demofabrikacoda"
        minSdk = 31
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // kotlinOptions - теперь сам подтягивает jvm
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes +=
                listOf(
                    "META-INF/*.SF",
                    "META-INF/*.DSA",
                    "META-INF/*.RSA",
                    "META-INF/INDEX.LIST",
                    "META-INF/license/*",
                )
            pickFirsts +=
                listOf(
                    "META-INF/io.netty.versions.properties",
                    "META-INF/native-image/io.netty.incubator/netty-incubator-codec-native-quic/*",
                    "META-INF/native-image/io.netty/netty-codec-native-quic/*",
                )
        }
    }
}

configurations.all {
//    resolutionStrategy {
//        force("tech.pegasys:noise-java:22.1.0")
//    }
    exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    exclude(group = "org.bouncycastle", module = "bcpkix-jdk18on")
//    exclude(group = "io.netty", module = "netty-*")
    exclude(group = "tech.pegasys", module = "noise-java")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.graphics)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ipfs
    implementation(libs.nabu) {
        exclude(group = "tech.pegasys", module = "noise-java")
    }
    implementation(libs.noise.java)
//    implementation("io.netty:netty-all:4.2.10.Final")
//    // io.ipfs
//    implementation("com.github.peergos:jvm-libp2p:0.20.0")
//    // io.libp2p
//    implementation("com.github.multiformats:java-multiaddr:v1.4.13")

    // Поддержка api < 33
//    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)
}
