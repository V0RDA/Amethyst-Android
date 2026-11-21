package ru.playvorda.engine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.Surface;

import net.kdt.pojavlaunch.Architecture;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.tasks.AsyncAssetManager;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.lifecycle.ContextExecutor;

import com.oracle.dalvik.VMLauncher;

import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Узкий API движка Amethyst-Android (LGPL-3.0) для лаунчера VORDA. Изменённая часть форка VORDA —
 * исходники открыты (github.com/V0RDA/vorda-engine-android). Лаунчер VORDA вызывает только этот класс.
 *
 * Отличия от upstream (требования аудита безопасности VORDA, A-17):
 *  - аргументы JVM собирает только этот класс из констант движка и списка, который лаунчер уже проверил;
 *    пользовательских «своих аргументов», arguments.jvm из манифестов и агента arc_dns_injector
 *    (он направляет запросы плащей на сторонний адрес) нет; единственный агент — MioLibPatcher из APK;
 *  - JAVA_TOOL_OPTIONS / _JAVA_OPTIONS / JDK_JAVA_OPTIONS сбрасываются перед запуском;
 *  - все пути — внутри папки приложения (не общий /sdcard/games/Amethyst).
 */
public final class VordaEngineApi {
    private static final String TAG = "VordaEngine";
    private static volatile boolean initialized;

    private VordaEngineApi() {}

    /** Пути движка внутри приложения. {@code gameRoot} — общая папка игры лаунчера (versions, libraries, assets). */
    public static synchronized void init(Application app, File gameRoot) {
        if (initialized) return;
        ContextExecutor.setApplication(app);
        Tools.initEarlyConstants(app);
        File home = new File(app.getFilesDir(), "engine");
        //noinspection ResultOfMethodCallIgnored
        home.mkdirs();
        Tools.DIR_GAME_HOME = home.getAbsolutePath();
        Tools.DIR_GAME_NEW = gameRoot.getAbsolutePath();
        Tools.DIR_HOME_VERSION = Tools.DIR_GAME_NEW + "/versions";
        Tools.DIR_HOME_LIBRARY = Tools.DIR_GAME_NEW + "/libraries";
        Tools.DIR_HOME_CRASH = Tools.DIR_GAME_NEW + "/crash-reports";
        Tools.ASSETS_PATH = Tools.DIR_GAME_NEW + "/assets";
        Tools.CTRLMAP_PATH = Tools.DIR_GAME_HOME + "/controlmap";
        Tools.CTRLDEF_FILE = Tools.CTRLMAP_PATH + "/default.json";
        Tools.DEVICE_ARCHITECTURE = Architecture.getDeviceArchitecture();
        AsyncAssetManager.unpackRuntime(app.getAssets());
        initialized = true;
    }

