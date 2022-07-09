import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.builders.InputAttrsScope
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

@Composable fun Input(
    value: String,
    placeholder: String = "",
    resetAfterSubmit: Boolean = false,
    attrs: InputAttrsScope<String>.() -> Unit = {},
    onSubmit: (String) -> Unit = {},
) {
    var submit by remember { mutableStateOf(false) }
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

    LaunchedEffect(submit) {
        if (submit) {
            nativeElement.value?.value?.also(onSubmit)
            submit = false

            if (resetAfterSubmit) {
                nativeElement.value?.value = ""
                nativeElement.value?.focus()
                editMode = true
            }
        }
    }

    org.jetbrains.compose.web.dom.Input(InputType.Text) {
        attrs()
        classes(AppStyleSheet.responsiveInput)
        placeholder(placeholder)
        defaultValue(value)
        refState(nativeElement)

        when {
            editMode -> onKeyUp { evt ->
                if (evt.key == "Enter") {
                    editMode = false
                    submit = true
                }
            }
            else -> readOnly()
        }
    }
}

@Composable fun Separator() {
    Hr(attrs = { classes(AppStyleSheet.separator) })
}

@Composable fun Items(items: Iterable<String>) {
    items.forEachIndexed { index, item ->
        Item(item, index == items.count() - 1)
    }
}

@Composable fun Item(item: String, isLast: Boolean = false) {
    Div(attrs = { classes(AppStyleSheet.item) }) {
        Text(item)
    }
    if (!isLast) {
        Separator()
    }
}