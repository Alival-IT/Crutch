-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}

# for crashlytics
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Logs
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int d(...);
public static int w(...);
public static int v(...);
public static int i(...);
public static int e(...);
}

-assumenosideeffects class timber.log.Timber* {
public static *** d(...);
public static *** w(...);
public static *** v(...);
public static *** i(...);
public static *** e(...);
}
