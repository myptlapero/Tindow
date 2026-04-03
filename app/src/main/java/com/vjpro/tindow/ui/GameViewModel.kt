package com.vjpro.tindow.ui

import androidx.lifecycle.ViewModel
import com.vjpro.tindow.data.model.GameMode
import com.vjpro.tindow.data.model.GameSession
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.model.PlayerTurn
import com.vjpro.tindow.data.model.Topic
import com.vjpro.tindow.domain.TournamentEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Shared ViewModel managing game session state across all screens */
class GameViewModel : ViewModel() {

    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()

    fun setMode(mode: GameMode) {
        _session.update { it.copy(mode = mode) }
    }

    fun setTopic(topic: Topic) {
        _session.update { it.copy(topic = topic, customTopic = null) }
    }

    fun setCustomTopic(customTopic: String) {
        _session.update { it.copy(topic = null, customTopic = customTopic) }
    }

    fun addOption(option: Option) {
        _session.update { it.copy(allOptions = it.allOptions + option) }
    }

    fun addOptions(options: List<Option>) {
        _session.update { it.copy(allOptions = it.allOptions + options) }
    }

    fun removeOption(optionId: String) {
        _session.update { s -> s.copy(allOptions = s.allOptions.filter { it.id != optionId }) }
    }

    /** Initialize the first round and prepare cards for swiping */
    fun startGame() {
        val options = TournamentEngine.getOptionsForRound(
            allOptions = _session.value.allOptions,
            previousSurvivors = null,
            round = 1
        )
        _session.update {
            it.copy(
                currentRound = 1,
                currentRoundOptions = options,
                currentCardIndex = 0,
                currentRoundSurvivors = emptyList(),
                currentTurn = PlayerTurn.PLAYER_1,
                player1Survivors = emptyList(),
                player2Survivors = emptyList(),
                isFinished = false
            )
        }
    }

    /** Process a swipe decision on the current card */
    fun swipe(kept: Boolean) {
        val s = _session.value
        if (s.currentCardIndex >= s.currentRoundOptions.size) return

        val currentOption = s.currentRoundOptions[s.currentCardIndex]
        val newSurvivors = if (kept) s.currentRoundSurvivors + currentOption else s.currentRoundSurvivors

        val nextIndex = s.currentCardIndex + 1
        val roundComplete = TournamentEngine.isRoundComplete(s.currentRoundOptions.size, nextIndex)

        _session.update {
            it.copy(
                currentCardIndex = nextIndex,
                currentRoundSurvivors = newSurvivors
            )
        }

        if (roundComplete) {
            onRoundComplete(newSurvivors)
        }
    }

    private fun onRoundComplete(survivors: List<Option>) {
        val s = _session.value

        if (s.mode == GameMode.DUO && s.currentTurn == PlayerTurn.PLAYER_1) {
            // Save P1 survivors, prepare for P2
            _session.update {
                it.copy(player1Survivors = survivors)
            }
            return
        }

        if (s.mode == GameMode.DUO && s.currentTurn == PlayerTurn.PLAYER_2) {
            // Save P2 survivors, tournament done for duo
            _session.update {
                it.copy(
                    player2Survivors = survivors,
                    isFinished = true
                )
            }
            return
        }

        // Solo mode: check if tournament complete
        if (TournamentEngine.isTournamentComplete(survivors)) {
            _session.update { it.copy(isFinished = true) }
        }
    }

    /** Start next round with current survivors (solo mode) */
    fun nextRound() {
        val s = _session.value
        val survivors = s.currentRoundSurvivors.toList()
        val nextRoundNum = s.currentRound + 1
        val options = TournamentEngine.getOptionsForRound(
            allOptions = s.allOptions,
            previousSurvivors = survivors,
            round = nextRoundNum
        )
        _session.update {
            it.copy(
                currentRound = nextRoundNum,
                currentRoundOptions = options,
                currentCardIndex = 0,
                currentRoundSurvivors = emptyList()
            )
        }
    }

    /** Switch to Player 2's turn (duo mode) — resets swipe state with same options */
    fun switchToPlayer2() {
        val s = _session.value
        val options = TournamentEngine.getOptionsForRound(
            allOptions = s.allOptions,
            previousSurvivors = null,
            round = 1
        )
        _session.update {
            it.copy(
                currentTurn = PlayerTurn.PLAYER_2,
                currentRoundOptions = options,
                currentCardIndex = 0,
                currentRoundSurvivors = emptyList()
            )
        }
    }

    /** Get match results for duo mode */
    fun getMatches(): List<Option> {
        val s = _session.value
        return TournamentEngine.findMatches(s.player1Survivors, s.player2Survivors)
    }

    /** Reset everything for a new game */
    fun reset() {
        _session.value = GameSession()
    }
}
