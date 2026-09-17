# Same as consumer rules
-keep class com.mafrilearth.pokedex.core.data.source.remote.response.** { *; }
-keep class com.mafrilearth.pokedex.core.data.source.local.entity.** { *; }
-keep class com.mafrilearth.pokedex.core.domain.** { *; }
-keep class com.mafrilearth.pokedex.core.di.** { *; }
-keep class com.mafrilearth.pokedex.core.utils.** { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keep class net.zetetic.database.sqlcipher.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn java.lang.invoke.StringConcatFactory  
 