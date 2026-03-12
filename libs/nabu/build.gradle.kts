plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

group = "com.github.peergos"
version = "v0.9.0-suifuto"
description = "nabu"

//repositories {
//    mavenCentral()
//    maven { url = uri("https://jitpack.io") }
//}

kotlin {
    jvmToolchain(17)
}

configurations.all {
    exclude(group = "tech.pegasys", module = "noise-java")
}

dependencies {
    implementation("com.github.peergos:jvm-libp2p:0.20.0")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("org.apache.commons:commons-collections4:4.5.0")
    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_common:0.16.0")
    implementation("io.prometheus:simpleclient_httpserver:0.16.0")
    implementation("redis.clients:jedis:7.3.0")
    implementation("com.github.multiformats:java-multiaddr:v1.4.13")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("io.netty:netty-all:4.2.10.Final")
    implementation(libs.noise.java)
    implementation("dnsjava:dnsjava:3.6.4")
    implementation("com.offbynull.portmapper:portmapper:2.0.6") {
        exclude(group = "tech.pegasys", module = "noise-java")
    }


}