package com.game.asura

import com.game.asura.messagein.MatchInfoIn

class MatchHeroInfo {

    private val heroes: MutableMap<String, MatchInfoIn> = HashMap()

    fun getHeroInfo(key: String): MatchInfoIn? {
        return heroes[key]
    }


    fun addHeroInfo(key: String, value: MatchInfoIn) {
        heroes[key] = value
    }

    fun size(): Int {
        return heroes.size
    }

    override fun toString(): String {
        val sb = StringBuilder()
        heroes.forEach {
            sb.append("key=${it.key},accountName=${it.value.accountName},primaryHeroId=${it.value.primaryHeroId}" +
                    ",secondaryHeroId=${it.value.secondaryHeroId},cardType=${it.value.cardType}")
        }
        return sb.toString()
    }
}