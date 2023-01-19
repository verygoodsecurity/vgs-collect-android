# VGSCollect-Card.io 

Module based on [blinkcard-android](https://github.com/blinkcard/blinkcard-android). It allows to secure integrate scanner with [VGSCollect](https://github.com/verygoodsecurity/vgs-collect-android)

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Integration](#integration)
      * [Add the SDK to your project](#add-the-sdk-to-your-project)
   * [Usage](#usage)
<!--te-->

## Dependencies

| Dependency                   | Version |
|:-----------------------------|:-------:|
| Min SDK                      |   21    |
| com.microblink:blinkcard     |  2.7.0  |
| androidx.appcompat:appcompat |  1.6.0-rc01  |
| androidx.core:core-ktx       |  1.9.0  |

## Integration 
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.

#### Add the SDK to your project
To use the SDK in project you just simply need to add the following line of dependency in your module `gradle.gradle` file:
```
repositories {
    maven { url 'https://maven.microblink.com' }
}

dependencies {
   implementation 'com.verygoodsecurity:vgscollect:1.7.5' // required version 1.7.5 or above
   implementation 'com.verygoodsecurity.api:adapter-blinkcard:<latest-version>'
}
```

## Usage

**Important**: VGSCollect should be configured too. Check [here](https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk#step-2-configure-your-app) how to do it.

Create intent:
```
val intent = VGSBlinkCardIntentBuilder(this)
     .setCardNumberFieldName(vgsTiedCardNumber.getFieldName())
     .setCardHolderFieldName(vgsTiedCardHolder.getFieldName())
     .setExpirationDateFieldName(vgsTiedExpiry.getFieldName())
     .setCVCFieldName(vgsTiedCvc.getFieldName())
     build()
```

To start scanning you need create intent and start this intent for result:
```
public void scanCard() {
     val intent = VGSBlinkCardIntentBuilder(this)
          .setCardNumberFieldName(vgsTiedCardNumber.getFieldName())
          .setCardHolderFieldName(vgsTiedCardHolder.getFieldName())
          .setExpirationDateFieldName(vgsTiedExpiry.getFieldName())
          .setCVCFieldName(vgsTiedCvc.getFieldName())
          build()
     startActivityForResult(intent, SCAN_REQUEST_CODE)
}
```

Also very important to call [VGSCollect](https://github.com/verygoodsecurity/vgs-collect-android) **onActivityResult** method inside Activity onActivityResult callback:
```
@Override 
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    vgsForm.onActivityResult(requestCode, resultCode, data);
}
```
