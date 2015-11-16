# Proguard rules that are applied to your test apk/code.
-ignorewarnings
#-applymapping mapping.txt

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter

# Uncomment this if you use Mockito
-dontwarn org.mockito.**

-dontwarn org.objenesis.**
