package com.vjpro.tindow.ui.cooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.ui.theme.BeigeBackground
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingStepScreen(
    meal: Meal,
    initialStep: Int = 0,
    onBack: () -> Unit,
    viewModel: CookingStepViewModel = hiltViewModel()
) {
    val currentStepIndex by viewModel.currentStep.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    // Initialize step
    androidx.compose.runtime.LaunchedEffect(initialStep) {
        viewModel.setStep(initialStep)
    }

    val steps = meal.cookingSteps
    val step = steps.getOrNull(currentStepIndex) ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(meal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "Step ${currentStepIndex + 1}/${steps.size}",
                        fontSize = 12.sp, color = TextSecondary
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                // Timer
                if (step.duration > 0) {
                    if (isTimerRunning) {
                        Text(
                            viewModel.formatTimer(timerSeconds),
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        IconButton(onClick = { viewModel.stopTimer() }) {
                            Icon(Icons.Filled.Stop, "Stop", tint = Color.Red)
                        }
                    } else {
                        IconButton(onClick = { viewModel.startTimer(step.duration) }) {
                            Icon(Icons.Filled.PlayArrow, "Start timer", tint = GreenPrimary)
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeBackground)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Step badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${step.stepNumber}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                step.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (step.duration > 0) {
                Spacer(Modifier.height(4.dp))
                Text("\u23F1 ${step.duration} min", color = TextSecondary)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                step.description,
                fontSize = 15.sp,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            // Step-specific ingredients
            if (step.ingredients.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Ingredients needed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                step.ingredients.forEach { ingredient ->
                    var checked by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                        )
                        Text("${ingredient.amount} ${ingredient.name}", color = TextPrimary)
                    }
                }
            }
        }

        // Bottom navigation: prev / indicator / next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.previousStep() },
                enabled = currentStepIndex > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Previous")
            }

            // Page indicator dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentStepIndex) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentStepIndex) GreenPrimary
                                else GreenPrimary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (currentStepIndex < steps.size - 1) {
                        viewModel.nextStep(steps.size)
                    } else {
                        onBack()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(if (currentStepIndex < steps.size - 1) "Next" else "Done")
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next", modifier = Modifier.size(16.dp))
            }
        }
    }
}
