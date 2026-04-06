package com.vjpro.tindow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vjpro.tindow.domain.model.Meal
import com.vjpro.tindow.ui.theme.CalorieOrange
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = meal.imageUrl.ifBlank { null },
                contentDescription = meal.name,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = meal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )
                Text(
                    text = "\uD83D\uDD25 ${meal.calories} kcal",
                    fontSize = 13.sp,
                    color = CalorieOrange
                )
                Text(
                    text = "\u26A1 ${meal.difficulty}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = "\u23F1 ${meal.cookingTime} min",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = "View details \u2192",
                    fontSize = 13.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
