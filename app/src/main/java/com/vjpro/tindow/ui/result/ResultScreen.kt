package com.vjpro.tindow.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vjpro.tindow.data.model.GameMode
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.domain.TournamentEngine
import com.vjpro.tindow.ui.GameViewModel
import com.vjpro.tindow.ui.theme.PastelBgGradientEnd
import com.vjpro.tindow.ui.theme.PastelBgLight
import com.vjpro.tindow.ui.theme.PastelLavender
import com.vjpro.tindow.ui.theme.PastelOrange

@Composable
fun ResultScreen(
    viewModel: GameViewModel,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val session by viewModel.session.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(PastelBgLight, PastelBgGradientEnd))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            when (session.mode) {
                GameMode.SOLO -> SoloResult(survivors = session.currentRoundSurvivors)
                GameMode.DUO -> DuoResult(
                    player1Survivors = session.player1Survivors,
                    player2Survivors = session.player2Survivors,
                    allOptions = session.allOptions
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelOrange,
                    contentColor = Color.White
                )
            ) {
                Text("Chơi lại", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Về trang chủ", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SoloResult(survivors: List<Option>) {
    val winner = survivors.firstOrNull()

    if (survivors.isEmpty()) {
        Text(text = "\uD83E\uDD37", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bạn đã loại hết tất cả!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thử lại với các lựa chọn khác?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        return
    }

    Text(text = "\uD83C\uDF89", fontSize = 72.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = if (survivors.size == 1) "Người chiến thắng!" else "Top lựa chọn!",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (survivors.size == 1 && winner != null) {
        WinnerCard(option = winner)
    } else {
        Text(
            text = "${survivors.size} lựa chọn còn lại",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(survivors, key = { it.id }) { option ->
                SurvivorCard(option = option)
            }
        }
    }
}

@Composable
private fun DuoResult(
    player1Survivors: List<Option>,
    player2Survivors: List<Option>,
    allOptions: List<Option>
) {
    val matches = TournamentEngine.findMatches(player1Survivors, player2Survivors)

    if (matches.isNotEmpty()) {
        Text(text = "\u2764\uFE0F", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (matches.size == 1) "Trùng khớp!" else "Trùng ${matches.size} lựa chọn!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(matches, key = { it.id }) { option ->
                SurvivorCard(option = option)
            }
        }
    } else {
        Text(text = "\uD83D\uDE22", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Không trùng khớp!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thử lại với các lựa chọn khác?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        val closest = TournamentEngine.findClosestOptions(allOptions, player1Survivors, player2Survivors)
        if (closest.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Gần nhất:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                items(closest, key = { it.id }) { option ->
                    SurvivorCard(option = option)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PlayerScoreChip(label = "NC1 giữ", count = player1Survivors.size)
        PlayerScoreChip(label = "NC2 giữ", count = player2Survivors.size)
    }
}

@Composable
private fun PlayerScoreChip(label: String, count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = PastelLavender.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                "$count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WinnerCard(option: Option) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (option.imageUri != null) {
                    AsyncImage(
                        model = option.imageUri,
                        contentDescription = option.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(PastelBgLight, PastelBgGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\uD83C\uDF7D\uFE0F", fontSize = 64.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                option.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SurvivorCard(option: Option) {
    Card(
        modifier = Modifier.size(width = 130.dp, height = 160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                if (option.imageUri != null) {
                    AsyncImage(
                        model = option.imageUri,
                        contentDescription = option.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("\uD83C\uDF7D\uFE0F", fontSize = 32.sp)
                }
            }
            Text(
                text = option.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
