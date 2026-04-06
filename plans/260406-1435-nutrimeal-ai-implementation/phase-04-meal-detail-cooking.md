# Phase 4: Meal Detail + Cooking Steps

## Overview
- **Priority:** P1
- **Status:** Complete
- **Effort:** 3h
- **Description:** Meal detail screen with nutrition, ingredients, cooking guide. Cooking step-by-step view.

## Context Links
- Pencil Meal Detail: `Nye5A` — hero image, nutrition chips, ingredients, cooking steps, FAB
- Pencil Cooking Step: `rvCDD` — step image carousel, timer, ingredients checklist, prev/next

## Requirements
- Meal Detail: hero image, nutrition chips, ingredient list, cooking steps overview, FAB "Thêm vào thực đơn"
- Cooking Step: step-by-step view with image, timer, ingredient checklist, prev/next navigation

## Related Code Files

### Create
- `app/src/main/java/com/vjpro/tindow/ui/detail/MealDetailScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/detail/MealDetailViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/cooking/CookingStepScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/cooking/CookingStepViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/NutritionChip.kt`

### Modify
- `app/src/main/java/com/vjpro/tindow/ui/navigation/AppNavigation.kt` — add detail/cooking routes

## Implementation Steps

### 1. MealDetailScreen Layout (match Pencil Nye5A)

```
┌─────────────────────────────┐
│ [Hero Image 250dp]          │ ← Full-width, back button overlay
│  ◄                          │
├─────────────────────────────┤
│ Phở Bò Hà Nội              │ ← Title (headline)
│                             │
│ [420 kcal][28g][52g][12g]   │ ← Nutrition chips row
│                             │
│ Dễ  ⏱ 45 phút              │ ← Difficulty chip + time
│                             │
│ ─── Nguyên liệu ───        │ ← Section header
│ ☐ 200g bánh phở tươi       │ ← Checkbox list
│ ☐ 150g thịt bò tái         │
│ ☐ 2 củ hành tây             │
│ ...                         │
│                             │
│ ─── Hướng dẫn nấu ───      │ ← Section header
│ ① Chuẩn bị nước dùng       │ ← Numbered steps (tap → cooking view)
│   Ninh xương bò với...      │
│ ② Chuẩn bị bánh phở và thịt│
│   ...                       │
│                             │
│        [+ Thêm vào thực đơn]│ ← FAB bottom
└─────────────────────────────┘
```

### 2. Nutrition Chips Row
Reusable `NutritionChip` composable:
- Rounded pill shape, colored border
- Label: "420 kcal", "28g protein", "52g carb", "12g fat"
- Colors from theme: CalorieOrange, ProteinBlue, CarbYellow, FatRed

### 3. Ingredient Checklist
- LazyColumn with checkbox items
- Local state only (no persistence needed)
- Each row: Checkbox + ingredient text

### 4. Cooking Steps Overview
- Numbered list with step title + brief description
- Tap any step → navigate to CookingStepScreen at that step index

### 5. FAB "Thêm vào thực đơn"
- FloatingActionButton at bottom
- Opens dialog to select day + meal slot (breakfast/lunch/dinner)
- Saves to Room via WeeklyPlanRepository

### 6. CookingStepScreen Layout (match Pencil rvCDD)

```
┌─────────────────────────────┐
│ [Step Image with carousel]  │ ← Pager/carousel, "Bước 1/4" badge
│  Bước 1/4                   │
├─────────────────────────────┤
│ Phở Bò Hà Nội  ⏱ 15:00    │ ← Title + timer button
│                             │
│ Bước 1: Chuẩn bị nước dùng │ ← Step title (bold)
│                             │
│ Cho xương bò vào nồi nước  │ ← Step description (paragraph)
│ lạnh, đun sôi rồi đổ bỏ...│
│                             │
│ ─── Nguyên liệu cần dùng ──│
│ ☑ 500g xương bò             │ ← Step-specific ingredients
│ ☐ 1 củ hành tây nướng       │
│ ☐ 1 nhánh gừng nướng        │
│ ...                         │
│                             │
│ [◄ Trước]  ●○○○  [Tiếp ►]  │ ← Navigation + page indicator
└─────────────────────────────┘
```

### 7. CookingStepViewModel
```kotlin
@HiltViewModel
class CookingStepViewModel : ViewModel() {
    val currentStep: StateFlow<Int>
    val timerSeconds: StateFlow<Int>
    val isTimerRunning: StateFlow<Boolean>

    fun nextStep()
    fun previousStep()
    fun startTimer(durationMinutes: Int)
    fun stopTimer()
}
```

Timer: use `viewModelScope.launch` with `delay(1000)` countdown loop

### 8. Navigation
- Suggest → tap card → MealDetail (pass meal index or serialized meal)
- MealDetail → tap step → CookingStep (pass meal + step index)
- Weekly Plan meals also navigate to MealDetail (shared route)

## Todo List
- [x] Create NutritionChip composable
- [x] Create MealDetailScreen with full layout
- [x] Create MealDetailViewModel
- [x] Implement ingredient checklist with checkboxes
- [x] Implement FAB "Thêm vào thực đơn" with day picker dialog
- [x] Create CookingStepScreen with step-by-step view
- [x] Create CookingStepViewModel with timer
- [x] Implement prev/next step navigation + page indicator
- [x] Wire navigation Suggest → Detail → CookingStep
- [x] Verify UI matches Pencil designs

## Success Criteria
- Hero image loads with back button overlay
- Nutrition chips display correctly with colors
- Ingredient checklist toggles work
- Cooking steps navigate step-by-step
- Timer counts down per step
- FAB saves meal to weekly plan via Room
