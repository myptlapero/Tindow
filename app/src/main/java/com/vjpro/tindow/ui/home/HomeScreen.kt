package com.vjpro.tindow.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vjpro.tindow.data.model.GameMode
import com.vjpro.tindow.ui.theme.PastelBgGradientEnd
import com.vjpro.tindow.ui.theme.PastelBgLight
import com.vjpro.tindow.ui.theme.PastelLavender
import com.vjpro.tindow.ui.theme.PastelOrange
import com.vjpro.tindow.ui.theme.PastelPurple

@Composable
fun HomeScreen(
    onStartGame: (GameMode) -> Unit
) {
    var selectedMode by remember { mutableStateOf(GameMode.SOLO) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PastelBgLight, PastelBgGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TINDOW",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PastelPurple,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Phân vân? Vuốt để quyết định!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "Chọn chế độ chơi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                ModeCard(
                    icon = Icons.Default.Person,
                    label = "Solo",
                    description = "Một mình",
                    selected = selectedMode == GameMode.SOLO,
                    onClick = { selectedMode = GameMode.SOLO }
                )
                ModeCard(
                    icon = Icons.Default.Group,
                    label = "Duo",
                    description = "Hai người",
                    selected = selectedMode == GameMode.DUO,
                    onClick = { selectedMode = GameMode.DUO }
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = { onStartGame(selectedMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Bắt đầu!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    icon: ImageVector,
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) PastelPurple else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderModifier = if (!selected) {
        Modifier.border(
            width = 1.5.dp,
            color = PastelLavender,
            shape = RoundedCornerShape(20.dp)
        )
    } else {
        Modifier
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .size(140.dp)
            .then(borderModifier),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 10.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}
