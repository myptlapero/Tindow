# Phase 3: Navigation & Screen Scaffolds

## Context
- [plan.md](plan.md) | Depends on: Phase 1, 2

## Overview
- **Priority:** P0
- **Status:** complete
- **Effort:** 30 min

## Requirements
- Set up Compose Navigation with 4 routes
- Create empty screen scaffolds for each
- Modify MainActivity to host NavGraph
- Shared ViewModel scoped to nav graph for game session state

## Related Code Files
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/navigation/TindowNavGraph.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/navigation/TindowRoute.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/home/HomeScreen.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/input/InputScreen.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/swipe/SwipeScreen.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/result/ResultScreen.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/GameViewModel.kt`
- **Modify:** `app/src/main/java/com/vjpro/tindow/MainActivity.kt`

## Implementation Steps

### 1. Define routes
```kotlin
// ui/navigation/TindowRoute.kt
sealed class TindowRoute(val route: String) {
    object Home : TindowRoute("home")
    object Input : TindowRoute("input")
    object Swipe : TindowRoute("swipe")
    object Result : TindowRoute("result")
}
```

### 2. GameViewModel — shared across screens
```kotlin
// ui/GameViewModel.kt
class GameViewModel : ViewModel() {
    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()

    private val engine = TournamentEngine()

    fun setMode(mode: GameMode) { ... }
    fun setOptionCount(count: Int) { ... }
    fun addOption(option: Option) { ... }
    fun addOptions(options: List<Option>) { ... }
    fun swipe(option: Option, kept: Boolean) { ... }
    fun nextRound() { ... }
    fun switchPlayer() { ... }  // duo mode
    fun reset() { ... }
}
```

### 3. NavGraph
```kotlin
// ui/navigation/TindowNavGraph.kt
@Composable
fun TindowNavGraph(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController, startDestination = TindowRoute.Home.route) {
        composable(TindowRoute.Home.route) { HomeScreen(navController, viewModel) }
        composable(TindowRoute.Input.route) { InputScreen(navController, viewModel) }
        composable(TindowRoute.Swipe.route) { SwipeScreen(navController, viewModel) }
        composable(TindowRoute.Result.route) { ResultScreen(navController, viewModel) }
    }
}
```

### 4. Update MainActivity
Replace current Scaffold content with NavGraph. ViewModel created at activity level, passed into NavGraph.

### 5. Screen scaffolds
Each screen: basic `Column` with placeholder text + nav buttons for testing.

## Todo
- [x] Create route definitions
- [x] Create GameViewModel with session state
- [x] Create NavGraph composable
- [x] Create 4 screen scaffolds with placeholder content
- [x] Update MainActivity to use NavGraph
- [x] Verify navigation flow: Home → Input → Swipe → Result

## Success Criteria
- Can navigate through all 4 screens
- GameViewModel state shared across screens
- Back navigation works correctly
