package nl.avwie.common.coroutines

import kotlinx.coroutines.flow.MutableSharedFlow
import nl.avwie.common.UUID
import nl.avwie.common.uuid

interface DistributableMutableSharedFlow<T> : MutableSharedFlow<T>, Distributable

fun <T> MutableSharedFlow<T>.asDistributable(clientId: UUID = uuid()): DistributableMutableSharedFlow<T> =
    DistributableMutableSharedFlowImpl(clientId, this)

private class DistributableMutableSharedFlowImpl<T>(
    override val clientId: UUID,
    private val flow: MutableSharedFlow<T>
) : DistributableMutableSharedFlow<T>, MutableSharedFlow<T> by flow