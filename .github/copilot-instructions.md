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

## 7. Testing

### 7.1 Tech Stack

| Library | Role | Scope |
|---|---|---|
| `kotlin-test` | Multiplatform assertions (`assertEquals`, `assertNull`, `assertTrue`, …) | All modules — `commonTest` |
| `kotlinx-coroutines-test` | `runTest`, `StandardTestDispatcher`, `advanceUntilIdle` | `composeApp`, `data` — `commonTest` / `jvmTest` |
| `ktor-client-mock` | `MockEngine` for zero-network HTTP testing | `data` — `commonTest` |
| `kotlin-test-junit` | JUnit 4 runner bridge for JVM test targets | `data` — `jvmTest` |
| `androidx.test.ext:junit` | JUnit instrumented test runner | `data` — `androidDeviceTest` |
| `androidx.test:runner` + `core` | Android instrumentation support | `data` — `androidDeviceTest` |

### 7.2 Test Directories

| Path | Source set | What lives here |
|---|---|---|
| `composeApp/src/commonTest/` | `commonTest` | ViewModel, Reducer, and integration tests shared across all targets |
| `data/src/commonTest/` | `commonTest` | Repository and mapper unit tests using a Ktor `MockEngine` |
| `data/src/jvmTest/` | `jvmTest` | JVM-specific test runner wiring with JUnit 4 bridge |
| `data/src/androidHostTest/` | `androidHostTest` | Host-side (JVM) Android unit test stubs |
| `data/src/androidDeviceTest/` | `androidDeviceTest` | On-device instrumented test stubs |
| `domain/src/androidHostTest/` | `androidHostTest` | Host-side unit test stubs |
| `domain/src/androidDeviceTest/` | `androidDeviceTest` | On-device instrumented test stubs |
| `di/src/androidHostTest/` | `androidHostTest` | Placeholder — no bindings to test yet |
| `utility/src/androidHostTest/` | `androidHostTest` | Placeholder — utility module host-side stubs |

### 7.3 Test Coverage by Module

**`composeApp/src/commonTest/`**
- Focuses on MVI correctness. ViewModel tests validate state transitions triggered by `Intent` dispatch — covering init auto-load, success/error paths, and call-count expectations using a `StandardTestDispatcher`. Reducer tests are pure and synchronous, asserting that `reduce(state, intent)` produces the correct next state for happy path, error path, edge cases (empty list, null page), and unimplemented intent stubs.

**`data/src/commonTest/`**
- Focuses on infrastructure correctness. Repository tests verify end-to-end HTTP → mapping → domain entity flow using a `MockEngine`-backed fake client, covering page query param handling, empty/missing results, multi-page metadata, and server error propagation. Mapper tests assert domain mapping fidelity for full responses, multiple items, null inputs, and graceful null-field handling.

### 7.4 Testing Patterns

- **Fake objects over mocking frameworks** — Tests use hand-written `FakeMovieRepository` / `FakeTmdbHttpClient` implementations; no Mockk or Mockito dependency.
- **`StandardTestDispatcher` + `advanceUntilIdle()`** — ViewModel tests control coroutine execution explicitly, avoiding flaky timing.
- **`MockEngine` for HTTP** — `MovieRepositoryImplTest` never touches the network; all HTTP responses are injected via Ktor's `MockEngine`.
- **Arrange / Act / Assert** — All test bodies follow the three-section comment pattern for readability.
- **`@BeforeTest` / `@AfterTest`** — `Dispatchers.setMain` / `resetMain` in ViewModel tests ensures `Main` dispatcher is always restored.

---

## 8. Known TODOs & Improvement Areas

| Location | Note |
|---|---|
| `TheMovieDatabaseApp.kt` | Replace manual `MovieRepositoryImpl` + `ITmdbHttpClient` construction with proper DI via the `di` module |
| `composeApp/build.gradle.kts` | Direct `data` module dependency should be removed once DI is wired |
| `PaginationManager` | Planned refactor to integrate natively with the MVI Reducer pipeline |
| `TmdbHttpClient.kt` | Ktor `Logging` plugin noted as not working correctly; fix deferred |
| `di` module | Entire module is a scaffold — no bindings or injection framework configured yet |

