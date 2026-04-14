# vgs-collect-android-private Dependency Map

## Project Structure

Android SDK library (Kotlin/Gradle) published to Maven Central as `com.verygoodsecurity:vgs-collect-android`. Multi-module Gradle project using version catalogs (TOML) for dependency management.

| Module | Role |
|--------|------|
| `vgscollect` | Core SDK library — card data collection, field rendering, Compose UI, network (OkHttp). Published artifact. |
| `vgscollect-cardio` | Card.io scanner adapter (deprecated). Published artifact. |
| `vgscollect-blinkcard` | BlinkCard (Microblink) scanner adapter. Published artifact. |
| `vgs-sdk-analytics:VGSClientSDKAnalytics` | Internal analytics module (local debug, Maven release). |
| `app` | Demo/sample application — not published. |

## Dependency Categories

### Always Low Risk (Auto-merge Candidates)

| Pattern | Example | Reason |
|---------|---------|--------|
| Test-only dependencies (unit) | `junit:junit`, `org.mockito:mockito-core`, `org.mockito:mockito-inline`, `org.robolectric:robolectric`, `org.json:json`, `org.skyscreamer:jsonassert` | `testImplementation` scope only — no runtime impact |
| Android test dependencies | `androidx.test:core`, `androidx.test:runner`, `androidx.test:rules`, `androidx.test.ext:junit`, `androidx.test.uiautomator:uiautomator`, `androidx.test.espresso:*` | `androidTestImplementation` scope only |
| Debug-only dependencies | `com.squareup.leakcanary:leakcanary-android`, `androidx.compose.ui:ui-tooling` | `debugImplementation` — not in release builds |
| Documentation plugins | `org.jetbrains.dokka` (plugin + `dokka-base` lib) | Build-time doc generation only |
| Lint/static analysis | `io.gitlab.arturbosch.detekt` | Build-time analysis only |
| Code coverage | `org.jetbrains.kotlinx.kover` | Build-time coverage only |
| Demo-app-only deps | `com.github.kbiakov:CodeView-Android`, `com.github.kittinunf.fuel:fuel`, `androidx.multidex:multidex`, `androidx.preference:preference-ktx`, `androidx.constraintlayout:constraintlayout` | Used only in the unpublished `app` module |

### Needs Quick Review

| Pattern | Example | Reason | Expected Test Coverage |
|---------|---------|--------|----------------------|
| AndroidX minor bumps | `androidx.appcompat:appcompat`, `androidx.core:core-ktx` | Compile/runtime but highly stable | Unit + instrumentation tests |
| Compose minor bumps | `androidx.compose.material:material`, `androidx.compose.ui:ui-tooling-preview`, `androidx.activity:activity-compose` | UI layer, but SDK Compose support is limited | Demo app manual testing |
| Google Material minor | `com.google.android.material:material` | UI components — `api` scope in core SDK | Unit + instrumentation tests |
| Espresso idling resource | `androidx.test.espresso:espresso-idling-resource` | Runtime dep but only used for test synchronization | Instrumentation tests |
| Gradle plugin minor bumps | `com.android.application`/`com.android.library` (AGP), `com.vanniktech.maven.publish` | Build infra — check release notes for breaking changes | CI build pipeline |
| Mokkery minor | `dev.mokkery` | Test mocking plugin — verify test compilation | Unit tests |

### Needs Deep Review

| Pattern | Example | Reason | Expected Test Coverage |
|---------|---------|--------|----------------------|
| OkHttp | `com.squareup.okhttp3:okhttp` | Core networking — `api` scope, exposed to SDK consumers | Unit tests + integration tests |
| Kotlin | `org.jetbrains.kotlin.android`, `kotlin.plugin.parcelize`, `kotlin.plugin.compose`, `kotlin.plugin.serialization` | Language/compiler — affects all modules | Full test suite |
| VGS SDK Analytics | `com.verygoodsecurity:vgs-sdk-analytics-android` | Internal VGS dependency — opaque transitive changes | Integration tests |
| BlinkCard SDK | `com.microblink:blinkcard` | Native OCR scanner — vendor binary, `api` scope | Manual card scanning tests |
| Card.io SDK | `io.card:android-sdk` | Native card scanner (deprecated module) — `api` scope | Manual card scanning tests |
| Google Play Wallet | `com.google.android.gms:play-services-wallet` | Google Pay integration in demo app | Manual payment flow tests |
| Ktor | `io.ktor:ktor-client-core`, `io.ktor:ktor-client-okhttp`, `io.ktor:ktor-client-darwin` | HTTP client (KMP) — listed in catalog but usage may be in analytics module | Unit tests |
| Gradle major bumps | Gradle wrapper (8.x → 9.x) | Build system — can break plugin compatibility | Full CI pipeline |
| AGP major bumps | `com.android.application`/`com.android.library` major | Android build toolchain — can break compilation, minification, publishing | Full CI pipeline |

## Historical Patterns (from PR analysis)

There are currently 10 open Renovate PRs spanning:
- **Gradle tooling**: Gradle wrapper (8.14.4, 9.x), AGP
- **Kotlin ecosystem**: Kotlin monorepo (2.3.20), Ktor monorepo (3.4.2), Kover (0.9.8), Mokkery (v3 major)
- **Build plugins**: Maven Publish (0.36.0)
- **Test deps**: Mockito (5.23.0)
- **CI**: actions/cache (v5), pin-dependencies

PRs have been open since mid-January 2026, suggesting they are not being actively triaged.

## Renovate Configuration

Minimal configuration — `renovate.json` contains only the schema reference with no extends, labels, grouping, or automerge rules:

```json
{
  "$schema": "https://docs.renovatebot.com/renovate-schema.json"
}
```

This means Renovate is using all default settings (no preset extends, no custom grouping, no automerge).
