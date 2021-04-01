
[![UT](https://img.shields.io/badge/Unit_Test-pass-green)]()
[![license](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE)
<img src="./ZeroDataLogo.png" width="55" hspace="8">

# VGS Collect SDK 

VGS Collect - is a product suite that allows customers to collect information securely without possession of it. VGS Collect Android SDK allows you to securely collect data from your users via forms without having to have that data pass through your systems. The form fields behave like traditional input fields while securing access to the unsecured data.

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Structure](#structure)
   * [Integration](#integration)
   * [Next steps](#next-steps)
   * [License](#license)
<!--te-->

<p align="center">
<img src="/img/vgs-collect-android-state.png" width="200" alt="VGS Collect Android SDK States" hspace="20"><img src="/img/vgs-collect-android-response.png" width="200" alt="VGS Collect Android SDK Response" hspace="20">
</p>

## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 19 |
| Target SDK | 30 |
| androidx.appcompat:appcompat | 1.2.0 |
| com.google.android.material:material | 1.2.0 |

## Structure
* **VGSCollect SDK** - provides an API for interacting with the VGS Vault
* **Card Scanner** - This module is for adapting <a href="https://github.com/card-io/card.io-Android-SDK">Card.io SDK</a> with VGS Collect Android SDK.
* **app** - sample application to act as the host app for testing the SDK during development

## Integration
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.


<table>
  <tr>
    <td colspan="2">
      <b>Integrate the VGS Collect SDK to your project</b>. <br/>
      If you are using Maven, add the following to your <code>build.gradle</code> file.
    </td>
  </tr>
  <tr>
    <td colspan="2">

```gradle
dependencies {
    implementation "androidx.appcompat:appcompat:<version>"
    implementation "com.google.android.material:material:<version>"

    implementation "com.verygoodsecurity:vgscollect:<latest-version>"
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
      <b> To initialize VGSCollect you have to set your <a href="https://www.verygoodsecurity.com/docs/terminology/nomenclature#vault">vault id</a> and <a href="https://www.verygoodsecurity.com/docs/getting-started/going-live#sandbox-vs-live">Environment</a> type.</b> </br>You can find more information at the following <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/submit-data#start-session">section</a>.
    </td>
     <th rowspan="2"><img src="/img/vgs-field-setup-state.gif"></th>
  </tr>
  <tr>
    <td>

```kotlin
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect

class MainActivity : AppCompatActivity() {
  private lateinit var vgsForm:VGSCollect

  override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_main)

     vgsForm = VGSCollect(this, "<vauilt_id>", Environment.SANDBOX)

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
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect

class MainActivity : AppCompatActivity() {
  private lateinit var vgsForm:VGSCollect

  override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_main)

     vgsForm = VGSCollect(this, "<vauilt_id>", Environment.SANDBOX)
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
     vgsForm.bindView(cardNumberField)
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

    vgsForm = VGSCollect(this, "<vauilt_id>", Environment.SANDBOX)

    val submitBtn = findViewById<Button>(R.id.submitBtn)
    submitBtn?.setOnClickListener {
        submitData()
    }

    val view = findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
    view.bindView(cardNumberField)
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
