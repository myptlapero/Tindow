package com.vjpro.tindow.data.model

/** Predefined decision topics with API mapping */
enum class Topic(
    val label: String,
    val emoji: String,
    val colorHex: Long,
    val apiSource: TopicApiSource,
    val aiPrompt: String
) {
    FOOD(
        label = "Ăn gì",
        emoji = "\uD83C\uDF5C",
        colorHex = 0xFFFF6B6B,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "các món ăn phổ biến"
    ),
    DRINK(
        label = "Uống gì",
        emoji = "\u2615",
        colorHex = 0xFF8B5CF6,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "các loại đồ uống phổ biến"
    ),
    PLACE(
        label = "Đi đâu",
        emoji = "\uD83D\uDCCD",
        colorHex = 0xFF06B6D4,
        apiSource = TopicApiSource.GEOAPIFY,
        aiPrompt = "các địa điểm vui chơi gần đây"
    ),
    ACTIVITY(
        label = "Làm gì",
        emoji = "\uD83C\uDFAF",
        colorHex = 0xFF10B981,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "các hoạt động vui cuối tuần"
    ),
    WATCH(
        label = "Xem gì",
        emoji = "\uD83C\uDFAC",
        colorHex = 0xFFF59E0B,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "phim và chương trình đang hot"
    ),
    SHOP(
        label = "Mua gì",
        emoji = "\uD83D\uDED2",
        colorHex = 0xFFEC4899,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "những món đồ hữu ích nên mua"
    ),
    GAME(
        label = "Chơi gì",
        emoji = "\uD83C\uDFAE",
        colorHex = 0xFF6366F1,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "các trò chơi vui nhộn"
    ),
    READ(
        label = "Đọc gì",
        emoji = "\uD83D\uDCDA",
        colorHex = 0xFF78716C,
        apiSource = TopicApiSource.GEMINI,
        aiPrompt = "những cuốn sách hay nên đọc"
    );
}

/** Which API to auto-fetch suggestions from */
enum class TopicApiSource { MEAL_DB, GEOAPIFY, GEMINI }
