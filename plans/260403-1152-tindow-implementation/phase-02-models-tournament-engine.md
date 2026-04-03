# Phase 2: Data Models & Tournament Engine

## Context
- [plan.md](plan.md) | Depends on: Phase 1

## Overview
- **Priority:** P0
- **Status:** complete
- **Effort:** 45 min

## Key Insights
- Tournament = filter mode: each round user swipes all cards, survivors advance
- Need to handle odd numbers (last card gets "bye" — auto-advances)
- 2-player: each player swipes independently, then compare survivors
- In-memory only, no persistence needed

## Requirements
- `Option` data class: id, title, imageUri (local or URL), description
- `GameSession` state: mode (solo/duo), options, rounds history, current player
- `TournamentEngine`: manages round progression, survivor tracking, winner detection

## Related Code Files
- **Create:** `app/src/main/java/com/vjpro/tindow/data/model/Option.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/data/model/GameSession.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/domain/TournamentEngine.kt`

## Implementation Steps

### 1. Option model
```kotlin
// data/model/Option.kt
data class Option(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val imageUri: String? = null,  // content:// (local) or https:// (API)
    val description: String? = null,
    val source: OptionSource = OptionSource.MANUAL
)

enum class OptionSource { MANUAL, MEAL_API, PLACE_API, AI }
```

### 2. GameSession model
```kotlin
// data/model/GameSession.kt
enum class GameMode { SOLO, DUO }
enum class PlayerTurn { PLAYER_1, PLAYER_2 }

data class GameSession(
    val mode: GameMode = GameMode.SOLO,
    val allOptions: List<Option> = emptyList(),
    val currentRound: Int = 1,
    val currentTurn: PlayerTurn = PlayerTurn.PLAYER_1,
    val roundSurvivors: Map<Int, List<Option>> = emptyMap(),  // round# -> survivors
    val player1Survivors: List<Option> = emptyList(),  // for duo mode
    val player2Survivors: List<Option> = emptyList(),  // for duo mode
)
```

### 3. TournamentEngine
```kotlin
// domain/TournamentEngine.kt
class TournamentEngine {
    // Get options for current round (survivors from previous, or all if round 1)
    fun getOptionsForRound(session: GameSession): List<Option>

    // Record a swipe decision
    fun processSwipe(option: Option, kept: Boolean, currentSurvivors: MutableList<Option>)

    // Check if round is complete (all cards swiped)
    fun isRoundComplete(totalInRound: Int, swipedCount: Int): Boolean

    // Check if tournament is over (0 or 1 survivor)
    fun isComplete(survivors: List<Option>): Boolean

    // Get final winner
    fun getWinner(session: GameSession): Option?

    // For duo mode: find matching options between P1 and P2
    fun findMatches(p1Survivors: List<Option>, p2Survivors: List<Option>): List<Option>
}
```

Keep engine as pure functions — no state, easy to test.

## Todo
- [x] Create Option data model
- [x] Create GameSession data model
- [x] Implement TournamentEngine with pure functions
- [x] Handle odd number edge case (bye)

## Success Criteria
- Models represent all game states
- Engine correctly filters survivors per round
- Duo mode match detection works
- Odd count handled with auto-bye
