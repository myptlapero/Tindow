# Tindow - Decision-Making Android App

Tindow helps users quickly decide between options using Tinder-like swiping and tournament-style filtering.

## What is Tindow?

**Core Idea**: Swipe through cards to filter options down to a winner. Perfect for decisions like "What to eat?" or "Where to go?"

**Two Game Modes**:
- **Solo**: Single player swipes all options, survivors advance to next round until 1 remains
- **Duo**: Two players swipe the same options sequentially, then see matched choices they both kept

## Quick Start

### Build & Run
```bash
# Clone and open in Android Studio
./gradlew build
./gradlew assembleDebug  # or run via Studio
```

### Tech Stack
- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose + Material3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **API**: Retrofit + Moshi (TheMealDB)
- **Images**: Coil
- **Min/Target SDK**: 24 / 35
- **Package**: `com.vjpro.tindow`

## Project Structure

```
app/src/main/java/com/vjpro/tindow/
├── core/
│   ├── base/
│   │   └── view/BaseComposeActivity.kt       # Base activity
│   └── extension/WindowExt.kt                 # Window utilities
├── data/
│   ├── model/
│   │   ├── Option.kt                          # Single choice card
│   │   ├── GameSession.kt                     # Game state
│   └── api/
│       ├── RetrofitClient.kt                  # HTTP setup
│       ├── MealApiService.kt                  # TheMealDB endpoints
│       └── MealDto.kt                         # Response DTOs
├── domain/
│   └── TournamentEngine.kt                    # Tournament logic (pure functions)
└── ui/
    ├── GameViewModel.kt                       # Shared game state
    ├── home/HomeScreen.kt                     # Mode selection
    ├── input/InputScreen.kt                   # Add options
    ├── swipe/
    │   ├── SwipeScreen.kt                     # Swiping interface
    │   ├── SwipeCard.kt                       # Card component
    │   └── SwipeableCardStack.kt              # Stack animation
    ├── result/ResultScreen.kt                 # Winner/matches
    ├── navigation/
    │   ├── TindowRoute.kt                     # Route definitions
    │   └── TindowNavGraph.kt                  # NavGraph setup
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Key Concepts

### GameSession
Holds all game state: mode, all options, round number, survivors, and whether the tournament is finished. Persisted in `GameViewModel` as `StateFlow<GameSession>`.

### TournamentEngine
Pure functions (no side effects) that implement tournament logic:
- `getOptionsForRound()` — Get cards for a round (shuffled)
- `isRoundComplete()` — Check if all cards swiped
- `isTournamentComplete()` — Check if 1 or 0 survivors remain
- `findMatches()` — Find options both players kept (duo mode)
- `findClosestOptions()` — Fallback suggestions if no full matches

### Option & OptionSource
Each card is an `Option` with `id`, `title`, `imageUri`, `description`, and `source` (MANUAL, MEAL_API, PLACE_API, or AI).

## Navigation Flow

```
Home → (select mode) → Input → (add options) → Swipe → Result
```

- **Home**: Choose SOLO or DUO mode
- **Input**: Add manual options or fetch from TheMealDB API
- **Swipe**: Tap/swipe left (discard) or right (keep)
- **Result**: Show winner (solo) or matches (duo)

## Tournament Rules

**Solo Mode**:
1. Start with all options shuffled
2. Swipe through each card: tap right to keep, left to discard
3. Survivors advance to Round 2 (reshuffled)
4. Repeat until 1 option remains = winner

**Duo Mode**:
1. Player 1 swipes all options independently
2. Player 2 swipes the same options independently
3. System finds options both players kept = matches
4. If no matches: show "closest options" (survived most rounds)

## Data Flow

```
HomeScreen
  ↓ (mode) → GameViewModel.setMode()
InputScreen
  ↓ (options) → GameViewModel.addOptions()
SwipeScreen
  ↓ (kept/discarded) → GameViewModel.swipe()
  ↓ (triggers TournamentEngine logic)
ResultScreen
  ↓ (shows winner or matches)
```

## API Integration

### TheMealDB (Free, No Auth)
```kotlin
// Get random meal
val meal = mealApiService.getRandomMeal()

// Search meals
val meals = mealApiService.searchMeals("pasta")

// Filter by category
val meals = mealApiService.getMealsByCategory("Seafood")
```

Each meal becomes an `Option` with image URL and details.

## Development Notes

- **Offline-First**: All core logic runs offline. API calls optional.
- **State Management**: Single source of truth in `GameViewModel`
- **No Backend**: Lightweight, no server dependency
- **Hackathon Project**: Focus on UX, not scalability
- **Card Animations**: SwipeableCardStack uses spring physics for swipe feel

## Debugging

Check `GameSession` state via `GameViewModel.session` StateFlow. Add logging in `TournamentEngine` functions to trace tournament progression.

See `code-standards.md` for coding conventions.
