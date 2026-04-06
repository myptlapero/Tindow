# Phase 5: Weekly Plan Screen

## Overview
- **Priority:** P1
- **Status:** Complete
- **Effort:** 3h
- **Description:** 7-day meal plan tab with day tabs, 3 meals/day cards, generate/regenerate via Gemini, total calories

## Context Links
- Pencil Weekly Plan: `H0oE1` — day tabs (T2-CN), 3 meal cards, total kcal, "Tạo lại thực đơn" button

## Requirements
- 7 horizontal scrollable day tabs (T2, T3, T4, T5, T6, T7, CN)
- 3 meal cards per day (Bữa sáng, Bữa trưa, Bữa tối) with thumbnail + name + calories
- Total calories display per day
- "Tạo lại thực đơn" button → Gemini generates new plan
- Tap meal card → MealDetail screen
- Persist plan in Room

## Related Code Files

### Create
- `app/src/main/java/com/vjpro/tindow/ui/weeklyplan/WeeklyPlanScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/weeklyplan/WeeklyPlanViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/DayTabRow.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/MealSlotCard.kt`

### Modify
- `app/src/main/java/com/vjpro/tindow/ui/navigation/AppNavigation.kt` — weekly plan as tab destination

## Implementation Steps

### 1. WeeklyPlanScreen Layout (match Pencil H0oE1)

```
┌─────────────────────────────┐
│ Thực đơn tuần            🔄 │ ← Title + refresh icon
│                             │
│ [T2][T3][T4][T5][T6][T7][CN]│ ← Day tabs, selected = green filled
│                             │
│ 🔥 Tổng: 1,520 kcal        │ ← Total calories for selected day
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸  Bữa sáng            │ │ ← MealSlotCard
│ │     Bánh mì trứng    >  │ │    thumbnail left, info right
│ │     ~400 kcal            │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸  Bữa trưa            │ │
│ │     Cơm gà xối mỡ    >  │ │
│ │     ~600 kcal            │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸  Bữa tối             │ │
│ │     Canh chua cá      >  │ │
│ │     ~500 kcal            │ │
│ └─────────────────────────┘ │
│                             │
│ [✨ Tạo lại thực đơn]      │ ← Green button, full width
│                             │
│ ══════════════════════════  │
│  🏠 HOME    📅 THỰC ĐƠN   │ ← Bottom nav (this tab active)
└─────────────────────────────┘
```

### 2. DayTabRow Component
- Horizontal scrollable row of 7 day chips
- Selected: green filled, white text
- Unselected: green outline, green text
- onClick updates selected day index in ViewModel

### 3. MealSlotCard Component
- Row: thumbnail (square, rounded) + Column (slot label, meal name, calories)
- Chevron ">" on right
- onClick → navigate to MealDetail for this meal

### 4. WeeklyPlanViewModel

```kotlin
@HiltViewModel
class WeeklyPlanViewModel @Inject constructor(
    private val weeklyPlanRepository: WeeklyPlanRepository
) : ViewModel() {
    val uiState: StateFlow<WeeklyPlanUiState>
    val selectedDay: StateFlow<Int> // 0-6

    fun selectDay(index: Int)
    fun generateNewPlan()     // Gemini API → save to Room
    fun loadExistingPlan()    // From Room on init
}

sealed class WeeklyPlanUiState {
    object Empty : WeeklyPlanUiState()
    object Loading : WeeklyPlanUiState()
    data class Success(val plan: WeeklyPlan) : WeeklyPlanUiState()
    data class Error(val message: String) : WeeklyPlanUiState()
}
```

### 5. Gemini Prompt for Weekly Plan

```
Tạo thực đơn 7 ngày cho người Việt Nam. Mỗi ngày gồm 3 bữa (sáng, trưa, tối).
Yêu cầu:
- Tổng calo/ngày: 1800-2200 kcal (sáng ~400, trưa ~600, tối ~500)
- Không lặp món ăn
- Món ăn Việt Nam truyền thống và phổ biến
- Trả về JSON format: { "days": [...] }
```

### 6. Empty State
First time: show empty state with "Tạo thực đơn" button
After generation: show plan, persist in Room

### 7. Export (Text Format)
- Share intent with plain text summary
- Format: Day → 3 meals with calories
- Button in toolbar or after "Tạo lại"

## Todo List
- [x] Create DayTabRow composable
- [x] Create MealSlotCard composable
- [x] Create WeeklyPlanScreen with full layout
- [x] Create WeeklyPlanViewModel
- [x] Implement Gemini prompt for weekly plan generation
- [x] Implement Room persistence for weekly plan
- [x] Wire day tab selection → display correct day's meals
- [x] Implement "Tạo lại thực đơn" with loading state
- [x] Implement export as text (share intent)
- [x] Handle empty state (first visit)
- [x] Navigate meal card tap → MealDetail

## Success Criteria
- Day tabs switch between 7 days
- 3 meal cards per day with correct data
- Total calories displayed and realistic (1800-2200)
- "Tạo lại" regenerates plan via Gemini
- Plan persists across app restarts (Room)
- Export generates readable text and opens share sheet
- Tap meal → navigates to MealDetail
