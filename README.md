<h1 align="center">Angel Aura Amethyst</h1>

<img src="https://github.com/AngelAuraMC/Amethyst-Android/blob/v3_openjdk/app_pojavlauncher/src/main/assets/amethyst.png" align="left" width="130" height="130" alt="Amethyst logo">

[![Android CI](https://github.com/AngelAuraMC/Amethyst-Android/workflows/Android%20CI/badge.svg)](https://github.com/AngelAuraMC/Amethyst-Android/actions)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/AngelAuraMC/Amethyst-Android)](https://github.com/AngelAuraMC/Amethyst-Android/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/5ptqkyZxEy)

*From [Boardwalk](https://github.com/zhuowei/Boardwalk)'s ashes and [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)'s ruined reputation, here comes Amethyst!*

Amethyst is a launcher that allows you to play Minecraft: Java Edition on your Android and [iOS](https://github.com/AngelAuraMC/Amethyst-iOS) devices.

For more details, check out our [wiki](https://wiki.angelauramc.dev)!

> **VORDA fork.** This fork is the Minecraft: Java Edition engine of the [VORDA](https://playvorda.ru) launcher
> for Android. It is licensed under GNU LGPL-3.0, like upstream. All VORDA changes live in the `vorda` branch,
> see [Changes in this fork](#changes-in-this-fork-vorda) below and [VORDA.md](VORDA.md).

## Table of Contents

* [Changes in this fork (VORDA)](#changes-in-this-fork-vorda)
* [Introduction](#introduction)
* [Getting Amethyst](#getting-amethyst)
* [Building](#building)
    * [Quick Build (Recommended)](#quick-build-recommended)
    * [Detailed Build](#detailed-build)
* [Current Status](#current-status)
* [Known Issues](#known-issues)
* [FAQ](#faq)
* [Contributing](#contributing)
* [Support](#support)
* [License](#license)
* [Credits & Dependencies](#credits--dependencies)
* [Roadmap](#roadmap)

## Introduction

* Amethyst is a Minecraft: Java Edition launcher for Android and iOS based on [Boardwalk](https://github.com/zhuowei/Boardwalk) and [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)
* This launcher can launch almost all available Minecraft versions ranging from rd-132211 to 26.3-snapshot-1.
* Modding via Forge and Fabric are also supported.
* This repository contains source code for Android. For iOS/iPadOS, check out [Amethyst-iOS](https://github.com/AngelAuraMC/Amethyst-iOS).

## Getting Amethyst

You can get Amethyst via two methods:

1. **Releases:** Download the latest prebuilt app from [nightly.link](https://nightly.link/AngelAuraMC/Amethyst-Android/workflows/android/v3_openjdk/app-debug%20%28recommended%29.zip) or select an older version from our [automatic builds](https://github.com/AngelAuraMC/Amethyst-Android/actions).
2. **Build from Source:** Follow the [building instructions](#building) below.

## Building

### Pre-requisites

<details>

<summary>Preparing your build environment</summary>

1. ⚠️ Because of an upstream [issue](https://github.com/android/ndk/issues/1400#issuecomment-744620270) with ndk-build, do not clone the repository into any directory whose absolute path contains spaces. Doing so on will result in a build failure.

2. You must have a JDK installation, [Temurin](https://adoptium.net/temurin/releases) or [OpenJDK](https://openjdk.org/install/) are recommended, and a `JAVA_HOME` system environment variable configured for compiling your builds

</details>

### Quick Build (Recommended)

The easiest way to build Amethyst is to use the pre-built JREs provided by our CI.

1. Clone the repository: `git clone --recursive https://github.com/AngelAuraMC/Amethyst-Android.git`
2. Build the launcher: `./gradlew :app_pojavlauncher:assembleDebug` (Use `gradlew.bat` on Windows)

The built APK will be located in `app_pojavlauncher/build/outputs/apk/debug/`.

### Detailed Build

If you need more control over the build process, follow these steps:

1. **Java Runtime Environment (JRE):** Download the `jre8-pojav` artifact from our [CI auto builds](https://github.com/AngelAuraMC/openjdk-build-multiarch/actions), unzip and paste the contents of the downloaded zip into `app_pojavlauncher/src/main/assets/components/jre/`, create the folder if it does not exist.  This package contains pre-built JREs for all supported architectures.  If you need to build the JRE yourself, follow the instructions in the [android-openjdk-build-multiarch](https://github.com/AngelAuraMC/openjdk-build-multiarch) repository.

2. **LWJGL:** The build instructions for the custom LWJGL are available over the [LWJGL repository](https://github.com/AngelAuraMC/lwjgl3).

3. **Language List:** Because languages are auto-added by Crowdin, you need to run the language list generator before building. In the project directory, run:
   * Linux/macOS:
     ```bash
     chmod +x scripts/languagelist_updater.sh
     bash scripts/languagelist_updater.sh
     ```
   * Windows:
     ```batch
     scripts\languagelist_updater.bat
     ```

4. **Build GLFW stub:** `./gradlew :jre_lwjgl3glfw:build`

5. **Build the launcher:** `./gradlew :app_pojavlauncher:assembleDebug` (Replace `gradlew` with `gradlew.bat` on Windows).

## Current Status

* [x] OpenJDK 8 Mobile port: ARM32, ARM64, x86, x86_64
* [x] OpenJDK 17 Mobile port: ARM32, ARM64, x86, x86_64
* [x] OpenJDK 21 Mobile port: ARM32, ARM64, x86, x86_64
* [x] Headless mod installer
* [x] Mod installer with GUI
* [x] OpenGL in OpenJDK environment
* [x] OpenAL (works on most devices)
* [x] Support for Minecraft 1.12.2 and below
* [x] Support for Minecraft 1.13 and above
* [x] Support for Minecraft 1.17 (22w13a) and above
* [x] Game surface zooming
* [x] New input pipe rewritten to native code
* [x] Rewritten entire controls system
* [ ] More to come!

## Known Issues

See our [issue tracker](https://github.com/AngelAuraMC/Amethyst-Android/issues) for a list of known issues and their current status.

## FAQ

See our [wiki](https://wiki.angelauramc.dev/) for more information.

## Contributing

Contributions are welcome! We welcome any type of contribution, not only code. For example, you can help improve the [wiki](https://github.com/AngelAuraMC/angelauramc.github.io/), contribute to the [translations](https://crowdin.com/project/pojavlauncher), or submit bug reports and feature requests.

Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Support

For support, please join our [Discord server](https://discord.gg/5ptqkyZxEy).

## Changes in this fork (VORDA)

Base: upstream [AngelAuraMC/Amethyst-Android](https://github.com/AngelAuraMC/Amethyst-Android), branch `v3_openjdk`,
commit `166af61` (2026-11-12). License: GNU LGPL-3.0, unchanged. VORDA does not publish the Amethyst app from
this fork; the engine is used as a library by the VORDA Android launcher.

| Change | Where |
|---|---|
| New library module `vorda_engine`: builds the upstream engine (Java, JNI, resources, components) as an AAR without the Amethyst launcher UI and manifest | `vorda_engine/build.gradle`, `vorda_engine/src/main/AndroidManifest.xml`, `settings.gradle` |
| Narrow launcher API: app-private paths, component unpacking, JRE install, window, input, JVM launch | `vorda_engine/src/main/java/ru/playvorda/engine/VordaEngineApi.java` |
| JVM arguments are built by the engine only: no user arguments, no `arguments.jvm` from manifests, no `arc_dns_injector` agent; `JAVA_TOOL_OPTIONS` and similar variables are cleared | `VordaEngineApi.launch` |
| arm64 only; native libraries aligned to 16 KB pages (`APP_SUPPORT_FLEXIBLE_PAGE_SIZES`); GL4ES (Krypton Wrapper R0.4.5) built from source; game log written to `engine/latestlog.txt` | `vorda_engine/build.gradle`, `VordaEngineApi` |
| `ExitActivity`: optional `ExitListener` so the host launcher can show the game exit result itself | `app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/ExitActivity.java` |
| `JavaGUILauncherActivity`: `if` instead of `switch` on `R.id` (resource ids are not constants in a library module) | `app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/JavaGUILauncherActivity.java` |
| CI builds the `vorda-engine-bundle` package (Maven repo, native AARs, JRE 17/21 pinned by SHA-256) instead of the upstream APK workflow | `.github/workflows/vorda-engine.yml` (upstream `android.yml` removed) |
| Upstream signing keys removed from the fork | `app_pojavlauncher/aamc_upload.jks`, `app_pojavlauncher/debug.keystore` |

Replacing the engine (LGPL-3.0 §4): build `gradle :vorda_engine:publishReleasePublicationToMavenRepository`, put
`vorda_engine/build/repo` and the native AARs into the launcher's `android/engine/bundle/` and rebuild the launcher.
The launcher talks to the engine only through `ru.playvorda.engine.VordaEngineApi`; the engine is not obfuscated.

## License

Amethyst is licensed under [GNU LGPLv3](https://github.com/AngelAuraMC/Amethyst-Android/blob/v3_openjdk/LICENSE).

## Credits & Dependencies

* [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
* [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher): [GLGPL](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE)
* Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
* [GL4ES](https://github.com/AngelAuraMC/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).
* [MobileGlues](https://github.com/MobileGL-Dev/MobileGlues): [LGPL-2.1 License](https://github.com/MobileGL-Dev/MobileGlues/blob/dev-es/LICENSE).
* [Krypton Wrapper](https://github.com/BZLZHH/NG-GL4ES): [MIT License](https://github.com/BZLZHH/NG-GL4ES/blob/main/LICENSE)
* [ANGLE](https://chromium.googlesource.com/angle/angle): [All Rights Reserved](app_pojavlauncher/src/main/assets/licenses/ANGLE_LICENSE).
* [OpenJDK](https://github.com/AngelAuraMC/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).
* [LWJGL3](https://github.com/AngelAuraMC/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
* [LWJGLX](https://github.com/AngelAuraMC/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.
* [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
* [bhook](https://github.com/bytedance/bhook) (Used for exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
* [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
* [virglrenderer](https://github.com/AngelAuraMC/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
* [OpenAL-Soft](https://github.com/kcat/openal-soft): [GNU GPLv2](app_pojavlauncher/src/main/assets/licenses/OPENAL-SOFT_GPL2)
  * [oboe](https://github.com/google/oboe): [Apache License 2.0](app_pojavlauncher/src/main/assets/licenses/OBOE_APACHE2).
  * [pfffft](https://bitbucket.org/jpommier/pffft/src/master/): [ARR](app_pojavlauncher/src/main/assets/licenses/PFFFT_LICENSE)
* [SDL3](https://github.com/libsdl-org/SDL): [zlib License](https://github.com/libsdl-org/SDL/blob/main/LICENSE.txt)
* [sdl2-compat](https://github.com/libsdl-org/sdl2-compat): [zlib License](https://github.com/libsdl-org/sdl2-compat/blob/main/LICENSE.txt)
* Thanks to [MCHeads](https://mc-heads.net) for providing Minecraft avatars.

## Roadmap

We are currently focusing on:

* Exploring new rendering technologies.

Future plans include:

* Improving stability and performance.
* Enhancing the mod installation experience.

We welcome community feedback and suggestions for our roadmap.  Please feel free to open a feature request in our [issue tracker](https://github.com/AngelAuraMC/Amethyst-Android/issues).
