sealed interface Action {
    object Increase : Action
    object Decrease : Action
    data class Set(val value: Int): Action
}