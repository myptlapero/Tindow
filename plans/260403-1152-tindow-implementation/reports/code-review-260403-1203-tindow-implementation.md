# Code Review: Tindow Android Implementation

**Score: 7/10** | Hackathon context | 18 files reviewed | ~1100 LOC

## Overall Assessment

Solid hackathon implementation. Clean architecture (data/domain/ui), good use of StateFlow, proper Compose patterns. Several bugs in game logic that will cause runtime issues in duo mode and edge cases with empty survivor lists.

## Critical Issues

### BUG: MutableList in data class breaks StateFlow updates
**File:** `GameSession.kt:17`
```kotlin
val currentRoundSurvivors: MutableList<Option> = mutableListOf()
```
`StateFlow.update` uses structural equality. Mutating the same `MutableList` reference means `.copy()` produces a new `GameSession` pointing to the same list object, so `StateFlow` may skip emissions (old == new). The ViewModel already works around this by creating `newSurvivors` as a new list in `swipe()`, but in `startGame()` and `switchToPlayer2()` a fresh `mutableListOf()` is assigned, so it partially works. Still, using `MutableList` in a data class is a correctness hazard.

**Fix:** Change to `val currentRoundSurvivors: List<Option> = emptyList()` and build new lists in the ViewModel.

### BUG: Duo mode only runs 1 round per player
**File:** `GameViewModel.kt:81-101`
`onRoundComplete` for duo mode immediately saves survivors after the first round completes for each player and marks P2 as finished. There is no multi-round tournament for duo -- P1 swipes once, P2 swipes once, done. This may be intentional (each player filters once) but contradicts the solo mode multi-round design. If a player keeps 8 out of 10 options, those 8 all become "survivors" with no further filtering.

**Impact:** Duo results are less meaningful with many options. Consider: add multi-round support for duo, or document this as a design choice.

### BUG: SwipeScreen recomposition triggers onFinished repeatedly
**File:** `SwipeScreen.kt:56-64`
```kotlin
if (isRoundDone && !showRoundCompleteDialog && !showPassPhoneScreen) {
    if (session.isFinished) {
        onFinished() // SIDE EFFECT IN COMPOSITION
    }
```
Calling `onFinished()` (a navigation lambda) during composition is a side effect. This will fire on every recomposition. Should use `LaunchedEffect` keyed on `session.isFinished`.

**Fix:**
```kotlin
LaunchedEffect(session.isFinished) {
    if (session.isFinished) onFinished()
}
```

### BUG: Swipe direction check uses wrong value
**File:** `SwipeableCardStack.kt:121`
```kotlin
onSwipe(offsetX.value > 0) // right = kept
```
After `animateTo(targetX)` completes, `offsetX.value` is the animated target (e.g., `screenWidthPx * 2` or `-screenWidthPx * 2`). The direction should be captured BEFORE animation. Currently it works because the sign is preserved, but if the spring overshoots past zero (unlikely but possible with extreme drag), the check could invert.

**Recommendation:** Capture direction before animation:
```kotlin
val keptDirection = offsetX.value > 0
offsetX.animateTo(targetX, ...)
onSwipe(keptDirection)
```

## High Priority

### No network timeout or offline handling
**File:** `RetrofitClient.kt`
Default OkHttp timeouts (10s connect, 10s read) apply but no explicit configuration. No offline check. User taps "Random Meal" with no internet and waits 10s before seeing an error.

**Fix:** Add explicit timeout + connectivity check:
```kotlin
private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()
```

### API calls on wrong dispatcher
**File:** `InputScreen.kt:169-209`
API calls use `rememberCoroutineScope()` which runs on `Main`. Retrofit suspending functions are main-safe (they switch internally), so this works but the `mapNotNull` loop (lines 195-199) makes 5 sequential network calls on the main thread's coroutine. The loop logic itself is fine since Retrofit handles dispatching, but error handling wraps the whole batch -- one failure loses all fetched meals.

