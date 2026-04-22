[//]: # (Prompt: in the Coding Style section, add bullet list of industry standard practices when dealing with creating kotlin files, naming kotlin files, naming variables, naming constants, naming function/method parameters etc)
# AGENTS.md

## Kotlin Conventions

### File Naming
- Name files using **UpperCamelCase** matching the primary class/object they contain (e.g., `UserRepository.kt`, `MovieDetailScreen.kt`).
- When a file contains only top-level extension functions or utilities with no dominant class, use **lowerCamelCase** or a descriptive **noun phrase** in UpperCamelCase (e.g., `StringExtensions.kt`, `FlowUtils.kt`).
- One top-level class per file; multiple closely related declarations (e.g., a sealed class and its subclasses) may share a file.
- Test files mirror the class under test with a `Test` suffix (e.g., `MovieRepositoryImplTest.kt`).
- Avoid generic names like `Utils.kt`, `Helpers.kt`, or `Common.kt`; prefer intention-revealing names.

### Class & Object Naming
- Classes and objects: **UpperCamelCase** (e.g., `AuthTokenManager`, `MovieRepositoryImpl`).
- Interfaces: **UpperCamelCase**, no `I` prefix unless disambiguating from an existing concrete class (e.g., `MovieRepository`, not `IMovieRepository`).
- Sealed classes/interfaces: name the parent as a noun/state and subclasses as specific states (e.g., `UiState.Loading`, `UiState.Success`, `UiState.Error`).
- Enum entries: **SCREAMING_SNAKE_CASE** (e.g., `HTTP_ERROR`, `NOT_FOUND`).
- Type aliases: **UpperCamelCase** (e.g., `typealias MovieId = Int`).

### Function & Method Naming
- Functions: **lowerCamelCase**, verb or verb-phrase (e.g., `fetchMovies()`, `handleError()`, `mapToDomain()`).
- Suspend functions: name as actions, not reactive streams (e.g., `fetchUser()`, not `userFlow()`).
- Boolean-returning functions: prefix with `is`, `has`, `can`, or `should` (e.g., `isLoggedIn()`, `hasPermission()`).
- Extension functions: follow the same lowerCamelCase verb convention; place in a file named after the receiver type (e.g., `StringExtensions.kt`).
- Factory functions that return a type should be named after the type (e.g., `fun MovieUiModel(...): MovieUiModel`).

### Parameter Naming
- Parameters: **lowerCamelCase**, descriptive nouns or noun phrases (e.g., `userId: Int`, `onSuccess: (Movie) -> Unit`).
- Lambda parameters: use descriptive names over single letters; single-argument lambdas may use `it` only when context is unambiguous.
- Trailing lambda parameters intended as callbacks: prefix with `on` (e.g., `onClick`, `onError`, `onRetry`).
- Avoid abbreviations unless universally understood (e.g., `ctx` → `context`, `msg` → `message`).
- Default parameter values should be used to clarify intent and reduce overloads, with names that reflect their purpose (e.g., `fun fetchMovies(page: Int = 1, pageSize: Int = DEFAULT_PAGE_SIZE)`) if parameters are more than 2.

### Variable Naming
- Local variables and properties: **lowerCamelCase** (e.g., `movieList`, `currentPage`).
- Prefer `val` over `var`; justify every `var` with a comment if mutability is non-obvious.
- Boolean properties: prefix with `is`, `has`, `can`, or `should` (e.g., `isLoading`, `hasError`).
- Backing properties for `StateFlow`/`MutableStateFlow`: prefix private mutable with `_` (e.g., `_uiState` / `uiState`).
- Avoid single-letter names except for well-known loop indices (`i`, `j`) or mathematical contexts.

### Constant Naming
- Top-level or `companion object` compile-time constants (`const val`): **SCREAMING_SNAKE_CASE** (e.g., `const val BASE_URL = "..."`, `const val MAX_PAGE_SIZE = 20`).
- Runtime `val` properties that are semantically constant but not `const`: **lowerCamelCase** is acceptable; prefer SCREAMING_SNAKE_CASE inside `object` declarations used as namespaces.
- Group related constants inside a dedicated `object` rather than scattering them as top-level declarations (e.g., `object NetworkConfig { const val TIMEOUT_MS = 30_000L }`).
- Avoid magic numbers and strings inline; always extract to a named constant with a meaningful name.

### Package Naming
- All lowercase, dot-separated, reverse-domain style (e.g., `com.multiplatform.themoviedatabase.feature.landing`).
- No underscores or camelCase in package names.
- Structure packages by **feature** first, then by layer (e.g., `…landing.ui`, `…landing.states`, `…landing.data`).

