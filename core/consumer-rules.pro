# Proguard rules for core module
-keep class com.mafrilearth.pokedex.core.data.source.remote.response.** { *; }
-keep class com.mafrilearth.pokedex.core.data.source.local.entity.** { *; }
-keep class com.mafrilearth.pokedex.core.domain.** { *; }
-keep class com.mafrilearth.pokedex.core.di.** { *; }
-keep class com.mafrilearth.pokedex.core.utils.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# SQLCipher
-keep class net.zetetic.database.sqlcipher.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn java.lang.invoke.StringConcatFactory  
 