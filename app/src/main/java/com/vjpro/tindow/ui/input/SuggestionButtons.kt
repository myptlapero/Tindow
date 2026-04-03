package com.vjpro.tindow.ui.input

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vjpro.tindow.data.api.GeoapifyConfig
import com.vjpro.tindow.data.api.GeminiConfig
import com.vjpro.tindow.data.api.RetrofitClient
import com.vjpro.tindow.data.api.buildSuggestionPrompt
import com.vjpro.tindow.data.api.getBestImageUrl
import com.vjpro.tindow.data.api.parseGeminiSuggestions
import com.vjpro.tindow.data.api.toOption
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.api.PexelsConfig
import kotlinx.coroutines.launch

/**
 * All API suggestion buttons: TheMealDB, Geoapify, Gemini AI.
 * Each button only shows if the corresponding API is configured (or always for TheMealDB).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionButtons(
    existingOptionIds: Set<String>,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit,
    onAddOptions: (List<Option>) -> Unit,
    onError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Text(
        text = "— hoặc nhận gợi ý —",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TheMealDB — always available
        FilledTonalButton(
            onClick = {
                scope.launch {
                    onLoadingChange(true)
                    try {
                        val response = RetrofitClient.mealApi.getRandomMeal()
                        response.meals?.firstOrNull()?.let { meal ->
                            onAddOptions(listOf(meal.toOption()))
                        }
                    } catch (e: Exception) {
                        onError("Lỗi tải món: ${e.message}")
                    }
                    onLoadingChange(false)
                }
            },
            enabled = !isLoading
        ) { Text("\uD83C\uDF55 Món ngẫu nhiên") }

        FilledTonalButton(
            onClick = {
                scope.launch {
                    onLoadingChange(true)
                    try {
                        val meals = (1..5).mapNotNull {
                            try {
                                RetrofitClient.mealApi.getRandomMeal().meals?.firstOrNull()
                            } catch (_: Exception) { null }
                        }
                        val newOptions = meals.map { it.toOption() }
                            .filter { it.id !in existingOptionIds }
                        onAddOptions(newOptions)
                    } catch (e: Exception) {
                        onError("Lỗi tải món: ${e.message}")
                    }
                    onLoadingChange(false)
                }
            },
            enabled = !isLoading
        ) { Text("\uD83C\uDFB2 x5 Món") }

        // Geoapify — only if configured
        if (GeoapifyConfig.isConfigured()) {
            NearbyPlacesButton(
                isLoading = isLoading,
                existingOptionIds = existingOptionIds,
                onLoadingChange = onLoadingChange,
                onAddOptions = onAddOptions,
                onError = onError
            )
        }

        // Gemini AI — only if configured
        if (GeminiConfig.isConfigured()) {
            AiSuggestButton(
                isLoading = isLoading,
                existingOptionIds = existingOptionIds,
                onLoadingChange = onLoadingChange,
                onAddOptions = onAddOptions,
                onError = onError
            )
        }
    }

    if (isLoading) {
        Spacer(modifier = Modifier.height(8.dp))
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}

/** Geoapify nearby places button — requests location permission */
@Composable
private fun NearbyPlacesButton(
    isLoading: Boolean,
    existingOptionIds: Set<String>,
    onLoadingChange: (Boolean) -> Unit,
    onAddOptions: (List<Option>) -> Unit,
    onError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            scope.launch {
                fetchNearbyPlaces(context, existingOptionIds, onLoadingChange, onAddOptions, onError)
            }
        } else {
            onError("Quyền vị trí bị từ chối")
        }
    }

    FilledTonalButton(
        onClick = {
            locationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        enabled = !isLoading
    ) { Text("\uD83D\uDCCD Địa điểm gần") }
}

@SuppressLint("MissingPermission")
private suspend fun fetchNearbyPlaces(
    context: Context,
    existingOptionIds: Set<String>,
    onLoadingChange: (Boolean) -> Unit,
    onAddOptions: (List<Option>) -> Unit,
    onError: (String) -> Unit
) {
    onLoadingChange(true)
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location == null) {
            onError("Không lấy được vị trí. Thử lại.")
            onLoadingChange(false)
            return
        }

        val filter = GeoapifyConfig.circleFilter(location.latitude, location.longitude)
        val response = RetrofitClient.geoapifyApi.getNearbyPlaces(filter = filter)
        val options = response.features
            ?.mapNotNull { it.toOption() }
            ?.filter { it.id !in existingOptionIds }
            ?: emptyList()

        if (options.isEmpty()) {
            onError("Không tìm thấy địa điểm gần đây")
        } else {
            onAddOptions(options)
        }
    } catch (e: Exception) {
        onError("Lỗi tải địa điểm: ${e.message}")
    }
    onLoadingChange(false)
}

/** Gemini AI suggestion button — shows prompt dialog */
@Composable
private fun AiSuggestButton(
    isLoading: Boolean,
    existingOptionIds: Set<String>,
    onLoadingChange: (Boolean) -> Unit,
    onAddOptions: (List<Option>) -> Unit,
    onError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var promptText by remember { mutableStateOf("") }

    FilledTonalButton(
        onClick = { showDialog = true },
        enabled = !isLoading
    ) { Text("\u2728 AI Gợi ý") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Gợi ý AI") },
            text = {
                Column {
                    Text("Bạn đang tìm gì?", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        placeholder = { Text("VD: Đồ ăn Việt, Hoạt động cuối tuần...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val query = promptText.trim().ifEmpty { "food options" }
                        promptText = ""
                        scope.launch {
                            onLoadingChange(true)
                            try {
                                val request = buildSuggestionPrompt(query, extraDescription = null)
                                val response = RetrofitClient.geminiApi.generateContent(request = request)
                                val options = parseGeminiSuggestions(response)
                                    .filter { it.id !in existingOptionIds }
                                if (options.isEmpty()) {
                                    onError("AI không có gợi ý")
                                } else {
                                    // Auto-fetch Pexels images for AI suggestions
                                    val enriched = options.map { option ->
                                        if (PexelsConfig.isConfigured()) {
                                            try {
                                                val img = RetrofitClient.pexelsApi
                                                    .searchPhotos(query = option.title)
                                                    .photos?.firstOrNull()?.getBestImageUrl()
                                                option.copy(imageUri = img)
                                            } catch (_: Exception) { option }
                                        } else option
                                    }
                                    onAddOptions(enriched)
                                }
                            } catch (e: Exception) {
                                onError("Lỗi gợi ý AI: ${e.message}")
                            }
                            onLoadingChange(false)
                        }
                    },
                    enabled = promptText.isNotBlank()
                ) { Text("Tạo") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Huỷ") }
            }
        )
    }
}
