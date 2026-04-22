[//]: # (Prompt: analyze the codebase, generate a proper markdown of the directory and place in the copilot-instructions.md)
# TheMovieDatabase — KMM Project Architecture Overview

> **Project name:** `TheMovieDatabase` | **Root package:** `com.multiplatform.*`
> A Kotlin Multiplatform Mobile (KMP) app targeting **Android**, **iOS** (Simulator Arm64), and **Desktop JVM**, built around a clean, layered architecture with MVI state management and Compose Multiplatform UI.

---

## 1. Repository Layout

```
tmdbkmp/
├── build.gradle.kts              # Root build — plugin declarations only (apply false)
├── settings.gradle.kts           # Module includes: composeApp, utility, domain, data, di
├── gradle/
│   └── libs.versions.toml        # Version catalog (single source of truth for deps)
├── config/
│   ├── detekt.yml                # Detekt static-analysis rules
│   └── baseline.xml              # Detekt issue baseline
├── composeApp/                   # UI + presentation layer (Android app + KMP library)
├── domain/                       # Business entities & repository contracts
├── data/                         # Network data sources & repository implementations
├── di/                           # Dependency injection scaffolding (stub/WIP)
├── utility/                      # Cross-cutting utilities, constants, Compose helpers
└── iosApp/                       # Xcode project — thin Swift host for the KMP framework
```

---

## 2. Module-by-Module Breakdown

### 2.1 `composeApp` — Presentation / App Shell

**Gradle plugins:** `kotlinMultiplatform`, `androidApplication`, `composeMultiplatform`, `composeCompiler`, `composeHotReload`, `detekt`, `kotlinx.serialization`

**Android application ID:** `com.multiplatform.themoviedatabase` | **minSdk:** 24 | **targetSdk:** 36 | **JVM target:** 17

**Desktop entry point:** `com.multiplatform.themoviedatabase.MainKt` (distributable as `.dmg`, `.msi`, `.deb`)

The presentation layer. Contains all Compose Multiplatform UI screens (`Splash`, `Landing`, `Detail`), MVI components (`MviViewModel`, `Reducer`, `MiddleWare`), Nav3-based navigation, Material 3 theming, and platform entry points for Android, iOS, and Desktop JVM.

---

### 2.2 `domain` — Business Logic Contracts

**Gradle plugins:** `kotlinMultiplatform`, `androidKotlinMultiplatformLibrary`, `androidLint`, `kotlinx.serialization` | **iOS framework:** `domainKit`

Pure abstraction layer. Defines domain entities (`BaseMedia`, `Movie`, `MovieData`) and the `MovieRepository<T>` contract. No platform-specific implementations.

---

### 2.3 `data` — Network / Data Sources

**Gradle plugins:** `kotlinMultiplatform`, `androidKotlinMultiplatformLibrary`, `androidLint`, `kotlinx.serialization` | **iOS framework:** `dataKit`

Infrastructure layer. Implements `MovieRepository` using Ktor (`CIO`/`OkHttp`/`Darwin` engines), defines network DTOs and mappers, and provides the platform-specific API token via `expect/actual`. Includes unit tests with a Ktor mock engine.

---

### 2.4 `di` — Dependency Injection (Stub / WIP)

**Gradle plugins:** `kotlinMultiplatform`, `androidKotlinMultiplatformLibrary`, `androidLint` | **iOS framework:** `diKit`

> ⚠️ Scaffold module — no bindings wired yet. Intended to replace manual construction of `MovieRepositoryImpl` and `ITmdbHttpClient` in `TheMovieDatabaseApp.kt`.

---

### 2.5 `utility` — Cross-Cutting Helpers

**Gradle plugins:** `kotlinMultiplatform`, `androidKotlinMultiplatformLibrary`, `androidLint` | **iOS framework:** `utilityKit`

Leaf module with no internal dependencies. Provides shared constants (`INITIAL_PAGE`), TMDB URL/endpoint definitions, Kotlin extension functions, and Compose layout helpers used across all other modules.

---

### 2.6 `iosApp` — iOS Host (Xcode Project)

Native Swift shell that embeds the `ComposeApp` KMP framework.

| File | Purpose |
|---|---|
| `iOSApp.swift` | SwiftUI `@main` app entry point |
| `ContentView.swift` | Wraps `MainViewControllerKt.MainViewController()` inside a `UIViewControllerRepresentable`; calls `.ignoresSafeArea()` |
| `Info.plist` | iOS app metadata |
| `Assets.xcassets/` | App icon (1024 px), accent color |

---

## 3. Tech Stack

| Category | Technology | Version |
|---|---|---|
| **Language** | Kotlin | 2.3.20 |
| **UI** | Compose Multiplatform | 1.10.3 |
| **UI (iOS host)** | SwiftUI + UIKit | system |
| **Navigation** | JetBrains Navigation3 (`navigation3-ui`) | 1.0.0-alpha06 |
| **HTTP client** | Ktor (`CIO` / `OkHttp` / `Darwin`) | 3.4.2 |
| **Serialization** | `kotlinx.serialization-json` | 1.11.0 |
| **Concurrency** | `kotlinx.coroutines` | 1.10.2 |
| **Image loading** | Landscapist (`landscapist-image`, `landscapist-placeholder`) | 2.9.6 |
| **Lifecycle / ViewModel** | JetBrains Lifecycle + `lifecycle-viewmodel-navigation3` | 2.10.0 |
| **Build** | Gradle + Version Catalog (`libs.versions.toml`) | AGP 8.13.2 |
| **Static analysis** | Detekt | 1.23.8 |
| **Hot reload (desktop)** | Compose Hot Reload | 1.0.0 |

---

## 4. Architecture Pattern — MVI + Clean Architecture

```
┌────────────────────────────────────────────────────────────────┐
│  composeApp  (Presentation)                                    │
│  ┌──────────┐   dispatch(Intent)   ┌──────────┐               │
│  │  Screen  │──────────────────────▶ ViewModel│               │
│  │(@Compose)│◀─────── state ───────│(MviVM)   │               │
│  └──────────┘                      └────┬─────┘               │
│                                         │ Reducer.reduce(S,I) │
│                                    ┌────▼─────┐               │
│                                    │ Reducer  │               │
│                                    └──────────┘               │
└─────────────────────────────┬──────────────────────────────────┘
                              │ calls
┌─────────────────────────────▼──────────────────────────────────┐
│  domain  (Contracts)                                           │
│  MovieRepository<T>  ·  BaseMedia  ·  Movie  ·  MovieData      │
└─────────────────────────────┬──────────────────────────────────┘
                              │ implements
┌─────────────────────────────▼──────────────────────────────────┐
│  data  (Infrastructure)                                        │
│  MovieRepositoryImpl  →  TmdbHttpClient (Ktor)                 │
│  DTOs (Movie,TvShow,BaseMedia)  →  Mappers  →  Domain entities │
└────────────────────────────────────────────────────────────────┘
          ▲ all modules depend on ▼
┌─────────────────────────────────────────────────────────────────┐
│  utility   Endpoint · Constants · Extensions · ComposeHelpers   │
└─────────────────────────────────────────────────────────────────┘
```

**Key MVI mechanics:**
- `MviViewModel<S: State, I: Intent>` holds a `MutableSharedFlow<I>` action buffer and mutates `state` (Compose `mutableStateOf`) through a `Reducer.reduce(state, intent): S` call — fully coroutine-driven.
- Each screen owns a `{Screen}State`, `{Screen}Intent`, and `{Screen}Reducer` triple under its `states/` sub-package.
- `PaginationManager<T, K>` is a generic, key-based pager injected into `LandingViewModel` to append pages.
- `MiddleWare<S,I>` provides a side-effect hook that can re-dispatch intents without owning state.

---

## 5. Shared Source-Set Matrix

| Source set | `composeApp` | `data` | `domain` | `di` | `utility` |
|---|:---:|:---:|:---:|:---:|:---:|
| `commonMain` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `androidMain` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `iosMain` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `jvmMain` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `nativeMain` | — | ✅ | — | — | — |
| `commonTest` | ✅ | ✅ | — | — | — |
| `jvmTest` | — | ✅ | — | — | — |
| `androidHostTest` | — | ✅ | ✅ | ✅ | ✅ |
| `androidDeviceTest` | — | ✅ | ✅ | ✅ | ✅ |

> `data` has the widest target matrix, including a dedicated `nativeMain` source set for the `tkn()` `actual` declaration on iOS native targets (`iosX64`, `iosArm64`, `iosSimulatorArm64`).

---

## 6. Module Dependency Graph

```
composeApp
  ├── domain
  ├── data
  │    ├── domain
  │    └── utility
  ├── utility
  └── (di — planned, not yet wired)

domain
  └── utility

di
  └── (no module deps yet)

utility
  └── (leaf — no internal deps)
```

---

## 7. Known TODOs & Improvement Areas

| Location | Note |
|---|---|
| `TheMovieDatabaseApp.kt` | Replace manual `MovieRepositoryImpl` + `ITmdbHttpClient` construction with proper DI via the `di` module |
| `composeApp/build.gradle.kts` | Direct `data` module dependency should be removed once DI is wired |
| `PaginationManager` | Planned refactor to integrate natively with the MVI Reducer pipeline |
| `TmdbHttpClient.kt` | Ktor `Logging` plugin noted as not working correctly; fix deferred |
| `di` module | Entire module is a scaffold — no bindings or injection framework configured yet |

