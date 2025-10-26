# AGENTS.md

This file provides **context, instructions, and precise commands** for AI agents working with the VGS Collect Android SDK project.

## Project Context (Project Overview)

| Attribute | Value |
| :--- | :--- |
| **Name** | VGS Collect Android SDK |
| **Purpose** | A library for secure collection of sensitive data (e.g., payment card details) without the data passing through the client's systems. |
| **Core Technology** | Android (Kotlin/Java), Gradle |
| **License** | MIT |

## Project Structure

The main modules relevant to an agent are:
* **VGSCollect SDK**: The core API.
* **Card Scanner**: Module for integrating with the `blinkcard-android` SDK.
* **app**: Sample application used for development and testing.

## Development Environment Setup

The necessary prerequisites for working with the project are:
* **Android Studio**
* **JDK 8+**

## Build and Testing Commands

As this is an Android/Gradle project, all commands should be executed via the **Gradle Wrapper** (`./gradlew`) from the root directory.

| Purpose | Command |
| :--- | :--- |
| **Run All Unit Tests** | `./gradlew test` |
| **Full Project Build** | `./gradlew build` |
| **Install Debug App** | `./gradlew :app:installDebug` |

## External Documentation

For detailed information:
* [Overview Documentation](https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/overview)
* [Submitting Data Guide](https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/submit-data)
* [Demo Application](https://github.com/verygoodsecurity/android-sdk-demo)

## Dependency Management

New VGS Collect SDK versions are added to the **`build.gradle`** file in the `dependencies` block:

```gradle
dependencies {
    // Core VGS Collect dependency
    implementation "com.verygoodsecurity:vgscollect:<latest-version>"
    
    // Standard AndroidX dependencies
    implementation "androidx.appcompat:appcompat:<version>"
    implementation "com.google.android.material:material:<version>"
}
```