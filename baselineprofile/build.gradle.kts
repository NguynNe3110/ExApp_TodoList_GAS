plugins {
  alias(libs.plugins.android.test)
}

android {
  namespace = "com.example.baselineprofile"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    minSdk = 24
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"

  experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
  implementation(libs.androidx.junit)
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.androidx.runner)
}

tasks.register("generateBaselineProfile") {
  group = "verification"
  description = "Alias task for AGP9 compatibility: runs connected macrobenchmark tests."
  dependsOn("connectedAndroidTest")
}
