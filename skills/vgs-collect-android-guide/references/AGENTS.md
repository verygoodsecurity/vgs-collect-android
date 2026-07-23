# AGENTS.md

**SDK Version: 1.10.3**

This guide is tailored for autonomous engineering agents integrating `VGSCollectSDK` into Android applications. It focuses on deterministic, automatable steps: environment assessment, dependency installation, feature wiring (text fields, scanning, file upload), privacy compliance, testing, maintenance, and safe upgrade workflows.

Documentation Source of Truth Across Versions
- The canonical durable instruction source for this repository lives at `skills/vgs-collect-android-guide/references/AGENTS.md`.
- The single public AI skill entrypoint lives at `skills/vgs-collect-android-guide/SKILL.md`. It routes task type and version selection, then defers SDK rules and invariants to this file.
- The installed skill bundle must ship `skills/vgs-collect-android-guide/references/AGENTS.md` so standalone skill installs receive this policy file automatically.
- For public skill and documentation workflows, the canonical repository for tags and versioned docs is `https://github.com/verygoodsecurity/vgs-collect-android`.
- When the SDK version used by the target project can be determined, agents MUST prefer the durable guidance snapshot from the matching Git tag in the public `verygoodsecurity/vgs-collect-android` repository before giving version-sensitive guidance. For older tags, that snapshot may still live at repo-root `AGENTS.md`.
- If an exact tag is unavailable, agents MUST use the nearest compatible tag and clearly disclose the mismatch before giving version-sensitive guidance.
- Private forks or internal mirrors do not override the public repository as the source of truth for public skill guidance.
- If the SDK is not installed or its version cannot be determined, agents SHOULD use the default-branch copy of this file and disclose that latest guidance is used.

## Scope
- Use this file as the ONLY high-level instruction source when adding, updating, or testing VGS Collect Android SDK (`vgscollect`) usage in a downstream app.
- Contain ONLY public, non-deprecated APIs.
- Emphasis: security, correctness, determinism, reproducibility.

## Success Criteria for Any Agent Task
1. Sensitive data (PAN, CVC, SSN, files) NEVER logged, persisted, or leaked to analytics.
2. All fields validated (`state.isValid`) before submission.
3. Network submission surfaces aliases/tokens only to the app layer.
4. File uploads always followed by `collector.cleanFiles()` after success.
5. APIs used exist in current public surface and are not deprecated.
6. Version pins updated intentionally with a migration note and tests green.

---
## 1. Core Concepts (Mental Model)
- `VGSCollect` orchestrates secure collection and submission to a VGS Vault you own (identified by vault ID + environment / data region).
- **View-based**: UI inputs (`VGSTextInputEditText`) never expose raw values outside controlled SDK memory; you interact through configuration + state snapshots. Fields are bound to `VGSCollect` via `bindView()`.
- **Compose**: Immutable state objects (`BaseFieldState` subclasses) are created via `rememberVgs*TextFieldState()` factories and passed to `Vgs*TextField` composables. States are self-contained — no `bindView()` needed.
- Field `state` drives UI (validity, emptiness, metadata like last4, brand) - do not persist raw input.
- Submissions can be performed via listener, a direct `async` request, or by passing Compose states to `asyncSubmit()` / `tokenize()` / `createAliases()`.

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
- Empty vaultId -> initialization should fail fast (use preconditions).
- Using `.LIVE` in debug with test cards unintentionally -> enforce build configuration checks.
- Mixing aliases from different environments -> keep storage / processing segregated.

Security Note: Never derive environment from user input; it must be a static config determined at build or app launch.

---
## 2. Public Building Blocks — View-based (Summary Table)
(Do NOT assume additional behavior outside listed intents.)
- `VGSCollect`: register fields, submit data/files, observe states, manage headers, cleanup files.
- `VGSTextInputEditText`: generic configurable secure text field.
- `VGSCardNumberEditText`: adds automatic card brand detection and icon handling.
- `FieldType`: semantic `type`, formatting and validation rules.
- `FieldState` (+ specialized states e.g. `CardState`): read-only snapshot for UI logic.
- `VGSFilePickerConfig` + `VGSFilePickerController`: secure file selection pipeline.
- `VGSBlinkCardRecognitionActivity`: card scanning (BlinkCard module) - preferred scanning path.
- Validation Rule Types (examples): `VGSValidationRuleLength`, `VGSValidationRulePattern`, (others similarly shaped).
- `VGSAnalyticsClient`: toggle analytics collection.
- `VGSCollectLogger`: adjust log verbosity (ensure `.NONE` in production if required).

