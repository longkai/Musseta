# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/longkai/Development/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# fabric
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
#-printmapping mapping.txt

# timber
-keep class timber.log.Timber* {
  public static *** *(...);
}

# retrolambda
-dontwarn java.lang.invoke.*

# butterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# rxjava
-keep class rx.internal.util.unsafe.** { *; }
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

# appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# project
-keep class yuejia.liu.** { *; }
#-keep class yuejia.liu.**$Builder { *; }
#-keepclassmembers class yuejia.liu.** {
#  protected *** setupActivityComponent();
#  public void setActivityComponent(...);
#}

# misc
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn sun.misc.Unsafe
-dontwarn sun.reflect.**


#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
