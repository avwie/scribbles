import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoClass.hover
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

@Composable
fun Button(label: String, onClick: () -> Unit = {}) {
    org.jetbrains.compose.web.dom.Button({
        classes("btn", "btn-primary")
        onClick { onClick() }
    }) {
        Text(label)
    }
}

@Composable fun EditableTitle(value: String, onTitleUpdate: (String) -> Unit = {}) {
    var editMode by remember { mutableStateOf(false) }
    val nativeElement = remember { mutableStateOf<HTMLInputElement?>(null) }
    val hover = remember { mutableStateOf(false) }

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
                nativeElement.value?.value?.also(onTitleUpdate)
                nativeElement.value?.blur()
            }
        }
    }

    Input(InputType.Text) {
        placeholder("Enter list name...")
        defaultValue(value)
        classes("form-control", "fs-3", "text-primary")
        refState(nativeElement)
        hoverState(hover)

        if (!editMode) {
            classes("bg-white", "border-top-0", "border-start-0", "border-end-0", "border-2")
            if (hover.value) {
                classes("border-primary")
            } else {
                classes("border-white")
            }
            style {
                cursor("text")
                borderRadius(0.em)
            }
            readOnly()
        } else {
            style {
                property("box-shadow", "none")
                border(0.em)
            }
            onKeyUp { evt ->
                if (evt.key == "Enter") {
                    editMode = false
                }
            }
        }
    }
}