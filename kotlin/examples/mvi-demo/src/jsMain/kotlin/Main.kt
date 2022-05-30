import androidx.compose.runtime.*
import common.mvi.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val actionReducer = ActionReducer<Int, Action> { state, action ->
    when (action) {
        Action.Increase -> state + 1
        Action.Decrease -> state - 1
        is Action.Set -> action.value
    }
}

val effectHandler = EffectHandler<Int, Action, Effect> { state, effect, dispatcher ->
    when (effect) {
        is Effect.DelayedReset -> {
            delay(effect.ms)
            dispatcher.dispatchAction(Action.Set(0))
        }
    }
}

val store = Store(0, actionReducer, effectHandler)

fun main() {
    renderComposable("root") {
        CompositionLocalProvider(LocalDispatcher provides store) {
            val dispatch = rememberDispatcher()
            val count by store.state.collectAsState()

            Div(attrs = {
                classes("d-flex", "justify-content-center", "align-items-center", "vw-100", "vh-100")
            }) {
                Div {
                    Div(attrs = { classes("card", "m-2") }) {
                        Div(attrs = { classes("card-body") }) {
                            Text(count.toString())
                        }
                    }

                    Button(attrs = {
                        type(ButtonType.Button)
                        classes("btn", "btn-primary", "m-2")
                        onClick {
                            dispatch(Action.Increase)
                        }
                    }) {
                        Text("Increase")
                    }

                    Button(attrs = {
                        type(ButtonType.Button)
                        classes("btn", "btn-danger", "m-2")
                        onClick {
                            dispatch(Effect.DelayedReset(count * 1000L))
                        }
                    }) {
                        Text("Reset after ${count} seconds")
                    }

                    Button(attrs = {
                        type(ButtonType.Button)
                        classes("btn", "btn-primary", "m-2")
                        onClick {
                            dispatch(Action.Decrease)
                        }
                    }) {
                        Text("Decrease")
                    }
                }
            }
        }
    }
}