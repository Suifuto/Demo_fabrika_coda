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
        minSdk = 27
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
        isCoreLibraryDesugaringEnabled = true
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
//                    "META-INF/license/LICENSE.jbzip2.txt",
//                    "META-INF/license/LICENSE.webbit.txt",
//                    "META-INF/license/LICENSE.snappy.txt",
                )
            pickFirsts += "/META-INF/io.netty.versions.properties"
        }
    }
}

configurations.all {
//    resolutionStrategy {
//        force("tech.pegasys:noise-java:22.1.0")
//    }
    exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    exclude(group = "org.bouncycastle", module = "bcpkix-jdk15on")
//    exclude(group = "io.netty", module = "netty-*")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
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
//    implementation("io.netty:netty-all:4.2.10.Final")
//    implementation("tech.pegasys:noise-java:22.1.0")
    implementation("com.github.ConsenSys:noise-java:22.1.0")

//    constraints {
//        implementation("tech.pegasys:noise-java") {
//            version {
//                strictly("22.1.1")
//            }
//        }
//    }

    // Поддержка api < 33
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)
}
