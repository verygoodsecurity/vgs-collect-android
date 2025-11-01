# AGENTS.md

This guide is tailored for autonomous engineering agents integrating `VGSCollectSDK` into Android applications. It focuses on deterministic, automatable steps: environment assessment, dependency installation, feature wiring (text fields, scanning, file upload), privacy compliance, testing, maintenance, and safe upgrade workflows.

## Scope
- Use this file as the ONLY high-level instruction source when adding, updating, or testing VGS Collect Android SDK (`vgscollect`) usage in a downstream app.
- Contain ONLY public, non-deprecated APIs.
- Emphasis: security, correctness, determinism, reproducibility.

## Success Criteria for Any Agent Task
1. Sensitive data (PAN, CVC, SSN, files) NEVER logged, persisted, or leaked to analytics.
2. All fields validated (`state.isValid`) before submission.
3. Network submission surfaces aliases/tokens only to the app layer.
4. File uploads always followed by `collector.cleanFiles()` after success.
5. APIs used exist in current public surface & are not deprecated.
6. Version pins updated intentionally with a migration note & tests green.

---
## 1. Core Concepts (Mental Model)
- `VGSCollect` orchestrates secure collection & submission to a VGS Vault you own (identified by vault ID + environment / data region).
- UI inputs (`VGSTextInputEditText`) never expose raw values outside controlled SDK memory; you interact through configuration + state snapshots.
- Field `state` drives UI (validity, emptiness, metadata like last4, brand) – do not persist raw input.
- Submissions can be performed via listener, or a direct `async` request.

---
## 1A. Environment Preconditions
Purpose: ensure correct vault/environment pairing before any field setup or submission.

Required:
- Non-empty `vaultId` string (assert with `precondition(!vaultId.isEmpty)` in debug builds).
- Explicit environment selection: `Environment.SANDBOX` for development/testing; `Environment.LIVE` for production only.
- Keep sandbox and live vault IDs distinct; never point `.LIVE` at a sandbox vault ID.
- Optional hostname override (if provided): verify matches VGS dashboard configuration before deploy.

Quick Examples:
```kotlin
// Sandbox collector (dev/test)
val collector = VGSCollect(context, sandboxVaultId, Environment.SANDBOX)
// Production collector (release build)
val liveCollector = VGSCollect(context, liveVaultId, Environment.LIVE)
```
Failure Modes:
- Empty vaultId → initialization should fail fast (use preconditions).
- Using `.LIVE` in debug with test cards unintentionally → enforce build configuration checks.
- Mixing aliases from different environments → keep storage / processing segregated.

Security Note: Never derive environment from user input; it must be a static config determined at build or app launch.

---
## 2. Public Building Blocks (Summary Table)
(Do NOT assume additional behavior outside listed intents.)
- `VGSCollect`: register fields, submit data/files, observe states, manage headers, cleanup files.
- `VGSTextInputEditText`: generic configurable secure text field.
- `VGSCardNumberEditText`: adds automatic card brand detection & icon handling.
- `FieldType`: semantic `type`, formatting & validation rules.
- `FieldState` (+ specialized states e.g. `CardState`): read-only snapshot for UI logic.
- `VGSFilePickerConfig` + `VGSFilePickerController`: secure file selection pipeline.
- `VGSBlinkCardRecognitionActivity`: card scanning (BlinkCard module) – preferred scanning path.
- Validation Rule Types (examples): `VGSValidationRuleLength`, `VGSValidationRulePattern`, (others similarly shaped).
- `VGSAnalyticsClient`: toggle analytics collection.
- `VGSCollectLogger`: adjust log verbosity (ensure `.NONE` in production if required).

---
## 2A. FieldType Reference & Capabilities
Purpose: choose correct `FieldType` to obtain built-in formatting & validation. Each case sets defaults (pattern, divider, keyboardType, validation rule set, optional metadata & sensitivity).

Notation: P=Formatting Pattern, D=Divider, Val=Default Validation Rules (names), Meta=Metadata via state, Scan=Supported by BlinkCard scan mapping, Icon=Built-in Icon Support, Sens.=Sensitive (tokenized priority).

### FieldType.INFO
- Use: generic text (no formatting/validation by default)
- P: "" D: "" Val: (none) Meta: basic flags only Scan: No Icon: No Sens.: No

### FieldType.CARD_NUMBER
- Use: PAN input with brand detection & Luhn
- P: "#### #### #### ####" (expands brand-specifically internally) D: space
- Val: `VGSValidationRulePaymentCard`
- Meta: `FieldState.CardNumberState` (`bin`, `last4`, `cardBrand`)
- Scan: Yes (BlinkCard)
- Icon: Yes via `VGSCardNumberEditText`
- Sens.: Yes

### FieldType.CARD_EXPIRATION_DATE
- Use: Card expiry (MM/YY or MM/YYYY with pattern override)
- P: `##/##` D: `/`
- Val: `VGSValidationRulePattern`, `VGSValidationRuleCardExpirationDate`
- Meta: basic validity; no brand data
- Scan: Yes (BlinkCard)
- Icon: No
- Sens.: No

