package com.game.asura

enum class ConnectionStatus(val value: Byte) {
    UNKNOWN('0'.toByte()),
    CONNECTED('1'.toByte()),
    DISCONNECTED('2'.toByte());

    companion object {
        private val fieldNumberToName: Map<Byte, ConnectionStatus> = values().map { it.value to it }.toMap()
        fun getConnectionStatus(value: Byte): ConnectionStatus {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}