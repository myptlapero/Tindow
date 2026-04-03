# Phase 4: Swipe Card Component

## Context
- [plan.md](plan.md) | Depends on: Phase 2, 3
- This is the **core UX** of the app — most time-critical phase

## Overview
- **Priority:** P0
- **Status:** complete
- **Effort:** 1.5 hr

## Key Insights
- Tinder-style: single card visible, drag horizontally to keep/eliminate
- Spring physics for natural feel
- Visual feedback: card tilts + color overlay (green=keep, red=eliminate)
- Show 2-3 cards stacked for depth effect
- Round counter + progress indicator

## Requirements
- Draggable card with horizontal swipe gesture
- Spring-back animation if not swiped far enough
- Fly-off animation when swiped past threshold
- Color overlay feedback during drag
- Card stack (top card swipeable, cards behind visible as stack)
- Integration with SwipeViewModel for round state

## Related Code Files
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/swipe/SwipeCard.kt`
- **Create:** `app/src/main/java/com/vjpro/tindow/ui/swipe/SwipeableCardStack.kt`
- **Modify:** `app/src/main/java/com/vjpro/tindow/ui/swipe/SwipeScreen.kt`

## Implementation Steps

### 1. SwipeCard composable
```kotlin
// ui/swipe/SwipeCard.kt
@Composable
fun SwipeCard(
    option: Option,
    modifier: Modifier = Modifier
) {
    Card(shape, elevation, colors) {
        // Image (Coil AsyncImage) — top 60%
        // Title text — bottom
        // Description if available
    }
}
```

### 2. Swipeable card with gesture detection
Use `Modifier.pointerInput` + `detectHorizontalDragGestures`:
- Track `offsetX` with `Animatable`
- Threshold: 150dp for swipe commit
- Below threshold: spring back to center
- Above threshold: animate fly-off → trigger callback
- Rotation: slight tilt based on `offsetX` (max ±15°)
- Overlay: green tint for right drag, red for left drag, opacity based on distance

### 3. Card stack
```kotlin
// ui/swipe/SwipeableCardStack.kt
@Composable
fun SwipeableCardStack(
    options: List<Option>,
    onSwipe: (Option, Boolean) -> Unit,  // (option, kept)
    currentIndex: Int
) {
    Box {
        // Render up to 3 cards, back to front
        // Card at index+2: small, far back (scale 0.9)
        // Card at index+1: medium (scale 0.95)
        // Card at index: full size, swipeable
    }
}
```

### 4. SwipeScreen integration
- Show round counter: "Round X"
- Show progress: "3/8 cards"
- SwipeableCardStack in center
- NOPE / LIKE labels that appear during swipe
- "Round Complete" dialog when all cards swiped → show survivor count → next round or view results
- For duo mode: "Pass to Player 2" interstitial between turns

### 5. Animations
- Card enter: slide up from bottom with fade
- Swipe commit: fly off screen (left or right) with rotation
- Next card: scale up from 0.95 to 1.0
- Round complete: confetti or simple celebration animation (optional)

## Todo
- [x] Create SwipeCard composable with image + text
- [x] Implement horizontal drag gesture with spring physics
- [x] Add visual feedback (tilt, color overlay, NOPE/LIKE labels)
- [x] Create SwipeableCardStack with depth effect
- [x] Build SwipeScreen with round counter + progress
- [x] Add round-complete dialog with options (continue/stop)
- [x] Add "Pass phone" interstitial for duo mode
- [x] Test on real device for gesture feel

## Risk Assessment
- **Janky gestures:** Test early on real device. Emulator touch ≠ real finger.
- **Image loading delay:** Use Coil placeholder + crossfade

## Success Criteria
- Smooth swipe left/right with spring physics
- Card flies off screen on committed swipe
- Visual feedback clear (user knows keep vs eliminate)
- Round progression works correctly
- Stack effect gives depth perception
