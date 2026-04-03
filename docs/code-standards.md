# Code Standards — Tindow

Standards and conventions for Kotlin/Compose code in the Tindow project.

## Naming Conventions

### Files & Packages
- **Kotlin files**: PascalCase matching primary class name
- **Packages**: lowercase with domain hierarchy
  ```
  com.vjpro.tindow.ui.home
  com.vjpro.tindow.domain
  com.vjpro.tindow.data.api
  ```

### Classes & Objects
- **Classes**: PascalCase
  ```kotlin
  class GameViewModel
  data class Option
  enum class GameMode
  ```
- **Singletons & Utility Objects**: PascalCase
  ```kotlin
  object TournamentEngine
  object RetrofitClient
  ```

### Variables & Functions
- **Local variables & parameters**: camelCase
  ```kotlin
  val currentOption = options[index]
  fun swipe(kept: Boolean)
  ```
- **Constants**: UPPER_SNAKE_CASE (within objects only)
  ```kotlin
  object Constants {
      const val BASE_URL = "https://themealdb.com/"
      const val MIN_OPTIONS = 2
  }
  ```

### UI Components
- **Composable functions**: PascalCase (treated as components)
  ```kotlin
  @Composable
  fun HomeScreen()
  
  @Composable
  fun SwipeCard(option: Option)
  ```
- **Preview functions**: `Preview{ComponentName}`
  ```kotlin
  @Preview
  @Composable
  fun PreviewSwipeCard()
  ```

## Code Organization

### File Structure
1. Package declaration
2. Imports (group: stdlib → Android → Compose → project)
3. Class/Object declaration
4. Companion object (if needed)
5. Public properties
6. Public methods
7. Private methods
8. Nested classes

### ViewModel Structure
```kotlin
class GameViewModel : ViewModel() {
    // Public state
    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()
    
    // Public methods (business logic)
    fun setMode(mode: GameMode) { ... }
    fun swipe(kept: Boolean) { ... }
    
    // Private helpers
    private fun onRoundComplete(survivors: List<Option>) { ... }
}
```

### Composable Structure
```kotlin
@Composable
fun MyScreen(
    viewModel: GameViewModel = viewModel()
) {
    val session by viewModel.session.collectAsState()
    
    // Layout
    Column(modifier = Modifier.fillMaxSize()) {
        // Content
    }
}
```

## Data Classes

### Immutability
- Use `data class` for models (enables `copy()`)
- Mark properties `val` (immutable)
  ```kotlin
  data class Option(
      val id: String,
      val title: String,
      val imageUri: String? = null
  )
  ```

### Nullability
- Default to non-nullable; use `?` only when needed
  ```kotlin
  // Good
  val title: String
  val imageUri: String? = null
  
  // Avoid
  val imageUri: String = ""
  ```

## State Management

### StateFlow Pattern
- Private `MutableStateFlow`, expose immutable `StateFlow`
  ```kotlin
  private val _session = MutableStateFlow(initialValue)
  val session: StateFlow<GameSession> = _session.asStateFlow()
  ```

### State Updates
- Use `.update { }` for atomic changes (preferred)
  ```kotlin
  _session.update { it.copy(currentCardIndex = nextIndex) }
  ```
- Never mutate state; always create new objects via `copy()`

### Collecting State in Compose
```kotlin
val session by viewModel.session.collectAsState()
// or for one-shot reads:
viewModel.session.value
```

## Functions & Methods

### Pure Functions (Domain Layer)
- No side effects, no state mutation
- All logic in `TournamentEngine`
  ```kotlin
  object TournamentEngine {
      fun isTournamentComplete(survivors: List<Option>): Boolean {
          return survivors.size <= 1
      }
  }
  ```

### Suspend Functions (API/Async)
- Mark with `suspend` for network calls
  ```kotlin
  interface MealApiService {
      @GET("...")
      suspend fun getRandomMeal(): MealResponse
  }
  ```

