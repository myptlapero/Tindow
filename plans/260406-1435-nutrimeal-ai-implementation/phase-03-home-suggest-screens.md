# Phase 3: Home + Suggest Screens

## Overview
- **Priority:** P0
- **Status:** Complete
- **Effort:** 4h
- **Description:** Home screen (image/text input) + Suggest screen (3 meal cards). Core user flow.

## Context Links
- Pencil Home: `HxG26` — image upload area, text field, "Gợi ý món ăn" CTA, recent chips, bottom nav
- Pencil Suggest: `MaZEj` — 3 cards with image left (40%) + info right (60%)

## Requirements
- Home: camera/gallery image picker, text input, "Gợi ý món ăn" button, recent history chips
- Suggest: 3 meal suggestion cards, loading skeleton, error/retry, tap → detail
- ViewModel handles Gemini API calls + state management

## Related Code Files

### Create
- `app/src/main/java/com/vjpro/tindow/ui/home/HomeScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/home/HomeViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/suggest/SuggestScreen.kt`
- `app/src/main/java/com/vjpro/tindow/ui/suggest/SuggestViewModel.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/MealCard.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/LoadingSkeleton.kt`
- `app/src/main/java/com/vjpro/tindow/ui/components/ImageUploadArea.kt`

### Modify
- `app/src/main/java/com/vjpro/tindow/ui/navigation/AppNavigation.kt` — add Home/Suggest routes

## Implementation Steps

### 1. HomeScreen Layout (match Pencil HxG26)

Top-down vertical layout:
```
┌─────────────────────────────┐
│ 🍽 NutriMeal AI            │ ← TopBar with logo
│                             │
│ Hôm nay bạn muốn ăn gì?   │ ← Title text
│                             │
│ ┌─────────────────────────┐ │
│ │    📷                    │ │ ← Image upload area (dashed border)
│ │  Chụp ảnh món ăn        │ │    green bg, tap → camera/gallery picker
│ │  Nhấn để chụp hoặc chọn │ │
│ └─────────────────────────┘ │
│                             │
│ 🔍 Nhập tên món ăn...      │ ← TextField with search icon
│                             │
│ [✨ Gợi ý món ăn]          │ ← Green CTA button, full width
│                             │
│ Gần đây                    │ ← Section header
│ [Phở bò] [Gỏi cuốn] [Bún] │ ← History chips (horizontal scroll)
│                             │
│ ══════════════════════════  │
│  🏠 HOME    📅 THỰC ĐƠN   │ ← Bottom nav
└─────────────────────────────┘
```

### 2. Image Picker
- Use `ActivityResultContracts.TakePicturePreview` for camera
- Use `ActivityResultContracts.GetContent` for gallery
- Show bottom sheet / dialog with 2 options on tap
- Preview selected image thumbnail in upload area with remove button
- Compress bitmap before passing to Gemini

### 3. HomeViewModel
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState>

    fun onTextChanged(text: String)
    fun onImageSelected(bitmap: Bitmap)
    fun onRemoveImage()
    fun onSuggestMeals()   // → navigates to Suggest with loading
    fun loadRecentHistory() // → chips from Room
}

data class HomeUiState(
    val textInput: String = "",
    val selectedImage: Bitmap? = null,
    val recentHistory: List<String> = emptyList(),
    val isLoading: Boolean = false
)
```

### 4. SuggestScreen Layout (match Pencil MaZEj)

```
┌─────────────────────────────┐
│ ← Gợi ý món ăn             │ ← TopBar with back arrow
│ Dựa trên nguyên liệu của bạn│
│                             │
│ ┌─────────────────────────┐ │
│ │ 📸    │ Canh chua cá lóc │ │ ← MealCard
│ │ image │ 🔥 320 kcal      │ │    image left 40%
│ │       │ ⚡ Dễ             │ │    info right 60%
│ │       │ ⏱ 30 phút        │ │
│ │       │ Xem chi tiết →   │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ (card 2)                │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ (card 3)                │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

### 5. MealCard Component
Reusable composable:
- Row layout: Image (40%, rounded corners) | Column (60%, name/calories/difficulty/time/CTA)
- White card with rounded corners, subtle shadow
- "Xem chi tiết →" link in green
- onClick → navigate to MealDetail

### 6. SuggestViewModel
```kotlin
@HiltViewModel
class SuggestViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val uiState: StateFlow<SuggestUiState>

    fun suggestMeals(input: String, image: Bitmap?)
    // Called on screen entry, triggers Gemini API
    // Saves result to history via repository
}

sealed class SuggestUiState {
    object Loading : SuggestUiState()
    data class Success(val meals: List<Meal>) : SuggestUiState()
    data class Error(val message: String) : SuggestUiState()
}
```

### 7. Loading Skeleton
- 3 placeholder cards with shimmer animation
- Match card layout dimensions
- Use `Modifier.shimmer()` or custom shimmer brush

### 8. Navigation Flow
- Home → tap "Gợi ý" → navigate to Suggest route with query param
- Pass text input as nav argument. Image via shared ViewModel or SavedStateHandle
- Suggest → tap card → navigate to MealDetail (Phase 4)

## Todo List
- [x] Create ImageUploadArea composable
- [x] Create HomeScreen with full layout
- [x] Create HomeViewModel with state management
- [x] Implement camera/gallery image picker
- [x] Create MealCard reusable composable
- [x] Create LoadingSkeleton composable
- [x] Create SuggestScreen with 3 cards
- [x] Create SuggestViewModel with Gemini integration
- [x] Wire navigation Home → Suggest
- [x] Handle error states + retry
- [x] Add recent history chips on Home

## Success Criteria
- User can type text or capture image on Home
- "Gợi ý" button triggers Gemini API call
- Suggest screen shows 3 meal cards with real data
- Loading skeleton displays while waiting
- Error state shows snackbar with retry
- Recent history chips appear from Room data
- UI matches Pencil designs

## Risk Assessment
- **Image passing between screens:** Use shared ViewModel scoped to nav graph, or temporary file
- **Gemini response time:** 3-8 seconds typical. Skeleton essential.
- **Large bitmap camera:** Compress to max 1024px before API call
