import com.android.build.api.dsl.LibraryExtension

plugins {
  id("voice.library")
  id("voice.compose")
  alias(libs.plugins.anvil)
}

anvil {
  generateDaggerFactories.set(true)
}

extensions.configure<LibraryExtension> {
  androidResources.apply {
  }
}

dependencies {
  implementation(projects.common)
  implementation(projects.strings)
  implementation(projects.data)
  implementation(projects.pref)
  implementation(projects.datastore)

  implementation(libs.androidxCore)
  implementation(libs.material)
  implementation(libs.dagger.core)
  implementation(libs.anvil.annotations)
}
