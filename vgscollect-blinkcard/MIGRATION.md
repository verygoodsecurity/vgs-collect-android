# Migration Guide: BlinkCard v2 → v3000 Adapter

**Adapter versions:**
- Adapter `1.0.5` and earlier: Uses BlinkCard v2
- Adapter `1.0.6` and later: Uses BlinkCard v3000 (this migration guide)

This guide helps you upgrade apps using the older VGS BlinkCard adapter (v1.0.5-based) to the current v3000-based adapter (v1.0.6+).

**Note:** You interact with the adapter via `VGSBlinkCardIntentBuilder`, not raw BlinkCard APIs. Internal SDK changes are abstracted. This guide shows consumer-facing API changes only.

## Overview of changes

| Consumer-facing changes | Adapter 1.0.5 | Adapter 1.0.6+ |
|---|---|---|
| BlinkCard version | 2.7.0 | 3000.0.1 |
| Min SDK requirement | 21 | 24 |
| License setup | `MicroblinkSDK.setLicenseKey()` globally in `Application.onCreate` | Pass `BlinkCardSdkSettings("<KEY>")` per scan session |
| Intent builder instantiation | `VGSBlinkCardIntentBuilder(this)` | `VGSBlinkCardIntentBuilder(this, settings)` **settings required** |
| Builder methods | `.setCardNumberFieldName(...)` etc. | Same, but settings now mandatory in constructor |
| Result handling | Forward to `vgsCollect.onActivityResult(...)` | Same |

## Migration steps

### 1. Update app module min SDK

**Before:**
```kotlin
android {
    defaultConfig {
        minSdk = 21
    }
}
```

**After:**
```kotlin
android {
    defaultConfig {
        minSdk = 24  // v3000 adapter requires min SDK 24
    }
}
```

### 2. Update Gradle dependency

**Before (adapter 1.0.5):**
```kotlin
dependencies {
    implementation("com.verygoodsecurity.api:adapter-blinkcard:1.0.5")
}
```

**After (adapter 1.0.6+):**
```kotlin
dependencies {
    implementation("com.verygoodsecurity.api:adapter-blinkcard:1.0.6")  // or later
}
```

### 3. Remove global license setup

The adapter **no longer requires** global license configuration in `Application.onCreate`.

**Before:**
```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MicroblinkSDK.setLicenseKey("<LICENSE_KEY>", this)
    }
}
```

**After:**
Remove the above code entirely. License is now passed per scan session.

### 4. Update your scan launch code (critical)

The builder now **requires** `BlinkCardScanActivitySettings` in the constructor.

**Before:**
```kotlin
val intent = VGSBlinkCardIntentBuilder(this)
    .setCardNumberFieldName(fieldName)
    .setCardHolderFieldName(fieldName)
    .setExpirationDateFieldName(fieldName)
    .setCVCFieldName(fieldName)
    .build()

startActivityForResult(intent, SCAN_REQUEST_CODE)
```

**After:**
```kotlin
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder

// Create settings with your license key
val scanSettings = BlinkCardScanActivitySettings(
    BlinkCardSdkSettings("<LICENSE_KEY>")
)

// Pass settings to builder constructor (new requirement)
val intent = VGSBlinkCardIntentBuilder(this, scanSettings)
    .setCardNumberFieldName(fieldName)
    .setCardHolderFieldName(fieldName)
    .setExpirationDateFieldName(fieldName)
    .setCVCFieldName(fieldName)
    .build()

startActivityForResult(intent, SCAN_REQUEST_CODE)
```

### 5. Result handling (no change needed)

