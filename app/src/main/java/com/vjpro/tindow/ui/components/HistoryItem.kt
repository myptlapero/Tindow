package com.vjpro.tindow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vjpro.tindow.domain.model.SuggestionHistory
import com.vjpro.tindow.ui.theme.GreenPrimary
import com.vjpro.tindow.ui.theme.TextPrimary
import com.vjpro.tindow.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryItem(
    history: SuggestionHistory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.mealNames.joinToString(", "),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatTimestamp(history.timestamp),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${history.meals.size} dishes",
                    fontSize = 12.sp,
                    color = GreenPrimary
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                "Detail",
                tint = TextSecondary
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = timestamp }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    return when {
        isSameDay(now, date) -> "Today, ${timeFormat.format(Date(timestamp))}"
        isYesterday(now, date) -> "Yesterday, ${timeFormat.format(Date(timestamp))}"
        else -> SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
    a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

private fun isYesterday(now: Calendar, date: Calendar): Boolean {
    val yesterday = now.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return isSameDay(yesterday, date)
}
