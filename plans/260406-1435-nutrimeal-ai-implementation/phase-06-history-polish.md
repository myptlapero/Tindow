# Phase 6: History + Polish

## Overview
- **Priority:** P2
- **Status:** Complete
- **Effort:** 3h
- **Description:** History screen, loading states, error handling, UI polish to match Pencil

## Context Links
- Pencil History: `cE8sL` — list of past suggestions with timestamps, tap to revisit

## Requirements
- History screen: list of past suggestion sessions with timestamps
- Loading skeletons on all data-loading screens
- Error handling with snackbar + retry
- UI polish: spacing, typography, colors match Pencil exactly

## Related Code Files

### Create
- `app/src/main/java/com/vjpro/tindow/ui/history/HistoryScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/history/HistoryViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/HistoryItem.kt`

### Modify
- `app/src/main/java/com/vjpro/tindow/ui/navigation/AppNavigation.kt` — add history route
- `app/src/main/java/com/vjpro/tindow/ui/home/HomeScreen.kt` — history icon in top bar
- All screens — polish spacing, shadows, colors

## Implementation Steps

### 1. HistoryScreen Layout (match Pencil cE8sL)

```
┌─────────────────────────────┐
│ Lịch sử gợi ý           🗑 │ ← Title + clear all icon
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸  Phở Bò, Bún Chả,   │ │ ← HistoryItem
│ │     Gỏi Cuốn          > │ │    thumbnail, meal names
│ │     Hôm nay, 10:30      │ │    timestamp
│ │     3 món                │ │    meal count
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸  Bánh Mì Thịt,       │ │
│ │     Cơm Tấm, Bún Bò   > │ │
│ │     Hôm qua, 10:45      │ │
│ │     3 món                │ │
│ └─────────────────────────┘ │
│ ...                         │
│                             │
│ ══════════════════════════  │
│  🏠 HOME    📅 THỰC ĐƠN   │
└─────────────────────────────┘
```

### 2. HistoryViewModel
```kotlin
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val history: StateFlow<List<SuggestionHistory>>

    fun loadHistory()
    fun clearAll()
    fun onHistoryItemClick(item: SuggestionHistory)
    // → navigate to Suggest with cached meals (no re-fetch)
}
```

### 3. Timestamp Formatting
- "Hôm nay, 10:30"
- "Hôm qua, 10:45"
- "03/04/2026, 12:15"
- Use relative formatting for today/yesterday

### 4. Navigation from History
- Tap history item → Suggest screen with cached meals (skip API call)
- Pass history ID as nav argument, ViewModel loads from Room

### 5. Loading States Polish
Verify all screens have proper states:
- Home: none needed (static UI)
- Suggest: skeleton cards (3 placeholder cards with shimmer)
- MealDetail: skeleton for hero image + content
- WeeklyPlan: skeleton for meal cards
- CookingStep: skeleton for step content

### 6. Error Handling Polish
All API-calling screens:
- Snackbar with error message + "Thử lại" action
- Retry triggers same API call
- Network error: "Không có kết nối mạng"
- Gemini error: "AI đang bận, vui lòng thử lại"
- Timeout: "Quá thời gian chờ"

### 7. UI Polish Checklist
Compare each screen with Pencil screenshots:
- [ ] Correct green primary color throughout
- [ ] Beige background on main screens
- [ ] White cards with correct corner radius (~12dp)
- [ ] Correct font sizes (title, body, caption)
- [ ] Proper spacing/padding (16dp standard)
- [ ] Bottom nav matches design (green selected, gray inactive)
- [ ] Card shadows subtle (elevation 2dp)
- [ ] Image aspect ratios correct (1:1 thumbnails, 16:9 hero)

### 8. History Access
- Clock icon in Home screen top bar → navigates to History
- Or accessible via Home screen as tertiary nav

## Todo List
- [x] Create HistoryItem composable
- [x] Create HistoryScreen with list layout
- [x] Create HistoryViewModel
- [x] Implement timestamp relative formatting
- [x] Implement tap history → cached Suggest view
- [x] Implement clear all history
- [x] Add history navigation from Home screen
- [x] Polish loading skeletons on all screens
- [x] Polish error handling with Vietnamese messages
- [x] UI audit: compare all screens with Pencil designs
- [x] Fix spacing, colors, typography mismatches

## Success Criteria
- History shows all past suggestion sessions
- Tap history item shows cached results (no API call)
- Clear all removes history from Room
- All screens have proper loading/error states
- UI closely matches Pencil designs on visual comparison
- Vietnamese text throughout (no English UI strings)
