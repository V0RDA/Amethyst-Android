# VORDA engine — форк Amethyst-Android

Это движок Minecraft: Java Edition для лаунчера **VORDA** на Android. Форк [Amethyst-Android](https://github.com/AngelAuraMC/Amethyst-Android) — официального преемника PojavLauncher.

**Лицензия — GNU LGPL-3.0**, как у upstream (файл `LICENSE`). Все изменения VORDA в движке публикуются здесь.

## Что изменено относительно upstream

| Изменение | Где |
|---|---|
| Модуль-библиотека `vorda_engine`. Собирает движок upstream (Java, JNI, ресурсы, компоненты) как AAR без лаунчерного интерфейса и манифеста Amethyst | `vorda_engine/build.gradle`, `vorda_engine/src/main/AndroidManifest.xml` |
| Узкий API для лаунчера: инициализация путей внутри приложения, распаковка компонентов, установка JRE, окно, ввод, запуск JVM | `vorda_engine/src/main/java/ru/playvorda/engine/VordaEngineApi.java` |
| Аргументы JVM собирает только движок. Пользовательских аргументов и `arguments.jvm` из манифестов нет, агента `arc_dns_injector` нет. `JAVA_TOOL_OPTIONS` и похожие переменные сбрасываются | `VordaEngineApi.launch` |
| Только arm64; `.so` выровнены под страницы 16 КБ (`APP_SUPPORT_FLEXIBLE_PAGE_SIZES`) | `vorda_engine/build.gradle` |
| Ключи подписи upstream (`aamc_upload.jks`, `debug.keystore`) удалены из форка | — |
| CI собирает `vorda-engine-bundle` с контрольными суммами SHA-256 | `.github/workflows/vorda-engine.yml` |

В коде upstream в `app_pojavlauncher` изменены только `ExitActivity` (необязательный `ExitListener`, чтобы лаунчер сам показывал итог игры) и `JavaGUILauncherActivity` (`if` вместо `switch` по `R.id`: в модуле-библиотеке это не константы). Приложение Amethyst из этого форка не публикуется.

## Как заменить движок своей сборкой (LGPL-3.0, §4)

1. Измени код и собери `gradle :vorda_engine:publishReleasePublicationToMavenRepository`.
2. Положи `vorda_engine/build/repo` и нужные AAR в `android/engine/bundle/` лаунчера.
3. Пересобери лаунчер.

Лаунчер подключает движок только через `ru.playvorda.engine.VordaEngineApi`.

## Сторонние компоненты

Список и лицензии — `README.md` upstream: OpenJDK (GPL-2.0 + Classpath Exception), LWJGL (BSD-3), MobileGlues (LGPL-2.1), GL4ES/Krypton (MIT), Mesa (MIT), OpenAL Soft (LGPL-2.1), SDL3 (zlib) и другие.
