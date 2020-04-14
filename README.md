[![CircleCI](https://circleci.com/gh/verygoodsecurity/vgs-collect-android/tree/master.svg?style=svg&circle-token=24087545f8aff3cee11ebe55330d2df778a7bb1f)](https://circleci.com/gh/verygoodsecurity/vgs-collect-android/tree/master)
[![UT](https://img.shields.io/badge/Unit_Test-pass-green)]()
[![license](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/vg/vgs-collect-android/vgscollect/images/download.svg) ](https://bintray.com/vg/vgs-collect-android/vgscollect/_latestVersion)

# VGS Collect SDK 

VGS Collect - is a product suite that allows customers to collect information securely without possession of it. VGS Collect Android SDK allows you to securely collect data from your users via forms without having to have that data pass through your systems. The form fields behave like traditional input fields while securing access to the unsecured data.

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Structure](#structure)
   * [Integration](#integration)
      * [Add the SDK to your project](#add-the-sdk-to-your-project)
   * [Usage](#usage)
      * [Session initialization](#session-initialization)
      * [Submit information](#submit-information)
      * [Fields state tracking](#fields-state-tracking)
      * [Handle service response](#handle-service-response)
      * [End session](#end-session)
   * [Next steps](#next-steps)
   * [License](#license)
<!--te-->

<p align="center">
<img src="https://github.com/verygoodsecurity/vgs-collect-android/blob/master/vgs-collect-android-state.png" width="200" alt="VGS Collect Android SDK States" hspace="20"><img src="https://github.com/verygoodsecurity/vgs-collect-android/blob/master/vgs-collect-android-response.png" width="200" alt="VGS Collect Android SDK Response" hspace="20">
</p>

## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 19 |
| Target SDK | 29 |
| androidx.appcompat:appcompat | 1.1.0 |
| com.google.android.material:material | 1.1.0 |
| androidx.core:core-ktx | 1.2.0 |
| org.jetbrains.kotlin:kotlin-stdlib-jdk7 | 1.3.71 |

## Structure
* **VGSCollect SDK** - provides an API for interacting with the VGS Vault
* **Card Scanner** - This module is for adapting <a href="https://github.com/card-io/card.io-Android-SDK">Card.io SDK</a> with VGS Collect Android SDK.
* **app** - sample application to act as the host app for testing the SDK during development

## Integration
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.

#### Add the SDK to your project
If you are using Maven, add the following to your `build.gradle` file:
```gradle
dependencies {
   implementation 'com.verygoodsecurity:vgscollect:1.1.1’
}
```

## Usage

#### Session initialization
Add VGSEditText to your layout file:
```xml
<?xml version="1.0" encoding="utf-8"?>

<com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
  	 android:id="@+id/your_field"
   	 android:layout_width="match_parent"
  	 android:layout_height="match_parent" />
```

Add the following code to initialize the SDK to your Activity or Fragment class:
```java
public class ExampleActivity extends Activity {
   private VGSCollect vgsForm;

   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_layout);

        vgsForm = new VGSCollect( this, user_key, Environment.SANDBOX);

        vgsForm.addOnFieldStateChangeListener(...);
        vgsForm.setOnResponseListener(...);

        VGSCardNumberEditText yourField = findViewById(R.id.your_field);
        vgsForm.bindView(yourField);
    }

    private void submitData() {
        //...
    }

    @Override
    protected void onDestroy() {
        //...
    }
}
```

#### Fields state tracking
Whenever an EditText changes, **VGSCollect** can notify user about it. Implement `OnFieldStateChangeListener` to observe changes:
```java
  vgsForm.addOnFieldStateChangeListener(new OnFieldStateChangeListener() {
            @Override
            public void onStateChange(FieldState state) {
                //...
            }
        });
```

#### Submit information
Call `asyncSubmit` or `submit` to execute and send data on VGS Server if you want to handle multithreading by yourself:
```java
private void submitData() {
    //..
    vgsForm.asyncSubmit("/path", HTTPMethod.POST);
}
```

There is an option to send custom fields in the same request that the SDK CTA sends:
```java
private void submitData() {
    //..
    HashMap data = HashMap<String, String>();
    data.put("key", "value");
    vgsForm.setCustomData(data);

    vgsForm.asyncSubmit("/path", HTTPMethod.POST);
}
```

More to the point SDK allows send your custom headers:
```java
private void submitData() {
    //..
    HashMap headers = HashMap<String, String>();
    headers.put("key", "value");
    vgsForm.setCustomHeaders(headers);

    vgsForm.asyncSubmit("/path", HTTPMethod.POST);
}
```

To clear all custom headers use `resetCustomHeaders` or `resetCustomData` to clear custom fields added before.
```java
private void submitData() {
    vgsForm.resetCustomHeaders();

    HashMap headers = HashMap<String, String>();
    headers.put("key", "value");
    vgsForm.setCustomHeaders(headers);

    vgsForm.asyncSubmit("/path", HTTPMethod.POST);
}
```

#### Handle service response
You need to implement `VgsCollectResponseListener` to read response:
```java
  vgsForm.setOnResponseListener(new VgsCollectResponseListener() {
            @Override
            public void onResponse(@org.jetbrains.annotations.Nullable VGSResponse response) {
                //...
            }
        });
```

#### End session
Finish work with **VGSCollect** by calling `onDestroy` inside android onDestroy callback:
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vgsForm.onDestroy();
    }
 ```

## Next steps
Check out documentation guides:
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/overview">Overview</a>
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/ui-components">UI Components</a>
-  <a href="https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk/submit-data">Observing States and Submitting Data</a>

For a quick start, try our <a href="https://github.com/verygoodsecurity/android-sdk-demo">Demo application</a>.

## License
VGSCollect Android SDK is released under the MIT license. [See LICENSE](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE) for details.
