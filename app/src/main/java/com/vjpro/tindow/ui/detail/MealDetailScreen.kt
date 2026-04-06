package com.vjpro.tindow.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.ui.components.NutritionChip
import com.vjpro.tindow.ui.theme.CalorieOrange
import com.vjpro.tindow.ui.theme.CarbYellow
import com.vjpro.tindow.ui.theme.FatRed
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.ProteinBlue
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealDetailScreen(
    meal: Meal,
    onBack: () -> Unit,
    onCookingStepClick: (Int) -> Unit,
    viewModel: MealDetailViewModel = hiltViewModel()
) {
    val checkedIngredients by viewModel.checkedIngredients.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image with back button
        Box(
            modifier = Modifier.fillMaxWidth().height(250.dp)
        ) {
            AsyncImage(
                model = meal.imageUrl.ifBlank { null },
                contentDescription = meal.name,
                modifier = Modifier.fillMaxSize()
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, "Back",
                    tint = Color.White, modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Title
            Text(
                text = meal.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            // Nutrition chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutritionChip("${meal.nutrition.calories} kcal", CalorieOrange)
                NutritionChip("${meal.nutrition.protein}g protein", ProteinBlue)
                NutritionChip("${meal.nutrition.carbs}g carb", CarbYellow)
                NutritionChip("${meal.nutrition.fat}g fat", FatRed)
            }

            Spacer(Modifier.height(12.dp))

            // Difficulty + time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(meal.difficulty, color = GreenPrimary, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(16.dp))
                Text("\u23F1 ${meal.cookingTime} min", color = TextSecondary)
            }

            Spacer(Modifier.height(24.dp))

            // Ingredients section
            Text(
                "Ingredients",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            meal.ingredients.forEachIndexed { index, ingredient ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = index in checkedIngredients,
                        onCheckedChange = { viewModel.toggleIngredient(index) },
                        colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                    )
                    Text(
                        "${ingredient.amount} ${ingredient.name}",
                        color = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Cooking steps section
            Text(
                "Cooking Guide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            meal.cookingSteps.forEachIndexed { index, step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onCookingStepClick(index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${step.stepNumber}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                step.title,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                step.description,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