### Function Parameters
- Max 3 parameters; use data classes for more
  ```kotlin
  // Good
  fun addOption(option: Option) { ... }
  
  // Consider data class if many params
  data class SwipeEvent(val optionId: String, val kept: Boolean)
  ```

## Compose Guidelines

### Modifier Order
```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(16.dp)
        .clip(RoundedCornerShape(8.dp))
)
```

### Preview & Theming
- Always include Material3 theme in previews
  ```kotlin
  @Preview
  @Composable
  fun PreviewMyScreen() {
      TindowTheme {
          MyScreen()
      }
  }
  ```

### Reusable Components
- Extract to separate `@Composable` functions
- Pass state via parameters (no globals)
  ```kotlin
  @Composable
  fun SwipeCard(option: Option, onSwipe: (Boolean) -> Unit)
  ```

## Error Handling

### Try-Catch for API Calls
```kotlin
try {
    val meal = mealApiService.getRandomMeal()
    // update state
} catch (e: Exception) {
    // log and notify user
    Log.e("API", "Failed to fetch meal", e)
}
```

### Validation
- Check preconditions early
  ```kotlin
  fun swipe(kept: Boolean) {
      val s = _session.value
      if (s.currentCardIndex >= s.currentRoundOptions.size) return
      // proceed
  }
  ```

## Comments & Documentation

### Use When Needed
- **Why**, not what (code shows what)
  ```kotlin
  // Good: explains business logic
  // Round survivors are shuffled to ensure randomness
  val options = TournamentEngine.getOptionsForRound(...)
  
  // Avoid: obvious
  // Get survivors
  val survivors = ...
  ```

### Function Documentation
```kotlin
/**
 * Check if all cards in the round have been swiped.
 * @param totalInRound Total options in this round
 * @param swipedCount Cards user has swiped
 * @return true if all swiped, false otherwise
 */
fun isRoundComplete(totalInRound: Int, swipedCount: Int): Boolean
```

## Imports Organization

```kotlin
// 1. Kotlin stdlib
import java.util.UUID

// 2. Android framework
import androidx.lifecycle.ViewModel
import android.util.Log

// 3. Jetpack/Compose
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

// 4. Retrofit/Serialization
import retrofit2.http.GET
import com.squareup.moshi.Json

// 5. Project code
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.domain.TournamentEngine
```

## Testing (If Added)

- Test names: `test{Function}{Scenario}`
  ```kotlin
  fun testIsTournamentComplete_WithOneSurvivor_ReturnsTrue()
  fun testFindMatches_WithNoOverlap_ReturnsEmpty()
  ```
- Use meaningful assertions
- Mock StateFlow when testing ViewModels

## Compilation & Linting

- Build locally before pushing
  ```bash
  ./gradlew build --warning-mode=all
  ```
- No syntax errors; warnings acceptable if unavoidable
- Format via Android Studio's "Reformat Code" (Cmd+Alt+L)

## Security Checklist

- No API keys in source; use BuildConfig or .properties
- No logging of sensitive data (user choices, passwords)
- Validate API responses before parsing
- Use HTTPS for all network calls (Retrofit default)

## Performance Tips

- Avoid recompositions: use `remember` for expensive objects
- `remember` immutable state in Compose
- Use `.asStateFlow()` to expose immutable flows
- Lazy-load images via Coil (automatic with `Image(model=url)`

## Quick Reference

| Item | Style | Example |
|------|-------|---------|
| Class | PascalCase | `GameViewModel` |
| Enum | PascalCase | `GameMode` |
| Object | PascalCase | `TournamentEngine` |
| Variable | camelCase | `currentOption` |
| Constant | UPPER_SNAKE_CASE | `BASE_URL` |
| Composable | PascalCase | `HomeScreen()` |
| Package | lowercase.hierarchy | `com.vjpro.tindow.ui.home` |

## See Also

- `README.md` — Project overview & architecture
- `app/build.gradle.kts` — Dependencies & AGP config
