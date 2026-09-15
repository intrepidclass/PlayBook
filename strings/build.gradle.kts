import com.android.build.api.dsl.LibraryExtension

plugins {
  id("voice.library")
}

extensions.configure<LibraryExtension> {
  androidResources.apply {
    enable = true
  }
}
