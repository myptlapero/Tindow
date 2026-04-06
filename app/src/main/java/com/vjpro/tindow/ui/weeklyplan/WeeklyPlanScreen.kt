package com.vjpro.tindow.ui.weeklyplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.ui.components.DayTabRow
import com.vjpro.tindow.ui.components.MealSlotCard
import com.vjpro.tindow.ui.theme.BeigeBackground
import com.vjpro.tindow.ui.theme.CalorieOrange
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    onMealClick: (Meal) -> Unit,
    viewModel: WeeklyPlanViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text("Weekly Meal Plan", fontWeight = FontWeight.Bold)
            },
            actions = {
                IconButton(onClick = { viewModel.generateNewPlan() }) {
                    Icon(Icons.Filled.Refresh, "Refresh", tint = GreenPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeBackground)
        )

        when (val currentState = state) {
            is WeeklyPlanUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No meal plan yet",
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Create a 7-day meal plan with AI",
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.generateNewPlan() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("\u2728 Create Meal Plan")
                        }
                    }
                }
            }

            is WeeklyPlanUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GreenPrimary)
                        Spacer(Modifier.height(16.dp))
                        Text("Creating meal plan...", color = TextSecondary)
                    }
                }
            }

            is WeeklyPlanUiState.Success -> {
                val plan = currentState.plan
                val dayPlan = plan.days.getOrNull(selectedDay)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(Modifier.height(12.dp))

                    DayTabRow(
                        selectedIndex = selectedDay,
                        onDaySelected = { viewModel.selectDay(it) }
                    )

                    Spacer(Modifier.height(16.dp))

                    if (dayPlan != null) {
                        Text(
                            "\uD83D\uDD25 Total: ${dayPlan.totalCalories} kcal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CalorieOrange
                        )

                        Spacer(Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            MealSlotCard(
                                slotLabel = "Breakfast",
                                meal = dayPlan.breakfast,
                                onClick = { onMealClick(dayPlan.breakfast) }
                            )
                            MealSlotCard(
                                slotLabel = "Lunch",
                                meal = dayPlan.lunch,
                                onClick = { onMealClick(dayPlan.lunch) }
                            )
                            MealSlotCard(
                                slotLabel = "Dinner",
                                meal = dayPlan.dinner,
                                onClick = { onMealClick(dayPlan.dinner) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.generateNewPlan() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("\u2728 Regenerate Meal Plan", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            is WeeklyPlanUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            currentState.message,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.generateNewPlan() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
