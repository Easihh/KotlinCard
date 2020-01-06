package com.game.asura

import com.game.asura.messaging.MessageField

data class ChangedField(val field: MessageField,
                        val value: Any)