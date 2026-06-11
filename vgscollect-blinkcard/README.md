# VGS Collect BlinkCard Adapter

`vgscollect-blinkcard` integrates card scanning with [VGS Collect Android](https://github.com/verygoodsecurity/vgs-collect-android) using BlinkCard technology from Microblink.

**BlinkCard is a paid product.** Contact Microblink for licensing: <https://microblink.com/contact-us/>.

## Current version

This adapter wraps **BlinkCard v3000 SDK** and provides a simplified consumer API via `VGSBlinkCardIntentBuilder`.

- **Adapter version**: `1.0.6+` (contains v3000 upgrade)
- **Previous adapter**: `1.0.5` and earlier (uses BlinkCard v2)
- Adapter min SDK: `24`
- Internal BlinkCard version: `3000.0.1`
- License model: Per-session (passed to builder)

**Upgrading from adapter v1.0.5 or earlier?** See [MIGRATION_V2_TO_V3000.md](./MIGRATION_V2_TO_V3000.md).

## Dependencies

Add to your app module `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.verygoodsecurity:vgscollect:<vgscollect-version>")
    implementation("com.verygoodsecurity.api:adapter-blinkcard:1.0.6")  // v3000 support; v1.0.5 or earlier uses v2
}

repositories {
    google()
    mavenCentral()  // Required for adapter and BlinkCard dependencies
}
```

## Setup

### 1. Ensure VGS Collect is configured

Your app must already configure VGS Collect with vault ID and environment. Refer to [VGS Collect Android docs](https://github.com/verygoodsecurity/vgs-collect-android).

### 2. Create BlinkCard scan settings

Create `BlinkCardScanActivitySettings` with your Microblink license key:

```kotlin
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings

val scanSettings = BlinkCardScanActivitySettings(
    BlinkCardSdkSettings("<YOUR_BLINKCARD_LICENSE_KEY>")
)
```

### 3. Build intent and map VGS field names

Use the adapter builder to create a scan intent:

```kotlin
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder

val intent = VGSBlinkCardIntentBuilder(this, scanSettings)
    .setCardNumberFieldName(vgsTiedCardNumber.fieldName)
    .setCardHolderFieldName(vgsTiedCardHolder.fieldName)
    .setExpirationDateFieldName(vgsTiedExpiry.fieldName)
    .setCVCFieldName(vgsTiedCvc.fieldName)
    .build()
```

### 4. Launch scanner

```kotlin
startActivityForResult(intent, SCAN_REQUEST_CODE)
```

### 5. Forward result to VGS Collect

In your activity's `onActivityResult`:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SCAN_REQUEST_CODE) {
        vgsCollect.onActivityResult(requestCode, resultCode, data)
    }
}
```

## Error handling

The adapter handles scan completion and errors internally. Common scenarios:

- **Successful scan**: Card data is populated into your VGS fields via `onActivityResult`.
- **User cancelled**: Activity result is returned with `RESULT_CANCELED`.
- **License error**: Adapter logs a user-friendly error message ("`BlinkCard SDK failed to initialise...`").

If you need to detect license issues before launching scan, pre-validate your license key with Microblink.

## What's abstracted

You interact with the adapter via `VGSBlinkCardIntentBuilder` only. Internal details handled by the adapter:

- BlinkCard v3000 SDK initialization and lifecycle
- Scanner UI and result parsing
- Error categorization and user messaging
- Field-to-VGS mapping and data forwarding

You do **not** interact with raw BlinkCard APIs, activity contracts, or result objects directly.

## Security

- All sensitive data is processed through VGS Collect's secure tokenization pipeline.
- Adapter does not persist or cache sensitive values.
