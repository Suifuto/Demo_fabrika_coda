pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
//        maven { url = uri( "https://artifacts.consensys.net/public/maven/maven/") }
//        maven { url = uri( "https://dl.cloudsmith.io/public/libp2p/jvm-libp2p/maven/") }
//        maven { url = uri( "https://dl.cloudsmith.io/public/consensys/maven/maven/") }
    }

    components {
        withModule("tech.pegasys:noise-java") {
            // https://artifacts.consensys.net/public/maven/maven/tech/pegasys/noise-java/22.1.0/noise-java-22.1.0.pom
//            maven { url = uri( "https://artifacts.consensys.net/public/maven/maven/") }
        }
    }
}

rootProject.name = "Demo_fabrika_coda"
include(":app")
include(":libs:nabu")
