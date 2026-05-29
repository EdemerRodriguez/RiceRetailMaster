package com.rodriguez.riceretailmaster.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
object RecoveryEvents {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun signal() {
        _events.tryEmit(Unit)
    }
}
