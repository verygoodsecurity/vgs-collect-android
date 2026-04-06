# AGENTS.md

This file is a repository-specific addendum for AI coding agents working in this monorepo.
Read `/AGENTS.md` in full first and treat it as the baseline policy for VGS Collect integration behavior.

## Scope
- Use `/AGENTS.md` for SDK integration rules and security policy.
- Use this file for repository architecture, build/test workflow, and module-level conventions.
- If guidance conflicts, follow `/AGENTS.md` for SDK behavior and this file for repo mechanics.
- When changing SDK integration behavior, public API surface, or integration examples, update `/AGENTS.md` in the same change so downstream integration agents stay aligned.
- Use SKILLS/specialized agents when present and relevant; fall back to direct implementation only when no applicable SKILL is available.

## 1. Core Concepts (Mental Model)
- This repository is a multi-module Android/Kotlin project with a sample app and adapter modules.
- Main shipping Android SDK module: `vgscollect/`.
- Scanner adapters: `vgscollect-blinkcard/` (active) and `vgscollect-cardio/` (deprecated warning logged at configuration time).
- Sample host app for manual/instrumentation validation: `app/`.
- Shared analytics implementation lives in `vgs-sdk-analytics/VGSClientSDKAnalytics` (KMP), consumed differently by build type in `vgscollect/build.gradle`.

### Project Mental Model
- **Primary flow**: app UI input (`app/src/main/`) -> secure field/widget logic (`vgscollect/src/main/`) -> state/validation (`FieldState`, rules) -> submit/tokenize -> app receives aliases/tokens only (per `/AGENTS.md`).
- **Change surface map**:
  - Widget/input behavior, validation, submission internals: `vgscollect/src/main/`.
  - Blink scanning bridge and scan mapping: `vgscollect-blinkcard/src/main/`.
  - Legacy scanner compatibility only: `vgscollect-cardio/src/main/`.
  - Manual verification/demo flows: `app/src/main/`.
- **Testing map**:
  - Fast regression tests for SDK behavior: `vgscollect/src/test/` (Robolectric/JUnit stack from `gradle/test-libs.versions.toml`).
  - Device/instrumentation checks for sample integrations: `app/src/androidTest/`.
- **Release-facing boundaries**:
  - Published Android artifact work centers on `vgscollect/`.
  - Shared analytics implementation is versioned in `vgs-sdk-analytics/VGSClientSDKAnalytics` and linked into `vgscollect` by build type.

## 1A. Environment Preconditions
- Java toolchain is pinned to 17 for Android modules (`app/`, `vgscollect/`, `vgscollect-blinkcard/`, `vgscollect-cardio/`).
- Android SDK levels are currently `compileSdkVersion 36`, `targetSdkVersion 36`, `minSdkVersion 21` in Android modules.
- Demo app runtime config is injected from `local.properties` via `app/build.gradle` (`VGS_VAULT_ID`, `VGS_PATH`, and tokenization/auth keys).
- Build type environment constant in demo app is currently named `ENVIRINMENT` (keep spelling when editing to avoid breaking references).

## 2. Public Building Blocks (Summary Table)
- Module boundaries are defined in `settings.gradle`:
  - `:app`
  - `:vgscollect`
  - `:vgscollect-cardio`
  - `:vgscollect-blinkcard`
  - `:vgs-sdk-analytics:VGSClientSDKAnalytics`
- Dependency versions are centralized in `gradle/libs.versions.toml`; prefer version-catalog updates over inline versions.
- Test/debug-only catalogs are split into `gradle/debug-libs.versions.toml`, `gradle/test-libs.versions.toml`, `gradle/android-test-libs.versions.toml`.

### Core Project Architecture and Classes
- **SDK orchestration/API (`vgscollect/src/main/java/com/verygoodsecurity/vgscollect/core/`)**:
  - `VGSCollect` (main entry point), `Environment`, `HTTPMethod`, `VgsCollectResponseListener`.
  - Network result models: `VGSResponse`, `VGSError` in `core/model/network/`.
