# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Jordy\AppData\Local\Android\sdk1/tools/proguard/proguard-android.txt
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
-dontwarn com.google.android.gms.**
-dontwarn com.google.common.**
-dontwarn org.apache.**
-dontwarn com.smaato.soma.**
-dontwarn io.branch.**
-dontwarn com.dropbox.**
-dontwarn com.cleveroad.audiowidget.**
-dontwarn com.daimajia.androidanimations.library.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.github.siyamed.**
-dontwarn com.zendesk.**
-dontwarn io.codetail.animation.**
-dontwarn com.mikepenz.materialdrawer.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes JavascriptInterface
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-keep class com.connectsdk.** { *; }
-keep class es.munix.multidisplaycast.** { *; }
-dontwarn com.crashlytics.**
-keep public class org.jsoup.** {
public *;
}
-keep public class pl.droidsonroids.** {
public *;
}
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}
#-keep public class java.io.** {
#public *;
#}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
  public *;
}
-keep public class knf.animeflv.JsonFactory$UrlGetter
-keep public class * implements knf.animeflv.JsonFactory$UrlGetter
-keepclassmembers class knf.animeflv.JsonFactory$UrlGetter {
    <methods>;
}
-keep class .R
-keep class **.R$* {
    <fields>;
}

