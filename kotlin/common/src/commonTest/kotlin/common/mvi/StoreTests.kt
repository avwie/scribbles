package common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTests {

    private val actionReducer = ActionReducer<String, String> { state, action ->
        when (action) {
            "uppercase" -> state.uppercase()
            "lowercase" -> state.lowercase()
            "reverse" -> state.reversed()
            else -> state
        }
    }

    private val effectHandler = EffectHandler<String, String, String> { _, effect, dispatcher ->
        when (effect) {
            "delayed_reverse" -> {
                delay(2_000)
                dispatcher.dispatchAction("reverse")
            }
            else -> {}
        }
    }

    @Test
    fun simple() = runTest {
        val store = Store("foo", actionReducer, effectHandler, coroutineContext = coroutineContext)
        assertEquals("foo", store.state.value)

        store.dispatchAction("uppercase")
        assertEquals("FOO", store.state.value)

        store.dispatchAction("lowercase")
        assertEquals("foo", store.state.value)

        store.dispatchEffect("delayed_reverse")
        advanceUntilIdle()
        assertEquals("oof", store.state.value)
    }
}