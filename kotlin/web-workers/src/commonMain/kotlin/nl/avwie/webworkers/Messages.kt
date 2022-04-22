package nl.avwie.webworkers

import kotlinx.serialization.Serializable

@Serializable sealed interface Message
@Serializable sealed interface Request<R> : Message
@Serializable sealed interface Response : Message

@Serializable object Ping : Request<Pong>
@Serializable object Pong : Response

@Serializable data class StringResponse(val payload: String) : Response
@Serializable data class Sarcasm(val payload: String) : Request<StringResponse>