package com.vjpro.tindow.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.ui.components.MealCard
import com.vjpro.tindow.ui.theme.BeigeBackground
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSuggest: (String) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onMealClick: (Meal) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Refresh history every time HomeScreen is displayed (e.g. after navigating back)
    LaunchedEffect(Unit) {
        viewModel.refreshHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(
                    "\uD83C\uDF7D NutriMeal AI",
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            },
            actions = {
                IconButton(onClick = onNavigateToHistory) {
                    Icon(Icons.Filled.History, "History", tint = GreenPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeBackground)
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "What do you want to eat today?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.textInput,
                onValueChange = { viewModel.onTextChanged(it) },
                placeholder = { Text("Enter dish name...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Search", tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val query = state.textInput.ifBlank { "Suggest popular dishes" }
                    onNavigateToSuggest(query)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("\u2728 Suggest Meals", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            if (state.recentHistory.isNotEmpty()) {
                Text(
                    "Recent Searches",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.recentHistory.forEach { query ->
                        AssistChip(
                            onClick = {
                                viewModel.onTextChanged(query)
                                onNavigateToSuggest(query)
                            },
                            label = { Text(query, maxLines = 1) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = GreenPrimary.copy(alpha = 0.1f),
                                labelColor = GreenPrimary
                            )
                        )
                    }
                }
            }

            if (state.recentMeals.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Recent Meals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.recentMeals.forEach { meal ->
                        MealCard(
                            meal = meal,
                            onClick = { onMealClick(meal) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
