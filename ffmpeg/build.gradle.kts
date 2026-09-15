plugins {
  id("voice.library")
}

dependencies {
  // Pull directly from the Appodeal repository added in settings.gradle.kts
  api("dev.ffmpegkit-maintained:ffmpeg-kit-full-gpl:8.1.7")

  api("com.arthenica:smart-exception-java:0.2.1")
  implementation(libs.androidxCore)
}
