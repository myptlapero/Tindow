# Phase 6: Result Screen & 2-Player Mode

## Context
- [plan.md](plan.md) | Depends on: Phase 4, 5

## Overview
- **Priority:** P1
- **Status:** complete
- **Effort:** 1 hr

## Requirements
- Solo result: show winner card with celebration
- Duo result: show matched options, highlight common picks
- "Play Again" and "Back to Home" actions
- Player turn management for duo mode

## Related Code Files
- **Modify:** `app/src/main/java/com/vjpro/tindow/ui/result/ResultScreen.kt`
- **Modify:** `app/src/main/java/com/vjpro/tindow/ui/swipe/SwipeScreen.kt` (add duo flow)
- **Modify:** `app/src/main/java/com/vjpro/tindow/ui/GameViewModel.kt` (add duo logic)
- **Modify:** `app/src/main/java/com/vjpro/tindow/ui/home/HomeScreen.kt` (add mode selection)

## Implementation Steps

### 1. HomeScreen — mode selection
```
┌────────────────────────┐
│       TINDOW           │
│   "Can't decide?"      │
│                        │
│   ┌──────┐ ┌──────┐   │
│   │ Solo │ │ Duo  │   │
│   │  👤  │ │  👥  │   │
│   └──────┘ └──────┘   │
│                        │
│   Options: [  8  ▼]   │
│                        │
│   [Let's Go! →]       │
└────────────────────────┘
```

### 2. Duo mode flow in SwipeScreen
- Before P1 starts: "Player 1's Turn" interstitial (3s countdown or tap)
- P1 swipes all options → survivors saved to `player1Survivors`
- "Pass phone to Player 2" interstitial (opaque, hides P1 choices)
- P2 swipes same original options → survivors saved to `player2Survivors`
- Navigate to ResultScreen

### 3. ResultScreen — Solo
```
┌────────────────────────┐
│     🎉 Winner! 🎉      │
│                        │
│   ┌──────────────┐    │
│   │   [IMAGE]    │    │
│   │              │    │
│   │   Pho 🍜     │    │
│   └──────────────┘    │
│                        │
│   [Play Again] [Home] │
└────────────────────────┘
```

### 4. ResultScreen — Duo
```
┌────────────────────────┐
│     Match Results!     │
│                        │
│  ✅ Matches (2):       │
│  ┌──────┐ ┌──────┐   │
│  │ Pho  │ │Sushi │   │
│  └──────┘ └──────┘   │
│                        │
│  P1 only: Burger      │
│  P2 only: Pizza       │
│                        │
│  [Swipe Matches →]    │
│  [Play Again] [Home]  │
└────────────────────────┘
```

If matches >1: offer to run another swipe round on matches only.
If no matches: show "No match! Try again?" with closest options.

### 5. "Closest" logic for no-match scenario
Track which round each option was eliminated. Options eliminated latest = "almost matched." Show these as suggestions.

## Todo
- [x] Build HomeScreen with mode + option count selection
- [x] Add duo turn management to SwipeScreen
- [x] Create "Pass phone" interstitial (opaque, countdown)
- [x] Build ResultScreen for solo mode (winner card + celebration)
- [x] Build ResultScreen for duo mode (matches + breakdown)
- [x] Add "Swipe Matches" for multiple matches
- [x] Add "Play Again" / "Home" navigation
- [x] Handle no-match edge case

## Success Criteria
- Solo mode shows single winner clearly
- Duo mode correctly identifies matching choices
- Turn handoff is clear and hides P1's picks
- Can play again without restarting app
