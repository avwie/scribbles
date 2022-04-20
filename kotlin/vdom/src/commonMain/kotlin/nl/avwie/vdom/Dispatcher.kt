package nl.avwie.vdom

fun interface Dispatcher<Msg> {
    fun dispatch(message: Msg)

    companion object {
        fun <Msg> none() = Dispatcher<Msg> { }
        fun <Msg> print() = Dispatcher<Msg> { message -> println("$message") }
    }
}