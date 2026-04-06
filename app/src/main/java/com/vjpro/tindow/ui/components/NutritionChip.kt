package com.vjpro.tindow.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NutritionChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .border(1.dp, color, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
