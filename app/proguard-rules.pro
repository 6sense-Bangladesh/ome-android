# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

#GSON SPECIF
# For using GSON @Expose annotation
-keepattributes Signature, *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * { @com.google.gson.annotations.SerializedName <fields>;}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep, allowobfuscation, allowshrinking class com.google.gson.reflect.TypeToken
-keep, allowobfuscation, allowshrinking class * extends com.google.gson.reflect.TypeToken
#GSON SPECIF

#FIREBASE SPECIFIC
-keepclassmembers, allowoptimization class com.google.firebase.** { <init>(); }
-keepclassmembers, allowoptimization class com.chuckerteam.chucker.internal.data.room.ChuckerDatabase_Impl { <init>(); }
#FIREBASE SPECIFIC

#AWS Mobile Auth classes
-dontwarn com.amazonaws.mobile.**

# Keep Google Guava classes
-keep class com.google.common.reflect.**{*;}

-keep class org.bouncycastle.** { *; }
-keepclassmembers class org.bouncycastle.** { *; }
