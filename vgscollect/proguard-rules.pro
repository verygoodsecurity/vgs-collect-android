#-keep,allowobfuscation public class ppp.storage.** {
#   public protected *;
#}
#-keep,allowobfuscation public class ppp.view.** {
#   public protected *;
#}
#
#-keep public class ppp.widget.** {
#      public <init>(android.content.Context);
#      public <init>(android.content.Context, android.util.AttributeSet);
#      public <init>(android.content.Context, android.util.AttributeSet, int);
#      public void set*(...);
#}
#
#-repackageclasses