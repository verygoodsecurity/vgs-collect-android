plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

apply(from = "$rootDir/gradle/utils.gradle.kts")

val localProperty: (String) -> String by extra

android {
    namespace = "com.verygoodsecurity.demoapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.verygoodsecurity.demoapp"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true

        buildConfigField("String", "VAULT_ID", "\"${localProperty("VGS_VAULT_ID")}\"")
        buildConfigField("String", "PATH", "\"${localProperty("VGS_PATH")}\"")
        buildConfigField("String", "TOKENIZATION_ROUTE_ID", "\"${localProperty("VGS_TOKENIZATION_ROUTE_ID")}\"")
        buildConfigField("String", "TOKENIZATION_V2_ROUTE_ID", "\"${localProperty("VGS_TOKENIZATION_V2_ROUTE_ID")}\"")
        buildConfigField("String", "ACCESS_TOKEN_URL", "\"${localProperty("VGS_ACCESS_TOKEN_URL")}\"")
        buildConfigField("String", "CLIENT_ID", "\"${localProperty("VGS_CLIENT_ID")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${localProperty("VGS_CLIENT_SECRET")}\"")
        buildConfigField("String", "GRANT_TYPE", "\"${localProperty("VGS_GRANT_TYPE")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField(
                "com.verygoodsecurity.vgscollect.core.Environment",
                "ENVIRINMENT",
                "com.verygoodsecurity.vgscollect.core.Environment.SANDBOX"
            )
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            buildConfigField(
                "com.verygoodsecurity.vgscollect.core.Environment",
                "ENVIRINMENT",
                "com.verygoodsecurity.vgscollect.core.Environment.LIVE"
            )
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    testOptions {
        animationsDisabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.versionedparcelable:versionedparcelable:1.1.1")
    }
    exclude(group = "com.android.support")
}

dependencies {
    implementation(project(":vgscollect"))
    implementation(project(":vgscollect-blinkcard"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.activity.compose)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.play.services.wallet)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.codeview)
    implementation(libs.fuel)
    implementation(libs.espresso.idling.resource)

    debugImplementation(debugLibs.compose.ui.tooling)
    debugImplementation(debugLibs.leakcanary)

    testImplementation(testLibs.junit)

    androidTestImplementation(androidTestLibs.androidx.core)
    androidTestImplementation(androidTestLibs.androidx.runner)
    androidTestImplementation(androidTestLibs.androidx.rules)
    androidTestImplementation(androidTestLibs.androidx.junit.ext)
    androidTestImplementation(androidTestLibs.androidx.junit.ext.ktx)
    androidTestImplementation(androidTestLibs.androidx.uiautomator)
    androidTestImplementation(androidTestLibs.androidx.espresso.core)
    androidTestImplementation(androidTestLibs.androidx.espresso.intents)
    androidTestImplementation(androidTestLibs.espresso.contrib)
    androidTestImplementation(androidTestLibs.androidx.rules)
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
