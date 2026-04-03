package com.vjpro.tindow.domain

import com.vjpro.tindow.data.model.Option

/**
 * Pure-function engine for tournament filter logic.
 * Each round: user swipes all cards, survivors advance. Repeat until 1 remains.
 */
object TournamentEngine {

    /** Get options for a given round. Round 1 uses allOptions; subsequent rounds use previous survivors. */
    fun getOptionsForRound(
        allOptions: List<Option>,
        previousSurvivors: List<Option>?,
        round: Int
    ): List<Option> {
        return if (round == 1) allOptions.shuffled() else previousSurvivors?.shuffled() ?: emptyList()
    }

    /** Check if all cards in the round have been swiped */
    fun isRoundComplete(totalInRound: Int, swipedCount: Int): Boolean {
        return swipedCount >= totalInRound
    }

    /** Tournament ends when 0 or 1 survivor remains */
    fun isTournamentComplete(survivors: List<Option>): Boolean {
        return survivors.size <= 1
    }

    /** Get the winner (single survivor) or null if not yet decided */
    fun getWinner(survivors: List<Option>): Option? {
        return if (survivors.size == 1) survivors.first() else null
    }

    /**
     * For duo mode: find options both players kept.
     * Returns matched options preserving P1's order.
     */
    fun findMatches(
        player1Survivors: List<Option>,
        player2Survivors: List<Option>
    ): List<Option> {
        val p2Ids = player2Survivors.map { it.id }.toSet()
        return player1Survivors.filter { it.id in p2Ids }
    }

    /**
     * For duo mode no-match fallback: find "closest" options.
     * Options that survived the most rounds for either player.
     * Returns up to [limit] suggestions.
     */
    fun findClosestOptions(
        allOptions: List<Option>,
        player1Survivors: List<Option>,
        player2Survivors: List<Option>,
        limit: Int = 3
    ): List<Option> {
        val p1Ids = player1Survivors.map { it.id }.toSet()
        val p2Ids = player2Survivors.map { it.id }.toSet()
        // Score: 2 if in both, 1 if in either, 0 if neither
        return allOptions
            .sortedByDescending { option ->
                (if (option.id in p1Ids) 1 else 0) + (if (option.id in p2Ids) 1 else 0)
            }
            .take(limit)
    }
}
