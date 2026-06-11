plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.verygoodsecurity.vgscollect"
    compileSdk = 37

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "VERSION_NAME", "\"${project.properties["VERSION_NAME"]}\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { testTask ->
                testTask.jvmArgs("-noverify")
            }
        }
    }
}

dependencies {

    api(libs.material)
    api(libs.okhttp)

    debugImplementation(project(":vgs-sdk-analytics:VGSClientSDKAnalytics"))
    releaseImplementation(libs.vgs.sdk.analytics.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    compileOnly(libs.compose.material)
    compileOnly(libs.compose.material3)

    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockito.core)
    testImplementation(testLibs.mockito.inline)
    testImplementation(testLibs.robolectric)
    testImplementation(testLibs.json)
    testImplementation(testLibs.jsonassert)
    testImplementation(libs.compose.material)

    androidTestImplementation(androidTestLibs.androidx.runner)
    androidTestImplementation(androidTestLibs.androidx.junit.ext)
    androidTestImplementation(androidTestLibs.androidx.espresso.core)
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

detekt {
    buildUponDefaultConfig = false
    allRules = false
    config.setFrom(files("$rootDir/.detekt/config.yml"))
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootProject.file("docs"))
    }
}

tasks.withType<Javadoc> {
    enabled = false
}
