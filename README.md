# VGS Collect SDK 

>VGS Collect - is a product suite that allows customers to collect information securely without possession of it. VGS Collect mobile SDKs - are native mobile forms modules that allow customers to collect information securely on mobile devices with iOS and Android. **Source:** [vgs-collect](https://www.verygoodsecurity.com/docs/vgs-collect/index)
>
>Customers can use the same VGS Vault and the same server-side for Mobile apps as for Web. Their experience should stay the same and be not dependent on the platform they use: Web or Mobile.

## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 14 |
| Target SDK | 28 |
| com.android.support:appcompat-v7 | 28.0.0 |
| androidx.appcompat:appcompat | 1.1.0 |
| androidx.core:core-ktx | 1.1.0 |
| com.android.support:design | 28.0.0 |
| org.jetbrains.kotlin:kotlin-stdlib-jdk7 | 1.3.50 |

## Structure
* **VGSCollect SDK** - provides an API for interacting with the VGS Vault
* **app** - sample application to act as the host app for testing the SDK during development

## Integration 
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.

#### Add the SDK to your project
If you are using Maven, add the following to your `build.gradle` file:
```
This will be added later
```
#### Add permissions
The **SDK** requires the following permissions. Please add them to your `AndroidManifest.xml` file if they are not already present:
```
<uses-permission android:name="android.permission.INTERNET"/>
```

## Session tracking
In your Activity or Fragment class add the following code to initialize the SDK:
```
public class ExampleActivity extends Activity {
   private VGSCollect vgsForm = new VGSCollect( user_key, Environment.SANDBOX);
   
   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examle_layout);
        
        VGSEditText yourField = findViewById(R.id.your_field);
        vgsForm.bindView(yourField);
    }
}
```

## Submit information
 Description will be added later
```
  vgsForm.asyncSubmit(this, "/path", HTTPMethod.POST, customHeaders);
  vgsForm.submit(this, "/path", HTTPMethod.POST, customHeaders);
```

## Fields state tracking
You can register a `OnFieldStateChangeListener` to be notified of secure fields state changes.
```
  vgsForm.addOnFieldStateChangeListener(new OnFieldStateChangeListener() {
            @Override
            public void onStateChange(FieldState state) {
                //  your code
            }
        });
```

## Handle service response
```
  vgsForm.setOnResponseListener(new VgsCollectResponseListener() {
            @Override
            public void onResponse(@org.jetbrains.annotations.Nullable VGSResponse response) {
                //  your code
            }
        });
```

## End session
```
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vgsForm.onDestroy();
    }
 ```

