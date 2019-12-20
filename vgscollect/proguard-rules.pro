
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

-keep public class com.verygoodsecurity.vgscollect.view.card.CustomCardBrand  { *; }
-keep public enum com.verygoodsecurity.vgscollect.view.card.CardType  { *; }
-keep public enum com.verygoodsecurity.vgscollect.view.card.FieldType  { *; }

-keep public interface  com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener { *; }
-keep public interface com.verygoodsecurity.vgscollect.core.storage.VgsStore { *; }
-keep public class * extends com.verygoodsecurity.vgscollect.core.model.state.FieldState { *; }
-keep public class * extends com.verygoodsecurity.vgscollect.core.model.VGSResponse { *; }
-keep class com.verygoodsecurity.vgscollect.core.* {
    public void addOnResponseListeners(com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener);
    public void onDestroy();
    public List<FieldState> getAllStates();
    *;
}