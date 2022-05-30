package common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTests {

    sealed interface Action {
        object Uppercase : Action
        object Lowercase: Action
        object Reverse: Action
    }

    sealed interface Effect {
        data class DelayedAction(val delay: Long, val action: Action) : Effect
    }

    private val actionReducer = ActionReducer<String, Action> { state, action ->
        when (action) {
            Action.Uppercase -> state.uppercase()
            Action.Lowercase -> state.lowercase()
            Action.Reverse -> state.reversed()
        }
    }

    private val effectHandler = EffectHandler<String, Action, Effect> { _, effect, dispatcher ->
        when (effect) {
            is Effect.DelayedAction -> {
                delay(effect.delay)
                dispatcher.dispatchAction(effect.action)
            }
        }
    }

    @Test
    fun simple() = runTest {
        val store = Store("foo", actionReducer, effectHandler)
        assertEquals("foo", store.state.value)

        store.dispatchAction(Action.Uppercase)
        assertEquals("FOO", store.state.value)

        store.dispatchAction(Action.Lowercase)
        assertEquals("foo", store.state.value)

        store.dispatchEffect(Effect.DelayedAction(2000, Action.Reverse))
        assertEquals("oof", store.state.value)
    }

    @Test
    fun testLaunch() = runTest {
        val store = Store("foo", actionReducer, effectHandler)

        launch {
            store.dispatchEffect(Effect.DelayedAction(2000, Action.Reverse))
            assertEquals("oof", store.state.value)
        }
        assertNotEquals("oof", store.state.value)
        advanceUntilIdle()
        assertEquals("oof", store.state.value)
    }
}