
-keepattributes SourceFile,LineNumberTable

-renamesourcefileattribute SourceFile


-keep, allowobfuscation public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keep public class com.soverypref.collectpref.view.* {
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
-keep public class com.soverypref.collectpref.widget.** {
    public private protected *;
}
-keep, allowobfuscation public class com.soverypref.collectpref.core.** {
    public private protected *;
}

-keep public class * extends com.soverypref.collectpref.view.text.validation.card.VGSTextInputType  { *; }
-keep public interface  com.soverypref.collectpref.core.storage.OnFieldStateChangeListener { *; }
-keep public interface com.soverypref.collectpref.core.storage.VgsStore { *; }
-keep public class * extends com.soverypref.collectpref.core.model.state.FieldState { *; }
-keep public class * extends com.soverypref.collectpref.core.model.VGSResponse { *; }
-keep class com.soverypref.collectpref.core.* {
    public void addOnResponseListeners(com.soverypref.collectpref.core.VgsCollectResponseListener);
    public void onDestroy();
    public List<FieldState> getAllStates();
    *;
}
