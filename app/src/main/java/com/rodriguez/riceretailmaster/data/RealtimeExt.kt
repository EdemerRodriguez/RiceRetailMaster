package com.rodriguez.riceretailmaster.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.UUID

fun SupabaseClient.tableChanges(table: String): Flow<Unit> = channelFlow {
    val ch = channel("rrm_${table}_${UUID.randomUUID()}")
    val changeFlow = ch.postgresChangeFlow<PostgresAction>(schema = "public") {
        this.table = table
    }
    val job = launch { changeFlow.collect { trySend(Unit) } }
    ch.subscribe()
    awaitClose {
        job.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { ch.unsubscribe() }
            runCatching { realtime.removeChannel(ch) }
        }
    }
}
