package common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTests {

    val actionReducer = ActionReducer<String, String> { state, action ->
        when (action) {
            "uppercase" -> state.uppercase()
            "lowercase" -> state.lowercase()
            "reverse" -> state.reversed()
            else -> state
        }
    }

    val effectReducer = EffectReducer<String, String, String> { state, effect ->
        when (effect) {
            "delayed_reverse" -> {
                delay(1_000)
                EffectReducer.Result("reverse", listOf())
            }
            else -> EffectReducer.Result("none", listOf())
        }
    }

    @Test
    fun simple() = runTest {
        val store = Store("foo", actionReducer, effectReducer)
        assertEquals("foo", store.state.value)

        store.dispatchAction("uppercase")
        assertEquals("FOO", store.state.value)

        store.dispatchAction("lowercase")
        assertEquals("foo", store.state.value)

        store.dispatchEffect("delayed_reverse")
        assertEquals("oof", store.state.value)
    }
}