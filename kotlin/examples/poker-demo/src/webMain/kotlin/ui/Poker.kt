package ui

import AppViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable fun PokerApp(model: AppViewModel) {
    val state by model.state

    H1 {
        Text(state.name.value)
    }
}