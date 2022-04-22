package nl.avwie.webworkers

import kotlinx.serialization.Serializable

@Serializable sealed interface Message

@Serializable sealed interface Request<R : Response> : Message
@Serializable sealed interface Response : Message

@Serializable data class Initialize(val workerId: String) : Request<Initialized>
@Serializable object Initialized : Response

@Serializable data class PIApproximation(val iterations: Int) : Request<PIApproximationResult>
@Serializable data class PIApproximationResult(val pi: Double) : Response