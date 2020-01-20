package com.game.asura

enum class LoginStatus(val value: Byte) {
    UNKNOWN('0'.toByte()),
    CONNECTED('1'.toByte()),
    DENIED('2'.toByte());

    companion object {
        private val FIELD_NUMBER_TO_NAME: Map<Byte, LoginStatus> = values().map { it.value to it }.toMap()
        fun getLoginStatus(value: Byte): LoginStatus {
            return FIELD_NUMBER_TO_NAME[value] ?: return UNKNOWN
        }
    }
}