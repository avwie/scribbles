import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.w3c.dom.HTMLInputElement

@Composable fun Input(
    value: String,
    placeholder: String = "",
    onInputChange: (String) -> Unit = {}
) {
    var editMode by remember { mutableStateOf(false) }
    val nativeElement = remember { mutableStateOf<HTMLInputElement?>(null) }

    onDocumentMouseEvent {
        when {
            it.target != nativeElement.value && editMode -> {
                editMode = false
            }

            it.target == nativeElement.value && !editMode -> {
                editMode = true
            }
        }
    }

    LaunchedEffect(value) {
        nativeElement.value?.value = value
    }

    LaunchedEffect(editMode, nativeElement.value) {
        when {
            editMode -> nativeElement.value?.select()
            !editMode && nativeElement.value != null -> {
                nativeElement.value?.value?.also(onInputChange)
                nativeElement.value?.blur()
            }
        }
    }

    org.jetbrains.compose.web.dom.Input(InputType.Text) {
        classes(AppStyleSheet.responsiveInput)
        placeholder(placeholder)
        defaultValue(value)
        refState(nativeElement)

        when {
            editMode -> onKeyUp { evt ->
                if (evt.key == "Enter") editMode = false
            }
            else -> readOnly()
        }
    }
}