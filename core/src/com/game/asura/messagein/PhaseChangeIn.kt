package com.game.asura.messagein

import com.game.asura.Phase
import com.game.asura.parsing.DecodedMessage

data class PhaseChangeIn(val nextPhase: Phase) : DecodedMessage