- **Secure field widgets (`vgscollect/src/main/java/com/verygoodsecurity/vgscollect/widget/`)**:
  - `VGSEditText`, `VGSCardNumberEditText`, `ExpirationDateEditText`, `CardVerificationCodeEditText`, `PersonNameEditText`, `SSNEditText`, `RangeDateEditText`, `VGSTextInputLayout`.
  - Most custom field behavior ultimately routes through `InputFieldView` (`vgscollect/src/main/java/com/verygoodsecurity/vgscollect/view/InputFieldView.kt`).
- **State + validation (`vgscollect/src/main/java/com/verygoodsecurity/vgscollect/`)**:
  - Runtime state models are in `core/model/state/` (use these for submission gates and UI state decisions).
  - Validation/rules live under `view/card/validation/` (`PaymentCardNumberRule`, `ABARoutingNumberRule`, `VGSInfoRule`, validators).
- **Scanner adapters**:
  - BlinkCard: `vgscollect-blinkcard/src/main/java/com/verygoodsecurity/api/blinkcard/` (`VGSBlinkCardIntentBuilder`, `ScanActivity`, `ScannerCompatibilityUtils`).
  - Card.io legacy: `vgscollect-cardio/src/main/java/com/verygoodsecurity/api/cardio/` (`ScanActivity`).
- **Sample app flows (`app/src/main/java/com/verygoodsecurity/demoapp/`)**:
  - Start/navigation: `start/StartActivity.kt`.
  - Collection demos: `collect/views/CollectViewsActivity.kt`, `collect/compose/CollectComposeActivity.kt`.
  - Tokenization demos: `tokenization/v1/TokenizationActivity.kt`, `tokenization/v2/TokenizationActivity.kt`.

## 4. Field Setup & Configuration Pattern
- For real integration behavior and secure field usage patterns, use `/AGENTS.md` section 4 as source of truth.
- In this repo, the sample wiring patterns are primarily under `app/src/main/` and should be used as runnable examples when validating behavior changes.

## 6. Submission APIs
- Keep submission API usage aligned with `/AGENTS.md`.
- For SDK changes, validate with unit tests in `vgscollect/src/test/` before checking sample app behavior.

## 8. Card Scanning (BlinkCard)
- Prefer BlinkCard adapter (`vgscollect-blinkcard/`) for scanner work.
- Card.io adapter (`vgscollect-cardio/`) is explicitly marked deprecated in build logs (`vgscollect-cardio/build.gradle`) and should not be used for new work.

## 9. Analytics & Privacy
- `vgscollect` uses local analytics module in debug and published artifact in release:
  - `debugImplementation project(':vgs-sdk-analytics:VGSClientSDKAnalytics')`
  - `releaseImplementation(libs.vgs.sdk.analytics.android)`
- Preserve this split unless intentionally changing analytics integration strategy.

## 10. Logging & Redaction Policy
- Follow `/AGENTS.md` redaction rules.
- CI runs include static analysis and test publishing; do not add logs that expose raw field values because reports/artifacts are uploaded (`.github/workflows/ci.yaml`).

## 11. Final Rule for Agents
- Prefer the narrowest module change that satisfies the task (e.g., `vgscollect/` only, avoid sample app edits unless needed for validation/demo).
- Keep scanner-related changes isolated to `vgscollect-blinkcard/` unless maintaining legacy compatibility.
- If updating dependency or SDK behavior, run the same core checks used in CI:
  - `./gradlew vgscollect:detekt --continue`
  - `./gradlew vgscollect:testDebugUnitTest --continue`
- For UI-test-related changes (release branches), CI assembles and runs instrumentation via Firebase Test Lab using:
  - `./gradlew app:assembleDebug app:assembleAndroidTest`

End of .github/AGENTS.md

