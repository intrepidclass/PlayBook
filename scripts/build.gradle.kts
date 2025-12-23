import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("voice.ktlint")
  application
}

application {
  mainClass.set("voice.scripts.ScriptKt")
}

dependencies {
  implementation(libs.clikt)
}

// Use explicit configuration to avoid IDE accessor issues
configure<KotlinJvmProjectExtension> {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}
