pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    versionCatalogs {
        create("debugLibs") {
            from(files("gradle/debug-libs.versions.toml"))
        }
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
        create("androidTestLibs") {
            from(files("gradle/android-test-libs.versions.toml"))
        }
    }
}

rootProject.name = "vgs-collect-android"
include(":app", ":vgscollect", ":vgscollect-blinkcard", ":vgs-sdk-analytics:VGSClientSDKAnalytics")

