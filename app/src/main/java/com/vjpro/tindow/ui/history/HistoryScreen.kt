package com.vjpro.tindow.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vjpro.tindow.domain.model.SuggestionHistory
import com.vjpro.tindow.ui.components.HistoryItem
import com.vjpro.tindow.ui.theme.BeigeBackground
import com.vjpro.tindow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onHistoryClick: (SuggestionHistory) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Suggestion History", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                if (history.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(Icons.Filled.DeleteSweep, "Clear all", tint = TextSecondary)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeBackground)
        )

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No suggestion history", color = TextSecondary, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(history) { item ->
                    HistoryItem(
                        history = item,
                        onClick = { onHistoryClick(item) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
