sealed interface Effect {
    data class DelayedReset(val ms: Long): Effect
}