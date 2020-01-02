package com.game.asura

interface HeroPower:DrawableCard {

    fun isActive(): Boolean

    fun activate()

    fun deactivate()
}