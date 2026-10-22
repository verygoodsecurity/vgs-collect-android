[![UT](https://img.shields.io/badge/Unit_Test-pass-green)]()
[![license](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE)
<img src="./ZeroDataLogo.png" width="55" hspace="8">

# VGS Collect SDK 

VGS Collect - is a product suite that allows customers to collect information securely without possession of it. VGS Collect Android SDK allows you to securely collect data from your users via forms without having to have that data pass through your systems. The form fields behave like traditional input fields while securing access to the unsecured data.

Table of contents
=================

<!--ts-->
   * [AI Agent Integration](#ai-agent-integration)
   * [Structure](#structure)
   * [Integration](#integration)
   * [Next steps](#next-steps)
   * [License](#license)
<!--te-->

<p align="center">
<img src="/img/vgs-collect-android-state.png" width="200" alt="VGS Collect Android SDK States" hspace="20"><img src="/img/vgs-collect-android-response.png" width="200" alt="VGS Collect Android SDK Response" hspace="20">
</p>

## AI Agent Integration
This repository ships a public AI skill at [`skills/vgs-collect-android-guide/SKILL.md`](./skills/vgs-collect-android-guide/SKILL.md) for teams integrating `VGSCollectSDK` into Android applications.

Recommended: install the skill with `skills.sh`. This is the easiest way to give a compatible AI agent repository-specific guidance for `VGSCollectSDK` integration work.

The installed skill bundle includes `references/AGENTS.md`, which is the canonical durable integration guide for this SDK.

What the skill is useful for:
- matching guidance to the installed `vgscollect` version when that version can be detected
- steering integrations toward correct secure field setup, scanning, file upload, and submission flows
- enforcing non-empty vault configuration, `state.isValid` validation gates, and post-upload `cleanFiles()` cleanup
- preserving redaction-safe logging and analytics toggling guidance
- following upgrade and testing rules in [`references/AGENTS.md`](./skills/vgs-collect-android-guide/references/AGENTS.md)

Install the skill with `skills.sh`:
```bash
npx skills add https://github.com/verygoodsecurity/vgs-collect-android --skill vgs-collect-android-guide
```

If your AI tool does not support skills yet, load [`skills/vgs-collect-android-guide/references/AGENTS.md`](./skills/vgs-collect-android-guide/references/AGENTS.md) directly.

### Minimal System Prompt Example:

```
You are an autonomous engineering agent integrating the VGS Collect Android SDK into an existing Kotlin app.
Use skills/vgs-collect-android-guide/references/AGENTS.md as the authoritative policy.
Constraints:
- Only public, non-deprecated APIs.
- No raw sensitive data in logs/tests.
- Validate all fields before submission.
- Clean files after successful upload.
Goals:
1. Add a secure card form (number, name, exp, cvc) with redacted logging.
2. Integrate BlinkCard scanning.
3. Provide unit tests for valid/invalid card + expiration edge case.
Return: Modified Kotlin source files only, no secrets.
```

### Developer Prompt (Inline Example for a Single Task):

```
Task: Add a custom payment card brand "mycard" (BIN starts with 7777) and ensure CVC field adapts.
Follow skills/vgs-collect-android-guide/references/AGENTS.md.
Do not break existing brand detection; add tests for detection and negative near-miss.
```


## Structure
* **VGSCollect SDK** (`vgscollect`) - provides an API for interacting with the VGS Vault. Includes View-based widgets, Jetpack Compose text fields, and Card Management Platform (CMP) sessions (`VGSCollect.session()` → `createCard()` / `updateCard()`).
* **Card Scanner** (`vgscollect-blinkcard`) - This module is for adapting <a href="https://github.com/blinkcard/blinkcard-android">blinkcard-android SDK</a> with VGS Collect Android SDK.
* **Analytics** (`vgs-sdk-analytics`) - analytics module used by the SDK.
* **app** - sample application to act as the host app for testing the SDK during development

## Integration
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK 17](https://adoptium.net/) on your machine. The SDK supports Android **minSdk 23** and is built with **compileSdk 37**.

### Jetpack Compose (recommended)
Add the SDK to your `build.gradle.kts`. Compose dependencies are `compileOnly` in the SDK, so your app must provide `androidx.compose.material` or `androidx.compose.material3`:

```kotlin
dependencies {
    implementation("com.verygoodsecurity:vgscollect:2.1.0")

    // Optional: BlinkCard scanner adapter
    implementation("com.verygoodsecurity:adapter-blinkcard:1.0.6")
}
```

Both Material (`com.verygoodsecurity.vgscollect.widget.compose.material`) and Material3 (`...compose.material3`) variants are available. Fields are driven by immutable states, so there is no `bindView()` — pass the states straight to the submit call:

```kotlin
@Composable
fun PaymentForm(collect: VGSCollect) {
    var cardholderState by rememberVgsCardholderTextFieldState(collect = collect, fieldName = "data.name")
    var cardNumberState by rememberVgsCardNumberTextFieldState(collect = collect, fieldName = "data.card_number")
    var expiryState by rememberVgsExpiryTextFieldState(
        collect = collect,
        fieldName = "data.expiry",
        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat = VgsExpiryDateFormat.LongYearMonth,
    )
    var cvcState by rememberVgsCvcTextFieldState(collect = collect, fieldName = "data.cvc")

    // Keep CVC length in sync with the detected card brand
    LaunchedEffect(cardNumberState.cardBrand) {
        cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)
    }

    Column {
        VgsCardholderOutlinedTextField(state = cardholderState, onStateChange = { cardholderState = it })
        VgsCardNumberOutlinedTextField(state = cardNumberState, onStateChange = { cardNumberState = it })
        VgsExpiryOutlinedTextField(state = expiryState, onStateChange = { expiryState = it })
        VgsCvcOutlinedTextField(state = cvcState, onStateChange = { cvcState = it })

        val states = listOf(cardholderState, cardNumberState, expiryState, cvcState)
        Button(
            enabled = states.all { it.isValid },
            onClick = {
                collect.asyncSubmit(
                    VGSRequest.VGSRequestBuilder().setPath("/post").build(),
                    states,
                )
            },
        ) { Text("Submit") }
    }
}
```

Receive the result by registering a `VgsCollectResponseListener` with `collect.addOnResponseListeners(...)`. Also available: `tokenize()`, `createAliases()`, per-field tokenization configs, custom validators and visual transformations — see [AGENTS.md](./skills/vgs-collect-android-guide/references/AGENTS.md).

### View-based (XML)

<table>
  <tr>
    <td colspan="2">
      <b>Integrate the VGS Collect SDK to your project</b>. <br/>
      Add the following to your <code>build.gradle</code> file.
    </td>
  </tr>
  <tr>
    <td colspan="2">

```gradle
dependencies {
    implementation "androidx.appcompat:appcompat:<version>"
    implementation "com.google.android.material:material:<version>"

    implementation "com.verygoodsecurity:vgscollect:2.1.0"
}
```
  </td>
  </tr>


  <tr>
    <td> <b>Add input fields to <code>R.layout.activity_main </code> layout file </b>. </td>
     <th rowspan="2" width="33%" ><img src="/img/vgs-layout-config.png"></th>
  </tr>
  <tr>
    <td >

```xml
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hint="Card number"
        app:boxCornerRadius="8dp"
        app:boxBackgroundModes="outline">

        <com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
            android:id="@+id/cardNumberField"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="8dp"
            app:fieldName="cardNumber"
            app:numberDivider="-"
            app:cardBrandIconGravity="end"/>

    </com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout>

//Other fields..

</LinearLayout>
```
  </td>
  </tr>

  <tr>
    <td>
      <b> To initialize VGSCollect you have to set your <a href="https://www.verygoodsecurity.com/docs/terminology/nomenclature#vault">tenant id</a> and <a href="https://www.verygoodsecurity.com/docs/getting-started/going-live#sandbox-vs-live">Environment</a> type.</b> </br>You can find more information at the following <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/submit-data#start-session">section</a>.
    </td>
     <th rowspan="2"><img src="/img/vgs-field-setup-state.gif"></th>
  </tr>
  <tr>
    <td>

```kotlin

class MainActivity : AppCompatActivity() {
  private lateinit var vgsForm:VGSCollect

  override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_main)

     vgsForm = VGSCollect(this, "<tenant_id>", Environment.SANDBOX)

     val view = findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
     vgsForm.bindView(view)
    // Bind other fields

  }

  override fun onDestroy() {
      vgsForm.onDestroy()
      super.onDestroy()
  }
}
```
  </td>
  </tr>


  <tr>
    <td>
      <b>Fields state tracking</b>. </br> When an object of this type is attached to a VGS secure field, its methods will be called when the text or focus is changed.
    </td>
     <th rowspan="2"><img src="/img/vgs-field-states.gif"></th>
  </tr>
  <tr>
    <td>

```kotlin

class MainActivity : AppCompatActivity() {
  private lateinit var vgsForm:VGSCollect

  override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_main)

     vgsForm = VGSCollect(this, "<tenant_id>", Environment.SANDBOX)
     vgsForm.addOnFieldStateChangeListener(
     	object : OnFieldStateChangeListener {
         override fun onStateChange(state: FieldState) {
            // Handle input fields states
         }
     })

     val view = findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
     view?.setOnFieldStateChangeListener(
     	object : OnFieldStateChangeListener {
         override fun onStateChange(state: FieldState) {
            // Handle single field states
         }
     })
     vgsForm.bindView(view)
    // Bind other fields

  }

  override fun onDestroy() {
     vgsForm.onDestroy()
     super.onDestroy()
  }
}
```
  </td>
  </tr>


  <tr>
    <td> <b>Send a simple request. Receive responses. </b> </br> Call <code>asyncSubmit</code> to execute and send data on VGS Server. </br> To retrieve response you need to implement <code>VgsCollectResponseListener </code>. </td>
     <th rowspan="2"><img src="/img/vgs-response-state.gif"></th>
  </tr>
  <tr>
    <td>

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    vgsForm = VGSCollect(this, "<tenant_id>", Environment.SANDBOX)

    val view = findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
    vgsForm.bindView(view)
    // Bind other fields

    val submitBtn = findViewById<Button>(R.id.submitBtn)
    submitBtn?.setOnClickListener {
        vgsForm.asyncSubmit("/post", HTTPMethod.POST)
    }

    vgsForm.addOnResponseListeners(
    	object : VgsCollectResponseListener {
        override fun onResponse(response: VGSResponse?) {
            when(response) {
                is VGSResponse.SuccessResponse -> {
                    val successCode = response.successCode
                    val rawResponse = response.rawResponse
                }
                is VGSResponse.ErrorResponse -> {
                    val errorCode = response.errorCode
                    val localizeMessage = response.localizeMessage
                }
            }
        }
    })
}
```
  </td>
  </tr>

</table>


## Next steps
Check out documentation guides:
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/overview">Overview</a>
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/ui-components">UI Components</a>
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/submit-data">Observing States and Submitting Data</a>

For a quick start, try our <a href="https://github.com/verygoodsecurity/android-sdk-demo">Demo application</a>.

## License
VGSCollect Android SDK is released under the MIT license. [See LICENSE](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE) for details.
