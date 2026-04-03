---
status: in-progress
created: 2026-04-03
slug: tindow-implementation
branch: main
---

# Tindow Implementation Plan

## Context
- **Brainstorm:** [brainstorm-260403-1152-tindow-app.md](../reports/brainstorm-260403-1152-tindow-app.md)
- **API Research:** [researcher-260403-1152-free-api-research.md](../reports/researcher-260403-1152-free-api-research.md)
- **Scope:** Hackathon demo — ship fast, manual input + TheMealDB MVP

## Phases

| # | Phase | Status | Priority | Effort |
|---|-------|--------|----------|--------|
| 1 | [Dependencies & Project Setup](phase-01-dependencies-setup.md) | complete | P0 | 30 min |
| 2 | [Data Models & Tournament Engine](phase-02-models-tournament-engine.md) | complete | P0 | 45 min |
| 3 | [Navigation & Screen Scaffolds](phase-03-navigation-screens.md) | complete | P0 | 30 min |
| 4 | [Swipe Card Component](phase-04-swipe-card-component.md) | complete | P0 | 1.5 hr |
| 5 | [Input Screen & API Integration](phase-05-input-screen-api.md) | complete | P0 | 1 hr |
| 6 | [Result Screen & 2-Player Mode](phase-06-result-two-player.md) | complete | P1 | 1 hr |
| 7 | [Polish & Enhanced APIs](phase-07-polish-enhanced-apis.md) | pending | P2 | 1-2 hr |

**Total estimated: ~5-7 hours**

## Architecture Overview

```
com.vjpro.tindow/
├── core/
│   ├── base/view/BaseComposeActivity.kt  (existing)
│   └── extension/WindowExt.kt            (existing)
├── data/
│   ├── model/Option.kt                   — Core data model
│   ├── api/MealApiService.kt             — TheMealDB Retrofit service
│   └── api/MealRepository.kt             — Repository pattern
├── domain/
│   └── TournamentEngine.kt               — Round/filter logic
├── ui/
│   ├── theme/                             (existing)
│   ├── navigation/TindowNavGraph.kt       — Nav routes
│   ├── home/HomeScreen.kt                 — Mode + config
│   ├── input/InputScreen.kt               — Add options
│   ├── swipe/SwipeScreen.kt               — Core swiping
│   ├── swipe/SwipeCard.kt                 — Card composable
│   ├── swipe/SwipeViewModel.kt            — Swipe state
│   └── result/ResultScreen.kt             — Winner/match
├── MainActivity.kt                        (modify)
└── TindowApp.kt                           — App-level setup
```

## Dependency Chain
```
Phase 1 (setup) → Phase 2 (models) → Phase 3 (navigation)
                                          ↓
                   Phase 4 (swipe) ← Phase 3
                        ↓
                   Phase 5 (input + API)
                        ↓
                   Phase 6 (results + 2P)
                        ↓
                   Phase 7 (polish)
```

## Key Dependencies to Add
- `androidx.navigation:navigation-compose`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `io.coil-kt:coil-compose` — image loading
- `com.squareup.retrofit2:retrofit` + `converter-moshi` — API calls
- `com.squareup.moshi:moshi-kotlin` — JSON parsing

## Success Criteria
- [ ] Solo mode: input options → swipe rounds → winner
- [ ] Duo mode: P1 swipes → P2 swipes → match results
- [ ] TheMealDB: fetch food suggestions with images
- [ ] Smooth swipe animation with spring physics
- [ ] Demo-ready in under 2 minutes of interaction
