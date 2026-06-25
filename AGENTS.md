# JavaQuiz — Android (Jetpack Compose + Appwrite)

## Stack
- **Language:** Kotlin 2.2.10, Java source/target 11
- **UI:** Jetpack Compose (Material 3), Navigation Compose, ViewModel + State
- **Build:** AGP 9.2.1, Gradle version catalog (`gradle/libs.versions.toml`), Compose BOM 2026.02.01
- **Backend:** Appwrite SDK 25.0.0 (endpoint: `https://nyc.cloud.appwrite.io/v1`, project: `javaquiz`)
- **Min/Target SDK:** 26 / 36

## Architecture
- Single-activity (`MainActivity`), single `NavHost` in `NavGraph.kt`, `enableEdgeToEdge()` called in `MainActivity.onCreate`
- MVVM: `*ViewModel` classes hold UI state via `mutableStateOf` / `mutableIntStateOf`
- Two `ViewModel` instances created at `NavGraph` level and shared: `AuthViewModel` and `QuizViewModel`. All other ViewModels (`HomeViewModel`, `LeaderboardViewModel`, `HistoryViewModel`, `ProfileViewModel`) created per-screen via default parameter `viewModel()`
- Data layer: `AppwriteClient` (thread-safe singleton with double-checked locking), `AuthRepository` (class), `QuizRepository` (object)
- Models: `Question`, `Category`, `QuizHistory` under `data.model`. `Question.kt` also defines `QuizQuestion` (UI-layer model without `correctAnswer`/`categoryId`).
- `QuizRepository` wraps all suspend functions in `withContext(Dispatchers.IO)`. `AuthRepository` does **not** — it calls Appwrite client directly on whatever dispatcher the caller provides.
- `AppwriteClient` exposes `realtime` service (`io.appwrite.services.Realtime`) used by `LeaderboardViewModel` for live leaderboard updates.

## Navigation (sealed class `Screen` in `ui/navigation/Screen.kt`)
| Route | Purpose |
|---|---|
| `login` | Login |
| `register` | Register |
| `home` | Home (categories + stats) |
| `quiz/{categoryName}` | Quiz session (param is Appwrite category document ID) |
| `result/{score}/{total}/{timeUsed}` | Score summary (3 params) |
| `review` | Answer review/discussion |
| `leaderboard` | Leaderboard |
| `history` | Quiz history |
| `profile` | Profile |

All routes wired in `NavGraph`. Bottom nav bar is shared `AppBottomNavigationBar` used across home/leaderboard/history/profile screens.

## Known bugs (fixed)
- **2026-06-25:** Quiz results not saved for new users. Root cause: `saveProgress()` called `getCurrentUser()` (could return null on session) inside a fire-and-forget `viewModelScope.launch`, so `isFinished=true` fired before save completed. Fix: store `userId`/`userName` at `loadQuestions()` time, make `saveProgress()` a `suspend` function, and await it before setting `isFinished`.

## Key conventions
- `AppwriteClient.init(this)` called in `MainActivity.onCreate` **before** any other Appwrite usage
- Appwrite collection IDs: `categories`, `questions`, `quiz_histories` + bucket `assets` + database `javaquiz` — all hardcoded in `AppwriteClient` companion
- Questions store `correct_answer` as "A"/"B"/"C"/"D" string mapped to 0–3 index in `QuizRepository`
- `AuthViewModel` reuses `_registrationSuccess` for both register and login navigation flow
- `QuizRepository` uses `io.appwrite.Query.equal()` / `.limit()` / `.orderDesc()` (fully qualified, not imported)
- `AuthRepository()` instantiated inline in `LeaderboardViewModel` (not as a field, unlike other files)
- `ButtonDefaults.outlinedButtonBorder` is deprecated — use `BorderStroke(1.dp, color)` instead

## Build & test
```powershell
./gradlew assembleDebug        # Debug APK
./gradlew testDebugUnitTest    # JVM unit tests
./gradlew connectedAndroidTest # Instrumented tests (emulator/device)
./gradlew clean assembleDebug  # Clean build
```
- No CI pipeline. Tests are placeholder stubs only (`ExampleUnitTest`, `ExampleInstrumentedTest`).

## Gotchas
- **JDK 21 toolchain:** Gradle auto-downloads JDK 21 via `foojay-resolver-convention` in `settings.gradle.kts`. If the download fails, extract the cached zip in `%USERPROFILE%\.gradle\jdks\` manually
- **UI design references:** `stitch_modern_app_interface_design/` contains HTML/CSS mockups for every screen. Refer to the corresponding subdirectory (e.g. `login_simple_concept/`) when updating UI
