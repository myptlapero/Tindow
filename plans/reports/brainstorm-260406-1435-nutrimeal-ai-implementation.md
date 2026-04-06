# Brainstorm: NutriMeal AI Implementation

## Problem Statement
Build a full Android app (NutriMeal AI) for hackathon in 2-3 days. App uses Gemini AI to suggest meals from image/text input, show nutrition details, generate weekly meal plans, and provide step-by-step cooking guides. UI designs exist in Pencil (.pen file) with 6 screens.

## Decisions Made

| Decision | Choice | Rationale |
|---|---|---|
| Timeline | 2-3 days | Hackathon deadline |
| AI Model | Gemini (key in local.properties) | Already configured |
| Database | Room | Type-safe, Compose-friendly, handles relational data (weekly plan) |
| Navigation | Compose Navigation | Standard, type-safe routes, 2-tab + nested flows |
| Meal Images | Pexels Search API | Quality photos by meal name |
| UI Reference | Pencil designs (meal_ai.pen) | 6 screens already designed |

## Architecture: Recommended Approach

### Pattern: MVVM + Clean-ish Architecture
```
ui/ (Compose screens + ViewModels)
data/ (Repository, Room DB, API services)
domain/ (Models/data classes)
```

No need for full Clean Architecture (use cases layer) — YAGNI for hackathon scope.

### Tech Stack
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Compose Navigation (2 tabs: Home, Weekly Plan)
- **State**: ViewModel + StateFlow
- **DI**: Hilt (lightweight setup)
- **Network**: Retrofit + OkHttp (Gemini API + Image Search API)
- **DB**: Room (history, weekly plan cache)
- **Image Loading**: Coil (Compose native)
- **Camera**: CameraX or ActivityResult (simpler)

### Screen → Feature Mapping

| Screen | Pencil ID | Feature | Priority |
|---|---|---|---|
| Home | `HxG26` | F-001, F-002 — Input (image/text) | P0 |
| Suggest | `MaZEj` | F-003 — 3 meal suggestions | P0 |
| Meal Detail | `Nye5A` | F-004 — Nutrition + cooking steps | P1 |
| Weekly Plan | `H0oE1` | F-005 — 7-day meal plan | P1 |
| Cooking Step | `rvCDD` | F-006 — Step-by-step guide | P2 |
| History | `cE8sL` | F-008 — Past suggestions | P2 |

## Design System (from Pencil)

**Colors**: Green primary (#4A7C59 range), warm beige background, white cards
**Components**: Rounded cards with image left/info right, nutrition chips, bottom nav, day tabs
**Typography**: Vietnamese text, clean sans-serif

## Implementation Strategy (3 phases)

### Phase 1: Foundation + Core Flow (Day 1)
- Project setup: Hilt, Room, Retrofit, Coil, Navigation
- Data models (Meal, Ingredient, WeeklyPlan, SuggestionHistory)
- Gemini API service (text + vision)
- Image search API service
- Home screen (image upload + text input)
- Suggest screen (3 meal cards)

### Phase 2: Detail + Weekly Plan (Day 2)
- Meal Detail screen (nutrition, ingredients, cooking steps)
- Weekly Plan screen (7 tabs, 3 meals/day)
- Gemini prompt engineering for weekly plan JSON
- Room persistence for weekly plan + history

### Phase 3: Polish + Extra (Day 3)
- Cooking Step Detail screen (step-by-step with timer)
- History screen
- Loading states (skeleton), error handling, empty states
- UI polish matching Pencil designs
- Edge cases, testing

## Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Gemini response format inconsistent | High | Strict JSON schema in prompt, fallback parsing |
| Image search API rate limit | Medium | Cache results in Room, fallback placeholder |
| Time pressure (2-3 days) | High | Phase-based delivery, P0 first |
| Gemini Vision slow response | Medium | Loading skeleton, timeout handling |

## Success Criteria
- [ ] Home: capture image or type text → send to Gemini
- [ ] Suggest: display 3 meals with images + nutrition
- [ ] Detail: full meal info with cooking steps
- [ ] Weekly Plan: 7-day × 3-meal plan via AI
- [ ] Cooking Steps: step-by-step view with navigation
- [ ] History: persist and revisit past suggestions
- [ ] UI matches Pencil designs

## Unresolved Questions
- Pexels vs Pexels — which API? (Pexels has free tier, simpler auth)
- Voice input (F-002 optional) — skip for hackathon?
- Export weekly plan (F-005) — skip for hackathon?
