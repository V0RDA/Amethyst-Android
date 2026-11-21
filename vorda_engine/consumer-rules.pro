# Движок (LGPL-3.0) не обфусцируется: классы вызываются из нативного кода по имени, а пользователь вправе
# заменить движок своей сборкой (LGPL §4).
-keep class net.kdt.pojavlaunch.** { *; }
-keep class org.lwjgl.** { *; }
-keep class org.libsdl.** { *; }
-keep class com.oracle.dalvik.** { *; }
-keep class ru.playvorda.engine.** { *; }