---
## 2A. FieldType Reference & Capabilities (View-based)
Purpose: choose correct `FieldType` to obtain built-in formatting and validation. Each case sets defaults (pattern, divider, keyboardType, validation rule set, optional metadata and sensitivity).

Notation: P=Formatting Pattern, D=Divider, Val=Default Validation Rules (names), Meta=Metadata via state, Scan=Supported by BlinkCard scan mapping, Icon=Built-in Icon Support, Sens.=Sensitive (tokenized priority).

### FieldType.INFO
- Use: generic text (no formatting/validation by default)
- P: "" D: "" Val: (none) Meta: basic flags only Scan: No Icon: No Sens.: No

### FieldType.CARD_NUMBER
- Use: PAN input with brand detection and Luhn
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
- Meta: length and validity only
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
## 2B. Public Building Blocks — Compose (Summary Table)
The SDK provides native Jetpack Compose composables as an alternative to View-based widgets. Both Material (`widget.compose.material`) and Material3 (`widget.compose.material3`) variants are available. Compose dependencies are `compileOnly` — the host app must provide them.

Composables (each has filled + outlined variant):
- `VgsTextField` / `VgsOutlinedTextField`: generic secure text field (city, postal code, address, etc.).
- `VgsCardNumberTextField` / `VgsCardNumberOutlinedTextField`: card number with brand detection, Luhn validation, masking.
- `VgsCvcTextField` / `VgsCvcOutlinedTextField`: CVC/CVV with brand-dependent length.
- `VgsExpiryTextField` / `VgsExpiryOutlinedTextField`: card expiry with configurable input/output date formats.
- `VgsSsnTextField` / `VgsSsnOutlinedTextField`: SSN with mask (`###-##-####`) and validation.
- `VgsCardholderTextField` / `VgsCardholderOutlinedTextField`: cardholder name with pattern validation.

State factories (use inside `@Composable`):
- `rememberVgsTextFieldState`, `rememberVgsCardNumberTextFieldState`, `rememberVgsCvcTextFieldState`, `rememberVgsExpiryTextFieldState`, `rememberVgsSsnTextFieldState`, `rememberVgsCardholderTextFieldState`.

Tokenization configs (one per field type):
- `VgsCardNumberTokenizationConfig`, `VgsCvcTokenizationConfig`, `VgsCardholderTokenizationConfig`, `VgsSsnTokenizationConfig`, `VgsExpiryTokenizationConfig`, `VgsTextFieldTokenizationConfig`.

Validators:
- `VgsRequiredFieldValidator`, `VgsRegexValidator`, `VgsTextLengthValidator`, `VgsLuhnAlgorithmValidator`, `VgsMinMaxDateValidator`.

Visual transformations:
- `VgsMaskVisualTransformation`, `VgsPasswordVisualTransformation`, `VgsVisualTransformation.None`.

Card brand:
- `VgsCardBrand` (detection, icons, mask, lengths), `VgsCardBrand.DEFAULT`, `VgsCardBrand.UNKNOWN`.

Date formats:
- `VgsExpiryDateFormat.MonthShortYear` (MM/yy), `.MonthLongYear` (MM/yyyy), `.ShortYearMonth` (yy/MM), `.LongYearMonth` (yyyy/MM).

Date serializer:
- `VgsExpirySerializer` for splitting expiry into separate month/year JSON fields on submit.

### Compose vs View: key differences
- No `bindView()` / `unbindView()` — states are self-contained and passed to submit methods directly.
- States are immutable — every keystroke produces a new state object via `onStateChange`.
- Validation is built into the state: check `state.isValid` / `state.validationResult`.
- Card brand detection is automatic: read `cardNumberState.cardBrand` for icon/name.
- CVC brand sync is manual: call `cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)` in a `LaunchedEffect`.
- No `FieldType` enum — field type is determined by which composable/state you use.