**Fix:** Fetch in parallel with `async` and collect results individually.

### Solo mode: user can reject ALL options
If user swipes left on every card, `currentRoundSurvivors` is empty. `isTournamentComplete(emptyList())` returns `true` (size <= 1). `getWinner(emptyList())` returns `null`. `ResultScreen.SoloResult` receives empty list -- shows "Your Top Picks!" with 0 items. Not a crash but a confusing UX.

**Fix:** Add guard in `SwipeScreen` when survivors = 0, show "No options survived" message or force-keep at least one.

## Medium Priority

### ViewModel scoping risk
**File:** `TindowNavGraph.kt:18`
`viewModel: GameViewModel = viewModel()` creates the ViewModel scoped to the NavGraph's `ViewModelStoreOwner`. Since `TindowNavGraph` is called from `MainActivity.ContentView`, this is effectively activity-scoped, which is correct. However, if someone refactors to nested nav graphs, the ViewModel scope could fragment. Consider using `viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!)` explicitly or using `hiltViewModel()` in the future.

### Duplicate random meals not fully deduplicated
**File:** `InputScreen.kt:195-203`
The x5 fetch deduplicates against existing options but not against the batch itself. TheMealDB random endpoint can return the same meal twice in 5 calls. Two identical meals in the batch both pass the `existingIds` filter.

**Fix:** Add `.distinctBy { it.idMeal }` before `.map { it.toOption() }`.

### `generateAdapter = false` on Moshi DTOs
**File:** `MealDto.kt:8,14`
`@JsonClass(generateAdapter = false)` disables codegen, falling back to reflection adapter (via `KotlinJsonAdapterFactory`). This is slower and uses more memory. For a hackathon this is fine, but for production switch to `generateAdapter = true` and add `kapt`/`ksp` for Moshi codegen.

### Image URI persistence
Local image URIs from `PickVisualMedia` are temporary. If the process is killed and restored, the URI may no longer be accessible. For a hackathon, acceptable.

## Low Priority

- `findClosestOptions` scoring is simplistic -- items in both lists get score 2, but these are already "matches" handled by `findMatches`. The fallback only shows items from one player's survivors.
- No back-press handling on SwipeScreen -- user can navigate back mid-tournament, leaving session in inconsistent state.
- `onPlayAgain` and `onHome` in `ResultScreen` do identical things (reset + navigate home).
- `BaseComposeActivity` hides nav bar on every resume/focus change which may conflict with edge-to-edge.

## Positive Observations

- Clean separation: data/domain/ui layers, pure TournamentEngine
- Proper StateFlow usage in ViewModel (mostly)
- Good Compose patterns: `collectAsState`, `key()` on lists, proper modifiers
- Sensible dependencies, no bloat, version catalog well-organized
- No hardcoded API keys or secrets
- INTERNET permission only -- minimal attack surface
- Swipe gesture implementation is well-tuned (spring physics, rotation, threshold)

## Recommended Actions (Priority Order)

1. **Fix** MutableList in GameSession -- change to immutable `List<Option>`
2. **Fix** side effect in SwipeScreen composition -- use `LaunchedEffect`
3. **Fix** empty survivors edge case in solo mode
4. **Add** network timeout configuration
5. **Fix** duplicate meal deduplication in batch fetch
6. **Consider** multi-round support for duo mode

## Metrics

| Metric | Value |
|--------|-------|
| Files | 18 Kotlin + 3 config |
| LOC | ~1100 |
| Test Coverage | 0% (no tests yet) |
| Linting Issues | Not run (no lint config) |
| Security | Clean -- no secrets, minimal permissions |

## Unresolved Questions

- Is duo mode intentionally single-round? If so, document it.
- Should the app handle process death/restoration? (ViewModel state is lost)
- Will additional API sources (PlaceAPI, AI) be added? If so, RetrofitClient needs refactoring to support multiple base URLs.
