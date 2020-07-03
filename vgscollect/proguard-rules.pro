
-keepattributes SourceFile,LineNumberTable

-renamesourcefileattribute SourceFile

-keep, allowobfuscation public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keep public class com.verygoodsecurity.vgscollect.view.* {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keep public class com.verygoodsecurity.vgscollect.view.material.* {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keep, allowobfuscation public class * extends com.google.android.material.textfield.TextInputLayout {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keep public class com.verygoodsecurity.vgscollect.widget.** {
    public private protected *;
}
-keep, allowobfuscation public class com.verygoodsecurity.vgscollect.core.** {
    public private protected *;
}

-keep public class com.verygoodsecurity.vgscollect.app.** {
    public private protected *;
}

-keep public class com.verygoodsecurity.vgscollect.view.card.CardBrand  { *; }
-keep public enum com.verygoodsecurity.vgscollect.view.card.CardType  { *; }
-keep public enum com.verygoodsecurity.vgscollect.view.card.FieldType  { *; }
-keep public enum com.verygoodsecurity.vgscollect.view.date.DatePickerMode { *; }

-keep, allowobfuscation public interface com.verygoodsecurity.vgscollect.core.storage.OnVgsViewStateChangeListener { *; }
-keep, allowobfuscation public class com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper { *; }

-keep public interface com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener { *; }

-keep public class com.verygoodsecurity.vgscollect.core.model.network.VGSError* { *; }
-keep public class com.verygoodsecurity.vgscollect.core.model.network.VGSRequest$* { *; }
-keep public interface com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider { *; }
-keep public class com.verygoodsecurity.vgscollect.core.model.state.FileState  { *; }
-keep public class * extends com.verygoodsecurity.vgscollect.core.model.state.FieldState { *; }
-keep public class * extends com.verygoodsecurity.vgscollect.core.model.network.VGSResponse { *; }
-keep class com.verygoodsecurity.vgscollect.core.* {
    public void addOnResponseListeners(com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener);
    public void onDestroy();
    public List<FieldState> getAllStates();
    *;
}


# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform