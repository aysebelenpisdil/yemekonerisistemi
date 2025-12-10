# ============================================
# Yemek Öneri Sistemi - ProGuard Rules
# ============================================

# --- RETROFIT ---
# Retrofit does reflection on generic parameters
-keepattributes Signature
-keepattributes Exceptions

# Retain service method parameters
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations
-dontwarn javax.annotation.**

# --- OKHTTP ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# --- GSON ---
# Gson uses generic type information stored in a class file when working with fields
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Application classes that will be serialized/deserialized
-keep class com.yemekonerisistemi.app.models.** { *; }
-keep class com.yemekonerisistemi.app.api.** { *; }

# Prevent proguard from stripping interface information
-keep,allowobfuscation interface com.yemekonerisistemi.app.api.ApiService

# --- GLIDE ---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# --- COROUTINES ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- KOTLIN ---
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# --- MODELS ---
# Keep all model classes
-keep class com.yemekonerisistemi.app.models.Recipe { *; }
-keep class com.yemekonerisistemi.app.models.Ingredient { *; }
-keep class com.yemekonerisistemi.app.models.InventoryItem { *; }
-keep class com.yemekonerisistemi.app.models.Category { *; }

# --- API RESPONSES ---
-keep class com.yemekonerisistemi.app.api.IngredientSearchResponse { *; }
-keep class com.yemekonerisistemi.app.api.IngredientDTO { *; }
-keep class com.yemekonerisistemi.app.api.RecipeRecommendationRequest { *; }
-keep class com.yemekonerisistemi.app.api.RecipeRecommendationResponse { *; }
-keep class com.yemekonerisistemi.app.api.SemanticSearchRequest { *; }
-keep class com.yemekonerisistemi.app.api.SemanticSearchResponse { *; }

# --- ENUM ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
