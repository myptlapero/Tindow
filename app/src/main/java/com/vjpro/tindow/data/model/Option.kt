package com.vjpro.tindow.data.model

import java.util.UUID

/** Source of where an option came from */
enum class OptionSource { MANUAL, MEAL_API, PLACE_API, AI }

/** A single choice option displayed as a swipeable card */
data class Option(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val imageUri: String? = null,
    val description: String? = null,
    val source: OptionSource = OptionSource.MANUAL
)
