package nl.avwie.common.coroutines

import nl.avwie.common.UUID
import nl.avwie.common.uuid

@kotlinx.serialization.Serializable
data class DistributedMessage<T>(
    override val clientId: UUID = uuid(),
    val contents: T
) : Distributable