---
## 2C. Compose Field Setup & Configuration Pattern
Canonical Compose card form:
```kotlin
@Composable
fun PaymentForm(collect: VGSCollect, onSubmit: (List<BaseFieldState>) -> Unit) {
    // Step 1: Initialize states
    var cardholderState by rememberVgsCardholderTextFieldState(
        collect = collect,
        fieldName = "data.name",
    )
    var cardNumberState by rememberVgsCardNumberTextFieldState(
        collect = collect,
        fieldName = "data.card_number",
    )
    var cvcState by rememberVgsCvcTextFieldState(
        collect = collect,
        fieldName = "data.cvc",
    )
    var expiryState by rememberVgsExpiryTextFieldState(
        collect = collect,
        fieldName = "data.expiry",
        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat = VgsExpiryDateFormat.LongYearMonth,
    )

    // Step 2: Sync CVC brand from card number
    LaunchedEffect(cardNumberState.cardBrand) {
        cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)
    }

    // Step 3: Render fields (Material outlined variant shown)
    VgsCardholderOutlinedTextField(
        state = cardholderState,
        onStateChange = { cardholderState = it },
    )
    VgsCardNumberOutlinedTextField(
        state = cardNumberState,
        onStateChange = { cardNumberState = it },
        trailingIcon = {
            Image(
                painter = painterResource(id = cardNumberState.cardBrand.cardIcon),
                contentDescription = cardNumberState.cardBrand.name,
            )
        },
    )
    VgsExpiryOutlinedTextField(
        state = expiryState,
        onStateChange = { expiryState = it },
    )
    VgsCvcOutlinedTextField(
        state = cvcState,
        onStateChange = { cvcState = it },
    )

    // Step 4: Submit
    Button(onClick = {
        onSubmit(listOf(cardholderState, cardNumberState, expiryState, cvcState))
    }) { Text("Submit") }
}
```

---
## 2D. Compose Submission APIs
Compose states are passed directly to `VGSCollect` submit methods:
```kotlin
// Async (callback-based)
collect.asyncSubmit(
    VGSRequest.VGSRequestBuilder().setPath("/post").build(),
    listOf(cardholderState, cardNumberState, expiryState, cvcState),
)

// Coroutines
val response = collect.submitAsync("/post", fieldsStates = listOf(...))

// Tokenization
collect.tokenize(
    VGSTokenizationRequest.VGSRequestBuilder().build(),
    listOf(cardholderState, cardNumberState, expiryState, cvcState),
)

// Create aliases
collect.createAliases(
    VGSCreateAliasesRequest.VGSRequestBuilder().build(),
    listOf(cardholderState, cardNumberState, expiryState, cvcState),
)
```

Tokenization config per field:
```kotlin
var cardNumberState by rememberVgsCardNumberTextFieldState(
    collect = collect,
    fieldName = "data.card_number",
    tokenizationConfig = VgsCardNumberTokenizationConfig(), // FPE_SIX_T_FOUR, PERSISTENT
)
var cvcState by rememberVgsCvcTextFieldState(
    collect = collect,
    fieldName = "data.cvc",
    tokenizationConfig = VgsCvcTokenizationConfig(), // NUM_LENGTH_PRESERVING, VOLATILE
)
```

Custom validators:
```kotlin
var state by rememberVgsTextFieldState(
    collect = collect,
    fieldName = "data.city",
    validators = listOf(VgsRequiredFieldValidator(), VgsRegexValidator("^[a-zA-Z ]+$")),
)
// Pass emptyList() to disable validation entirely
// Pass null (default) to use the field type's built-in validators
```

Compose validation gate:
```kotlin
val states = listOf(cardholderState, cardNumberState, expiryState, cvcState)
val allValid = states.all { it.isValid }
if (!allValid) {
    // Handle invalid fields — check individual state.validationResult for details
    return
}
```

---
## 3. Specific Validation Rules

### ABARoutingNumberRule
- Use: Validates a 9-digit ABA routing number using a checksum algorithm.
- Example:
```kotlin
val abaRule = ABARoutingNumberRule.ValidationBuilder()
    .setErrorMsg("Invalid ABA routing number")
    .build()

val routingNumberField = VGSTextInputEditText(this)
routingNumberField.addRule(abaRule)
```

---
## 4. View-based Field Setup & Configuration Pattern
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
## 5. Observing & Using Field State (View-based)
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
## 6. Submission APIs (View-based)
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

