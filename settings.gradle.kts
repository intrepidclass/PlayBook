@file:Suppress("UnstableApiUsage")

rootProject.name = "voice"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") } // Corrected uri() usage
    // includeBuild("plugins") // Removed this line
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven") }
    mavenCentral()
    maven { url = uri("https://jitpack.io") } // Added for ffmpeg-kit

    exclusiveContent {
      forRepository {
        maven(url = "https://jitpack.io")
      }
      filter {
        includeGroupByRegex("com.github.PaulWoitaschek.*")
      }
    }
  }
}

plugins {
  id("com.gradle.develocity") version "4.2"
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.9.0")
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

include(":app")
include(":strings")
include(":common")
include(":bookmark")
include(":data")
include(":playback")
include(":ffmpeg")
include(":scanner")
include(":playbackScreen")
include(":sleepTimer")
include(":settings")
include(":search")
include(":cover")
include(":datastore")
include(":folderPicker")
include(":bookOverview")
include(":migration")
include(":scripts")
include(":pref")
include(":logging:core")
include(":logging:debug")
include(":documentfile")
include(":onboarding")
include(":review:play")
include(":review:noop")
