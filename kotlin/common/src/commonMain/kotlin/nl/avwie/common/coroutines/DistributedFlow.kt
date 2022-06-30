package nl.avwie.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.common.uuid

fun <T> MutableSharedFlow<T>.distributeIn(
    scope: CoroutineScope,
    flow: MutableSharedFlow<Distributed<T>>
): MutableSharedFlow<Distributed<T>> {
    val clientId = uuid()

    scope.launch {
        flow.onEach { distributed ->
            println("$clientId: Receiving distributed $distributed")
            if (distributed.clientId != clientId) {
                //launch {
                    this@distributeIn.emit(distributed.contents)
                //}
            }
        }.launchIn(this)
    }

    scope.launch {
        this@distributeIn.onEach { item ->
            val message = Distributed(clientId = clientId, contents = item)
            launch {
                println("$clientId: Sending $message")
                flow.emit(message)
            }
        }.launchIn(this)
    }

    return flow
}

fun <T> MutableSharedFlow<T>.distribute(
    scope: CoroutineScope
) = distributeIn(scope, MutableSharedFlow())