Forward activity result to VGS Collect exactly as before:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    vgsCollect.onActivityResult(requestCode, resultCode, data)
}
```

## Key behavior differences

### License validation timing
- **v2 adapter**: License key checked globally at app startup via `MicroblinkSDK.setLicenseKey()`.
- **v3000 adapter**: License validation deferred until scan is launched. If invalid, scan UI reports `ErrorSdkInit` status after user taps scan button.

**For consumers**: If you need early license validation, store the key and pass it to settings before launch. If scan fails with `ErrorSdkInit`, contact Microblink to verify license status.

### Error reporting
- **v2 adapter**: Detailed error messages available through result object.
- **v3000 adapter**: Limited error detail (status enum only). Adapter logs friendly error messages to user.

**For consumers**: Error details are logged in adapter; user sees a UI message. No consumer code changes needed for error handling.

### Builder API
- **v2 adapter**: `VGSBlinkCardIntentBuilder(activity)` — settings optional.
- **v3000 adapter**: `VGSBlinkCardIntentBuilder(activity, settings)` — settings **mandatory**.

**For consumers**: You must now create and pass `BlinkCardScanActivitySettings` at builder instantiation time. This is the **only breaking change**.

## Removed features

The following v2 adapter features are **not** available in the v3000 adapter:

- **Global SDK setup**: No `Application.onCreate` configuration needed. License is per-session.
- **Direct activity access**: You no longer interact with `VGSBlinkCardRecognitionActivity` directly. All launch is through `VGSBlinkCardIntentBuilder` and managed by the adapter.
- **Legacy configuration builder**: `VGSBlinkCardRecognitionConfiguration` no longer exists. Use `BlinkCardScanActivitySettings` instead (passed to builder).

**For consumers**: These changes are internal to the adapter. Your consumer code only changes in step 4 of the migration steps (builder instantiation).

## Testing

After migrating:

1. **Test valid scan**: Verify card data populates VGS fields correctly.
2. **Test invalid license**: Confirm `onActivityResult` is called with error status (adapter logs it; end user sees error message).
3. **Test cancel**: Confirm user can cancel scan and return to app.
4. **Test submission**: Verify VGS Collect can tokenize/submit scanned data.

## Troubleshooting

### Build error: "Cannot resolve symbol 'VGSBlinkCardIntentBuilder'"

- Confirm you updated the adapter dependency version.
- Run `gradle clean` and sync.
- Check import: `import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder`.

### Compilation error: "VGSBlinkCardIntentBuilder requires 2 arguments"

- You are calling the old v2 constructor `VGSBlinkCardIntentBuilder(this)`.
- Update to v3000: `VGSBlinkCardIntentBuilder(this, scanSettings)` with settings created as shown in step 4.

### "BlinkCard SDK failed to initialise. Verify that the license key in BlinkCardSdkSettings is valid and not expired."

- License key in `BlinkCardSdkSettings` is invalid, expired, or mismatched to your Microblink account.
- Contact Microblink to verify key status.
- Confirm min SDK is 24+ in your app.

### Fields not populated after scan

- Confirm result is forwarded to `vgsCollect.onActivityResult(...)`.
- Confirm field names passed to builder match your VGS Collect field names exactly.
- Check VGS Collect setup and field binding in your activity/fragment.

### Runtime crash on launch

- Confirm `BlinkCardScanActivitySettings` is created before passing to builder.
- Confirm license key in settings is a valid string (not null or empty).
- Check logcat for full stack trace.

## Summary

**Adapter version boundary:** `1.0.5` (v2) → `1.0.6+` (v3000)

**Breaking change for consumers**: `VGSBlinkCardIntentBuilder` now requires `BlinkCardScanActivitySettings` in the constructor.

```kotlin
// v1.0.5 way (no longer works)
VGSBlinkCardIntentBuilder(this).setCardNumberFieldName(...).build()

// v1.0.6+ way (required)
val settings = BlinkCardScanActivitySettings(BlinkCardSdkSettings("<KEY>"))
VGSBlinkCardIntentBuilder(this, settings).setCardNumberFieldName(...).build()
```

Everything else in the adapter consumer API remains the same or is abstracted. The adapter handles all internal migration to v3000 BlinkCard APIs for you.

## Support

For issues with the VGS adapter itself, contact VGS support.
For issues with underlying BlinkCard v3000 SDK, refer to: <https://github.com/blinkcard/blinkcard-android>.

