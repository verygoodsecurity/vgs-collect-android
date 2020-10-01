# VGSCollect-Card.io 

Module based on [Card.io](https://github.com/card-io/card.io-Android-SDK). It allows to secure integrate scanner with [VGSCollect](https://github.com/verygoodsecurity/vgs-collect-android)

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Integration](#integration)
      * [Add the SDK to your project](#add-the-sdk-to-your-project)
   * [Usage](#usage)
<!--te-->

## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 16 |
| io.card:android-sdk | 5.5.1 |
| androidx.appcompat:appcompat | 1.2.0 |
| androidx.core:core-ktx | 1.2.0 |

## Integration 
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.

#### Add the SDK to your project
To use the SDK in project you just simply need to add the following line of dependency in your module `gradle.gradle` file:
```
dependencies {
   implementation 'com.verygoodsecurity:vgscollect:1.0.2’ //required version 1.0.2 or above
   implementation 'com.verygoodsecurity.api:adapter-cardio:1.0.0’
}
```

## Usage

**Important**: VGSCollect should be configured too. Check [here](https://www.verygoodsecurity.com/docs/vgs-collect/android-sdk#step-2-configure-your-app) how to do it.

Before scanning you have to setup which information you need to retrieve and final destination VGS secure field:
```
HashMap<String, Integer> scanSettings = new HashMap<>();
scanSettings.put(cardNumberField.getFieldName(), ScanActivity.CARD_NUMBER);
scanSettings.put(cardCvcield.getFieldName(), ScanActivity.CARD_CVC);
scanSettings.put(cardHolderField.getFieldName(), ScanActivity.CARD_HOLDER);
scanSettings.put(cardExpDateField.getFieldName(), ScanActivity.CARD_EXP_DATE);
scanSettings.put(cardPostalCodeField.getFieldName(), ScanActivity.POSTAL_CODE);
```

To start scanning you need to attach a Map with settings to Intent and start ScanActivity.
```
public void scanCard() {
   Intent intent = new Intent(this, ScanActivity.class);
 
   HashMap<String, Integer> scanSettings = new HashMap<>();
   scanSettings.put(cardNumberField.getFieldName(), ScanActivity.CARD_NUMBER);
 
   intent.putExtra(ScanActivity.SCAN_CONFIGURATION, scanSettings);
 
   startActivityForResult(intent, USER_SCAN_REQUEST_CODE);
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
