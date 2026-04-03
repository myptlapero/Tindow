# Brainstorm Report: Tindow - Decision Making App

**Date:** 2026-04-03 | **Type:** Brainstorm | **Status:** Agreed

---

## Problem Statement

Users face daily decision paralysis ("What to eat?", "Where to go?"). Need a fun, fast way to narrow choices using familiar Tinder-like swiping + tournament-style filtering.

## Confirmed Requirements

| Aspect | Decision |
|--------|----------|
| Target | Hackathon demo |
| Platform | Android native, Jetpack Compose + Material3 |
| Core UX | Tinder swipe - 1 card at a time, right=keep, left=eliminate |
| Tournament | Filter mode: each round swipe all cards, survivors advance |
| 2-player | Same device, sequential (P1 swipes all → P2 swipes all → compare matches) |
| Options count | User configurable |
| Data sources | Manual text/image input + API suggestions |
| Architecture | Offline-first, no backend, in-memory state |

## Evaluated Approaches

### A. Swipe Mechanic - Filter Mode (CHOSEN)
- Each round: see all remaining cards one by one, swipe keep/eliminate
- Survivors go to next round, repeat until 1 remains
- **Pros:** Simple, fast, Tinder-familiar
- **Cons:** Not true head-to-head comparison

### B. Swipe Mechanic - Bracket Tournament (REJECTED)
- Show 2 cards, pick winner, bracket-style knockout
- **Pros:** True comparison, clear bracket visualization
- **Cons:** Harder UX (showing 2 cards + swipe), more complex state management, overkill for hackathon

### C. Architecture - Offline-first (CHOSEN)
- All state in ViewModel + StateFlow, no database
- **Pros:** Zero setup, fastest to ship, no server costs
- **Cons:** No persistence between sessions

### D. Architecture - Firebase (REJECTED)
- Real-time sync, auth, cloud storage
- **Pros:** Scales to multi-device multiplayer later
- **Cons:** Overkill for hackathon scope, adds setup time

## Recommended API Stack

| Priority | API | Purpose | Auth | Free Limits |
|----------|-----|---------|------|-------------|
| P0 | Manual input | Text + gallery photo | None | Unlimited |
| P1 | TheMealDB | Food suggestions | None (key="1") | Unlimited |
| P2 | Pexels | Images for text-only options | API key | Unlimited |
| P3 | Geoapify | Nearby places | API key | 3K credits/day |
| P4 | Gemini API | AI-generated suggestions | API key | 250-1K req/day |

**Hackathon MVP:** P0 + P1 only. Add P2-P4 if time allows.

Full API research: [researcher-260403-1152-free-api-research.md](researcher-260403-1152-free-api-research.md)

## Proposed Architecture

```
UI Layer (Compose Screens)
├── HomeScreen        → Mode selection (solo/duo), option count config
├── InputScreen       → Add options (manual + API fetch)
├── SwipeScreen       → Core swiping experience with card stack
└── ResultScreen      → Winner (solo) / Match results (duo)

ViewModel Layer
├── SessionViewModel  → Game state, current round, player turn
├── OptionViewModel   → Manage option list, add/remove
└── SwipeViewModel    → Swipe logic, survivor tracking per round

Data Layer
├── ManualInputSource → User text/image input
├── MealApiSource     → TheMealDB integration
├── PlaceApiSource    → Geoapify integration (P3)
└── AiSuggestionSource→ Gemini API integration (P4)
```

## Core Game Flow

### Single Player
```
1. Setup: Choose category, set option count
2. Input: Add options manually or fetch from API
3. Round 1: Swipe N cards → ~N/2 survivors
4. Between rounds: Option to stop, continue, or add more
5. Round 2+: Repeat with survivors
6. End: Last standing = Winner
```

### Two Player (Same Device)
```
1. Setup: Same as single player
2. P1 Turn: "Pass to Player 1" screen → P1 swipes all
3. P2 Turn: "Pass to Player 2" screen → P2 swipes same set
4. Results: Show matched choices (both kept)
   - Perfect match → celebrate
   - Multiple matches → run another round on matches only
   - No match → show "closest" (items both kept longest)
```

## Technical Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Navigation:** Compose Navigation
- **State:** ViewModel + StateFlow
- **HTTP:** Retrofit + Moshi (for API calls)
- **Images:** Coil (Compose-native image loading)
- **Animations:** Compose animation APIs for swipe gestures
- **Min SDK:** 24 (already set)

## Key Implementation Notes

- Swipe gesture: `Modifier.pointerInput` + `detectHorizontalDragGestures` with spring animation
- Card stack: Only render top 2-3 cards for performance
- Odd options: Last card gets a "bye" (auto-advances to next round)
- "Pass phone" screen between players should be opaque (hide P1's choices from P2)
- Image picker: use `ActivityResultContracts.PickVisualMedia`

## Risk Assessment

| Risk | Severity | Mitigation |
|------|----------|------------|
| Swipe gesture feels janky | High | Test on real device early, use spring physics |
| API unavailable during demo | Medium | Bundle preset options as fallback |
| Odd option count edge case | Low | Auto-bye for last card |
| 2P turn handoff feels awkward | Medium | Clear "Pass phone" interstitial |

## Success Criteria

- [ ] Single player can input options and swipe to a winner
- [ ] 2-player mode finds matched choices
- [ ] At least TheMealDB integration working for food suggestions
- [ ] Smooth swipe animation with visual feedback
- [ ] Playable demo in under 2 minutes

## Cut for v1 (YAGNI)

- Adding options between rounds (adds state complexity)
- History/session persistence
- User accounts / profiles
- Multi-device multiplayer
- Complex category management
- Analytics / crash reporting

## Next Steps

1. Create implementation plan with phased approach
2. Set up navigation + screen scaffolds
3. Implement swipe card component first (core UX)
4. Add tournament engine logic
5. Integrate TheMealDB API
6. Add 2-player mode
7. Polish animations + result screen