    /** Распаковать компоненты движка (LWJGL 3.3.3/3.4.1, MioLibPatcher, caciocavallo) — асинхронно, ждём файлы. */
    public static void prepareComponents(Context ctx, long timeoutMs) throws InterruptedException {
        AsyncAssetManager.unpackComponents(ctx);
        long until = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < until) {
            if (new File(Tools.DIR_GAME_HOME, "lwjgl3/3.3.3/lwjgl.jar").isFile()
                    && new File(Tools.DIR_DATA, "MioLibPatcher/MioLibPatcher.jar").isFile()) return;
            Thread.sleep(200);
        }
        throw new IllegalStateException("Компоненты движка не распаковались.");
    }

    /** Установить JRE из архива .tar.xz (из APK лаунчера, проверенного по SHA-256). */
    public static void installRuntime(Context ctx, String name, InputStream tarXz) throws Exception {
        MultiRTUtils.installRuntimeNamed(ctx.getApplicationInfo().nativeLibraryDir, tarXz, name);
        MultiRTUtils.postPrepare(name);
    }

    public static boolean runtimeInstalled(String name) {
        return MultiRTUtils.getRuntimeHome(name).isDirectory();
    }

    /** Classpath LWJGL движка (вместо LWJGL из манифеста Mojang): 1.21.x — 3.3.3, 26.3+ — 3.4.1. */
    public static List<String> lwjglClasspath(boolean lwjgl341) {
        String v = lwjgl341 ? "3.4.1" : "3.3.3";
        File dir = new File(Tools.DIR_GAME_HOME, "lwjgl3/" + v);
        List<String> cp = new ArrayList<>();
        cp.add(new File(dir, "lwjgl.jar").getAbsolutePath());
        cp.add(new File(dir, "lwjgl-" + v + "-merged-modules.jar").getAbsolutePath());
        File[] mods = dir.listFiles(f -> f.getName().endsWith(".jar") && !f.getName().equals("lwjgl.jar")
                && !f.getName().equals("lwjgl-" + v + "-merged-modules.jar") && !f.getName().endsWith("lwjglx.jar"));
        if (mods != null) for (File f : mods) cp.add(f.getAbsolutePath());
        Tools.iLwjglVersion = lwjgl341 ? 341 : 333;
        Tools.lwjglNativesDir = String.format(Locale.ROOT, "%s/lwjgl-%s-natives/%s", Tools.DIR_DATA, v,
                Architecture.archAsStringAndroid(Architecture.getDeviceArchitecture()));
        return cp;
    }

    /** Наши имена рендеров → имена движка. */
    static String renderer(String vorda) {
        switch (vorda) {
            case "zink": return "opengles3_desktopgl_zink_kopper";
            case "gl4es": return "opengles2";
            default: return "opengles_mobileglues";
        }
    }

    /** Окно игры: Surface из GameActivity лаунчера. */
    public static void setSurface(Surface surface, int width, int height) {
        JREUtils.setupBridgeWindow(surface);
        CallbackBridge.windowWidth = width;
        CallbackBridge.windowHeight = height;
        CallbackBridge.physicalWidth = width;
        CallbackBridge.physicalHeight = height;
        CallbackBridge.sendUpdateWindowSize(width, height);
    }

    /**
     * Запуск JVM в этом процессе (:game). {@code gameArgs} — уже проверенные лаунчером: -cp, главный класс,
     * аргументы игры. Возвращает код выхода JVM (блокирует поток до выхода из игры).
     */
    public static int launch(Activity activity, String runtimeName, String rendererName, File gameDir,
                             int heapMb, boolean mali, List<String> gameArgs) throws Throwable {
        Runtime runtime = MultiRTUtils.forceReread(runtimeName);
        String home = MultiRTUtils.getRuntimeHome(runtimeName).getAbsolutePath();
        Tools.LOCAL_RENDERER = renderer(rendererName);
        for (String bad : new String[]{"JAVA_TOOL_OPTIONS", "_JAVA_OPTIONS", "JDK_JAVA_OPTIONS"}) {
            try {
                Os.unsetenv(bad);
            } catch (ErrnoException ignored) {
                // не было — хорошо
            }
        }
        JREUtils.relocateLibPath(runtime, home);
        JREUtils.setJavaEnvironment(activity, home);
        String graphics = JREUtils.loadGraphicsLibrary();
        try {
            if (graphics != null) Os.setenv("SDL_OPENGL_LIBRARY", graphics, true);
            String egl = Os.getenv("POJAVEXEC_EGL");
            if (egl != null) Os.setenv("SDL_EGL_LIBRARY", Tools.NATIVE_LIB_DIR + "/" + egl, true);
        } catch (ErrnoException e) {
            Log.w(TAG, "SDL env", e);
        }
        List<String> args = new ArrayList<>();
        args.add("java"); // argv[0]
        args.add("-Xms" + heapMb + "M");
        args.add("-Xmx" + heapMb + "M");
        if (graphics != null) args.add("-Dorg.lwjgl.opengl.libname=" + graphics);
        args.add("-Dorg.lwjgl.freetype.libname=" + Tools.lwjglNativesDir + "/libfreetype.so");
        args.add("-Dorg.lwjgl.spvc.libname=spirv-cross-c-shared");
        args.add("-Dorg.lwjgl.system.allocator=system");
        // На Mali ActiveProcessorCount не задаём (рекомендация сообщества Amethyst, RESEARCH.md §3).
        if (!mali) args.add("-XX:ActiveProcessorCount=" + java.lang.Runtime.getRuntime().availableProcessors());
        args.add("-javaagent:" + new File(Tools.DIR_DATA, "MioLibPatcher/MioLibPatcher.jar").getAbsolutePath());
        args.addAll(gameArgs);
        JREUtils.initJavaRuntime(home);
        JREUtils.setupExitMethod(activity.getApplication());
        JREUtils.initializeHooks();
        JREUtils.chdir(gameDir.getAbsolutePath());
        Log.i(TAG, "launch " + runtimeName + " renderer=" + Tools.LOCAL_RENDERER + " sdk=" + Build.VERSION.SDK_INT);
        return VMLauncher.launchJVM(args.toArray(new String[0]));
    }

    // ---- ввод (коды клавиш GLFW, как в лаунчере) ----

    public static void cursor(float x, float y) {
        CallbackBridge.sendCursorPos(x, y);
    }

    public static void mouseButton(int button, boolean down) {
        CallbackBridge.sendMouseButton(button, down);
    }

    public static void key(int glfwKey, boolean down) {
        CallbackBridge.sendKeyPress(glfwKey, 0, 0, down);
    }

    public static void character(int codepoint) {
        CallbackBridge.sendChar((char) codepoint, 0);
    }

    public static void scroll(float dy) {
        CallbackBridge.sendScroll(0, dy);
    }

    public static boolean grabbing() {
        return CallbackBridge.isGrabbing();
    }

    public static void resize(int w, int h) {
        CallbackBridge.windowWidth = w;
        CallbackBridge.windowHeight = h;
        CallbackBridge.sendUpdateWindowSize(w, h);
    }
}
