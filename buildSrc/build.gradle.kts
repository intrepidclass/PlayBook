plugins {
  `kotlin-dsl`
}

// Add a repository for these dependencies if not already inherited
// For buildSrc, it's good practice to define repositories explicitly.
repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
  implementation("com.android.tools.build:gradle:8.13.0") // androidPluginForGradle
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10") // kotlin.pluginForGradle
  compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.10") // kotlin.compilerEmbeddable
  implementation("io.github.usefulness:ktlint-gradle-plugin:0.10.0") // ktlint.gradlePlugin
  implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.1.10")}

gradlePlugin {
  plugins {
    create("library") {
      id = "voice.library"
      implementationClass = "LibraryPlugin"
    }
    create("app") {
      id = "voice.app"
      implementationClass = "AppPlugin"
    }
    create("compose") {
      id = "voice.compose"
      implementationClass = "ComposePlugin"
    }
    create("ktlint") {
      id = "voice.ktlint"
      implementationClass = "KtlintPlugin"
    }
  }
}

kotlin {
  jvmToolchain {
    // Ensure JavaLanguageVersion is imported or use the fully qualified name
    languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(17))
  }
}
