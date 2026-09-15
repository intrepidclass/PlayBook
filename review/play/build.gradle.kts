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
  implementation(projects.strings)
  implementation(projects.playback)
  implementation(projects.data)
  implementation(projects.common)
  implementation(projects.datastore)
  api(libs.review)
  implementation(libs.lottie)
  implementation(libs.dagger.core)
}
