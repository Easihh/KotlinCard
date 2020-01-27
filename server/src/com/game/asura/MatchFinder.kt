package com.game.asura

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import kotlin.collections.HashMap
import kotlin.coroutines.resume

class MatchFinder {

    private data class PendingPlayer(val coRoutine: CancellableContinuation<Match>,
                                     val player: ServerPlayer)

    private val allMatch: MutableMap<Int, Match> = HashMap()
    private val pendingPlayer: Queue<PendingPlayer> = LinkedBlockingDeque()


    fun findMatch(matchId: Int?): Match? {
        return allMatch[matchId]
    }

    suspend fun addPlayer(player: ServerPlayer) =
            suspendCancellableCoroutine<Match> {
                pendingPlayer.add(PendingPlayer(it, player))

                if (pendingPlayer.size >= MAX_PLAYER_PER_MATCH) {

                    val player1 = pendingPlayer.poll()
                    val player2 = pendingPlayer.poll()
                    val match = Match(player1.player, player2.player)
                    allMatch[match.matchId] = match
                    player1.coRoutine.resume(match)
                    player2.coRoutine.resume(match)
                }
            }
}