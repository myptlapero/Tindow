package com.vjpro.tindow.ui.swipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vjpro.tindow.data.model.GameMode
import com.vjpro.tindow.data.model.PlayerTurn
import com.vjpro.tindow.domain.TournamentEngine
import com.vjpro.tindow.ui.GameViewModel
import com.vjpro.tindow.ui.theme.PastelBgGradientEnd
import com.vjpro.tindow.ui.theme.PastelBgLight
import com.vjpro.tindow.ui.theme.PastelCoral
import com.vjpro.tindow.ui.theme.PastelMint
import com.vjpro.tindow.ui.theme.PastelOrange

@Composable
fun SwipeScreen(
    viewModel: GameViewModel,
    onFinished: () -> Unit,
    onBack: () -> Unit
) {
    val session by viewModel.session.collectAsState()
    var showRoundCompleteDialog by remember { mutableStateOf(false) }
    var showPassPhoneScreen by remember { mutableStateOf(false) }

    val options = session.currentRoundOptions
    val currentIndex = session.currentCardIndex
    val isRoundDone = currentIndex >= options.size && options.isNotEmpty()

    LaunchedEffect(isRoundDone, session.isFinished, session.currentTurn) {
        if (isRoundDone && !showRoundCompleteDialog && !showPassPhoneScreen) {
            if (session.isFinished) {
                onFinished()
            } else if (session.mode == GameMode.DUO && session.currentTurn == PlayerTurn.PLAYER_1) {
                showPassPhoneScreen = true
            } else {
                showRoundCompleteDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(PastelBgLight, PastelBgGradientEnd))
            )
    ) {
        if (showPassPhoneScreen) {
            PassPhoneScreen(
                onReady = {
                    showPassPhoneScreen = false
                    viewModel.switchToPlayer2()
                }
            )
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundHeader(
                round = session.currentRound,
                current = minOf(currentIndex + 1, options.size),
                total = options.size,
                playerTurn = if (session.mode == GameMode.DUO) session.currentTurn else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (currentIndex < options.size) {
                SwipeableCardStack(
                    options = options,
                    currentIndex = currentIndex,
                    onSwipe = { kept -> viewModel.swipe(kept) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwipeHintLabel(
                    icon = Icons.Default.ThumbDown,
                    label = "BỎ",
                    tint = PastelCoral
                )
                SwipeHintLabel(
                    icon = Icons.Default.ThumbUp,
                    label = "THÍCH",
                    tint = PastelMint,
                    iconOnRight = true
                )
            }
        }

        if (showRoundCompleteDialog) {
            val survivors = session.currentRoundSurvivors
            val isComplete = TournamentEngine.isTournamentComplete(survivors)

            RoundCompleteDialog(
                survivorCount = survivors.size,
                round = session.currentRound,
                isFinal = isComplete,
                onNextRound = {
                    showRoundCompleteDialog = false
                    viewModel.nextRound()
                },
                onFinish = {
                    showRoundCompleteDialog = false
                    onFinished()
                }
            )
        }
    }
}

@Composable
private fun SwipeHintLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    iconOnRight: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (!iconOnRight) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        }
        Text(
            text = label,
            color = tint,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
        if (iconOnRight) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun RoundHeader(
    round: Int,
    current: Int,
    total: Int,
    playerTurn: PlayerTurn?
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (playerTurn != null) {
                Text(
                    text = if (playerTurn == PlayerTurn.PLAYER_1) "Người chơi 1" else "Người chơi 2",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "Vòng $round",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$current / $total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RoundCompleteDialog(
    survivorCount: Int,
    round: Int,
    isFinal: Boolean,
    onNextRound: () -> Unit,
    onFinish: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (isFinal) "Đã có kết quả!" else "Vòng $round hoàn tất!",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isFinal) {
                    "Lựa chọn đã được quyết định!"
                } else {
                    "$survivorCount lựa chọn còn lại. Tiếp tục lọc?"
                }
            )
        },
        confirmButton = {
            if (isFinal) {
                Button(
                    onClick = onFinish,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelOrange)
                ) { Text("Xem kết quả") }
            } else {
                Button(
                    onClick = onNextRound,
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Vòng tiếp") }
            }
        },
        dismissButton = {
            if (!isFinal) {
                OutlinedButton(
                    onClick = onFinish,
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Chọn từ danh sách còn lại") }
            }
        }
    )
}

@Composable
private fun PassPhoneScreen(onReady: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
            .clickable { onReady() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = "Pass phone",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Đưa điện thoại cho Người chơi 2",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Chạm vào màn hình khi sẵn sàng",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}
