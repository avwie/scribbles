import androidx.compose.runtime.collectAsState
import common.mvi.ActionReducer
import common.mvi.EffectHandler
import common.mvi.Store
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

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

fun main() {
    val store = Store(0, actionReducer, effectHandler)

    renderComposable("root") {
        val count = store.state.collectAsState()
        Div(attrs = {
            classes("d-flex", "justify-content-center", "align-items-center", "vw-100", "vh-100")
        }) {
            Div {
                Div(attrs = { classes("card", "m-2") }) {
                    Div(attrs = { classes("card-body") }) {
                        Text(count.value.toString())
                    }
                }

                Button(attrs = {
                    type(ButtonType.Button)
                    classes("btn", "btn-primary", "m-2")
                    onClick {
                        store.dispatchAction(Action.Increase)
                    }
                }) {
                    Text("Increase")
                }

                Button(attrs = {
                    type(ButtonType.Button)
                    classes("btn", "btn-danger", "m-2")
                    onClick {
                        store.dispatchEffect(Effect.DelayedReset(count.value * 1000L))
                    }
                }) {
                    Text("Reset after ${count.value} seconds")
                }

                Button(attrs = {
                    type(ButtonType.Button)
                    classes("btn", "btn-primary", "m-2")
                    onClick {
                        store.dispatchAction(Action.Decrease)
                    }
                }) {
                    Text("Decrease")
                }
            }
        }
    }
}