### FieldType.DATE_RANGE
- Use: Generic date (custom ranges)
- P: default from DatePicker (e.g. `##/##/####` depending config) D: `/`
- Val: `VGSValidationRulePattern`, `VGSValidationRuleDateRange`
- Meta: basic validity
- Scan: No
- Icon: No Sens.: No

### FieldType.CVC
- Use: Card security code (3 or 4 digits brand dependent)
- P: `####` (allows up to 4 digits) D: ""
- Val: `VGSValidationRulePattern`, `VGSValidationRuleLengthMatch([3,4])`
- Meta: length & validity only
- Scan: Yes (BlinkCard)
- Icon: Yes (contextual CVC helper image)
- Sens.: Yes

### FieldType.CARD_HOLDER_NAME
- Use: Alphanumeric cardholder name
- P: none (no masking) D: ""
- Val: `VGSValidationRulePattern`
- Meta: validity, length
- Scan: Yes (BlinkCard)
- Icon: No Sens.: No

### FieldType.SSN
- Use: US Social Security Number
- P: `###-##-####` D: `-`
- Val: `VGSValidationRulePattern`
- Meta: may expose last4 via specialized state (`SSNNumberState`) if available
- Scan: No Icon: No Sens.: (Treat as sensitive in app logic even if `sensitive` flag false; never log.)

---
## 4. Field Setup & Configuration Pattern
Canonical card form snippet (XML Layout):
```xml
<com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
    android:id="@+id/cardNumberField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldName="card_number"/>

<com.verygoodsecurity.vgscollect.widget.VGSTextInputEditText
    android:id="@+id/cardHolderField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldName="card_holder_name"
    app:fieldType="cardHolderName"/>

<com.verygoodsecurity.vgscollect.widget.VGSTextInputEditText
    android:id="@+id/expiryField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldName="card_expiration_date"
    app:fieldType="cardExpirationDate"/>

<com.verygoodsecurity.vgscollect.widget.VGSTextInputEditText
    android:id="@+id/cvcField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldName="card_cvc"
    app:fieldType="cvc"/>
```

Binding in Activity/Fragment:
```kotlin
val vgsCollect = VGSCollect(this, "YOUR_VAULT_ID", Environment.SANDBOX)
vgsCollect.bindView(findViewById(R.id.cardNumberField))
vgsCollect.bindView(findViewById(R.id.cardHolderField))
vgsCollect.bindView(findViewById(R.id.expiryField))
vgsCollect.bindView(findViewById(R.id.cvcField))
```

---
## 5. Observing & Using Field State
State listening:
```kotlin
vgsCollect.addOnFieldStateChangeListener { state ->
    // Handle state changes
    if (state is FieldState.CardNumberState) {
        Log.d("VGS", "Card brand: ${state.cardBrand}, Last 4: ${state.last4}")
    }
}
```
Never log full PAN / CVC / SSN / raw file contents.

---
## 6. Submission APIs
Validation Gate:
```kotlin
val allFieldsValid = vgsCollect.getAllStates().all { it.isValid }
if (!allFieldsValid) {
    // Handle invalid fields
    return
}
```
Listener Variant:
```kotlin
vgsCollect.addOnResponseListeners(object : OnVgsResponseListener {
    override fun onResponse(response: VGSResponse?) {
        // Handle response
    }
})
vgsCollect.submit("/post")
```
Async/Await (using coroutines):
```kotlin
lifecycleScope.launch {
    val response = vgsCollect.submitAsync("/post")
    // Handle response
}
```

---
## 7. File Upload Pipeline
Setup:
```kotlin
val config = VGSFilePickerConfig.Builder(this)
    .setFieldName("my_file")
    .build()
VGSFilePickerController(this, activityResultLauncher, config).show()
```
On success: `vgsCollect.cleanFiles()`.

---
## 8. Card Scanning (BlinkCard)
Setup and Launch:
```kotlin
val intent = Intent(this, VGSBlinkCardRecognitionActivity::class.java)
    .putExtra(VGSBlinkCardRecognitionActivity.SCAN_CONFIGURATION,
        VGSBlinkCardRecognitionConfiguration.Builder()
            .setCardHolderFieldName("card_holder_name")
            .setCardNumberFieldName("card_number")
            .setExpirationDateFieldName("card_expiration_date")
            .setCVCFieldName("card_cvc")
            .build())
activityResultLauncher.launch(intent)
```
The result is automatically populated into the bound fields.

---
## 9. Analytics & Privacy
Opt out if mandated:
```kotlin
VGSAnalyticsClient.isAnalyticsEnabled = false
```
Do not modify analytics payload structure. Toggle only.

---
## 10. Logging & Redaction Policy
Allowed: brand, last4, validity flags, aggregate counts, error categories (e.g. timeout, validationFailure).
Forbidden: full PAN, full CVC, full SSN, unredacted file content, license keys, vault ID in user-visible error messages.
Ensure production logger level minimal (`.NONE` or equivalent).

---
## 11. Final Rule for Agents
If uncertain between two approaches, choose the one that:
1. Uses fewer APIs.
2. Avoids storing or exposing raw sensitive data.
3. Prefers non-deprecated, documented public surface.

Escalate (via comments / PR note) only when a required behavior is impossible without private or deprecated API access.

End of AGENTS.md