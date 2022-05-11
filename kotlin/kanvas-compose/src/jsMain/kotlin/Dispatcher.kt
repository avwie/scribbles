import androidx.compose.runtime.compositionLocalOf

fun interface Dispatcher {
    fun dispatch(msg: Message)

    companion object {
        val NONE = Dispatcher {}
    }
}

val LocalDispatcher = compositionLocalOf { Dispatcher.NONE }