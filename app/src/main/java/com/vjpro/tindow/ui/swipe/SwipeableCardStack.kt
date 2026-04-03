package com.vjpro.tindow.ui.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vjpro.tindow.data.model.Option
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val SWIPE_THRESHOLD_FRACTION = 0.3f
private const val MAX_ROTATION_DEGREES = 15f
private const val STACK_VISIBLE_CARDS = 3
private const val BEHIND_CARD_SCALE_STEP = 0.05f

/**
 * Displays a stack of cards with the top one swipeable.
 * Shows up to 3 cards for depth effect.
 */
@Composable
fun SwipeableCardStack(
    options: List<Option>,
    currentIndex: Int,
    onSwipe: (kept: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val swipeThreshold = screenWidth.value * SWIPE_THRESHOLD_FRACTION

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Render cards back-to-front (behind cards first)
        val visibleRange = currentIndex until minOf(currentIndex + STACK_VISIBLE_CARDS, options.size)
        visibleRange.reversed().forEach { index ->
            val offset = index - currentIndex

            if (offset == 0) {
                // Top card — swipeable
                key(options[index].id) {
                    SwipeableTopCard(
                        option = options[index],
                        screenWidthPx = screenWidth.value,
                        swipeThreshold = swipeThreshold,
                        onSwipe = onSwipe
                    )
                }
            } else {
                // Behind cards — static, scaled down
                val scale = 1f - (offset * BEHIND_CARD_SCALE_STEP)
                val alphaValue = 1f - (offset * 0.2f)

                SwipeCard(
                    option = options[index],
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationY = offset * 20f
                        }
                        .alpha(alphaValue)
                )
            }
        }
    }
}

@Composable
private fun SwipeableTopCard(
    option: Option,
    screenWidthPx: Float,
    swipeThreshold: Float,
    onSwipe: (kept: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val rotationZ = (offsetX.value / screenWidthPx) * MAX_ROTATION_DEGREES
    val swipeProgress = (offsetX.value / swipeThreshold).coerceIn(-1f, 1f)

    SwipeCard(
        option = option,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .offset { IntOffset(offsetX.value.toInt(), 0) }
            .graphicsLayer { this.rotationZ = rotationZ }
            .pointerInput(option.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (abs(offsetX.value) > swipeThreshold) {
                                // Swipe committed — fly off screen
                                val targetX = if (offsetX.value > 0) screenWidthPx * 2 else -screenWidthPx * 2
                                offsetX.animateTo(
                                    targetValue = targetX,
                                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                                )
                                onSwipe(offsetX.value > 0) // right = kept
                            } else {
                                // Spring back to center
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
                                )
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                        }
                    }
                )
            }
    )
}
