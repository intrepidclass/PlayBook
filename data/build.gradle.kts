import com.android.build.api.dsl.LibraryExtension

plugins {
  id("voice.library")
  id("kotlin-parcelize")
  id("kotlin-kapt")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.anvil)
}

anvil {
  generateDaggerFactories.set(false)
  useKsp(
    contributesAndFactoryGeneration = false,
    componentMerging = true,
  )
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
  allWarningsAsErrors = true
}

extensions.configure<LibraryExtension> {

  androidResources {
    enable = true
  }

  defaultConfig.apply {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  sourceSets {
    named("test") {
      assets.directories.add(project.file("schemas").path)
    }
  }
}

dependencies {
  api(projects.common)
  api(projects.documentfile)
  implementation(libs.appCompat)
  implementation(libs.androidxCore)
  implementation(libs.serialization.json)
  implementation(projects.pref)

  api(libs.room.runtime)
  ksp(libs.room.compiler)

  implementation(libs.dagger.core)
  implementation(libs.anvil.annotations)
  implementation(libs.datastore)
  implementation(libs.documentFile)

  kapt(libs.dagger.compiler)

  testImplementation(libs.room.testing)
  testImplementation(libs.androidX.test.core)
  testImplementation(libs.androidX.test.junit)
  testImplementation(libs.androidX.test.runner)
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.koTest.assert)
  testImplementation(libs.coroutines.test)
}
