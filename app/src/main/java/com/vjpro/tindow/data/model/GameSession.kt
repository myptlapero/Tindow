package com.vjpro.tindow.data.model

enum class GameMode { SOLO, DUO }
enum class PlayerTurn { PLAYER_1, PLAYER_2 }

/** Holds the full state of a game session across rounds */
data class GameSession(
    val mode: GameMode = GameMode.SOLO,
    val topic: Topic? = null,
    val customTopic: String? = null,
    val allOptions: List<Option> = emptyList(),
    val currentRound: Int = 1,
    val currentTurn: PlayerTurn = PlayerTurn.PLAYER_1,
    /** Options remaining in the current round (to be swiped) */
    val currentRoundOptions: List<Option> = emptyList(),
    /** Index of the card currently shown for swiping */
    val currentCardIndex: Int = 0,
    /** Survivors accumulated during the current round */
    val currentRoundSurvivors: List<Option> = emptyList(),
    /** P1's final survivors (duo mode) */
    val player1Survivors: List<Option> = emptyList(),
    /** P2's final survivors (duo mode) */
    val player2Survivors: List<Option> = emptyList(),
    /** Whether the tournament has concluded */
    val isFinished: Boolean = false
)
