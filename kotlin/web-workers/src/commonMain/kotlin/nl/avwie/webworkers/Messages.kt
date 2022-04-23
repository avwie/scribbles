package nl.avwie.webworkers

import kotlinx.serialization.Serializable

@Serializable sealed interface Message
@Serializable sealed interface Request<R : RequestResult> : Message
@Serializable sealed interface RequestResult : Message
@Serializable data class Response(val result: RequestResult? = null, val error: String? = null): Message

@Serializable data class Initialize(val workerId: String) : Request<Initialized>
@Serializable object Initialized : RequestResult

@Serializable data class PIApproximation(val iterations: Int) : Request<PIApproximationResult>
@Serializable data class PIApproximationResult(val pi: Double) : RequestResult