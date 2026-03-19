# NewPipeExtractor
-keep class org.schabi.newpipe.extractor.** { *; }
-keep class org.mozilla.javascript.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.xware.** { *; }
-dontwarn org.mozilla.**
-dontwarn org.schabi.**
