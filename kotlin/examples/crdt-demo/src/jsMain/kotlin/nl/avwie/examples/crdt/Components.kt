package nl.avwie.examples.crdt

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.builders.InputAttrsScope
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement

@Composable fun FancyInput(
    initialValue: String,
    placeholder: String = "",
    resetAfterSubmit: Boolean = false,
    attrs: InputAttrsScope<String>.() -> Unit = {},
    onSubmit: (String) -> Unit = {},
) {
    var localValue by mutableStateOf(initialValue)
    var editMode by remember { mutableStateOf(false) }
    val nativeElement = remember { mutableStateOf<HTMLInputElement?>(null) }

    fun blur() {
        nativeElement.value?.blur()
    }

    fun select() {
        nativeElement.value?.select()
    }

    fun submit() {
        if (localValue != initialValue) {
            onSubmit(localValue)
            if (resetAfterSubmit) {
                localValue = ""
            }
            blur()
        }
    }

    onDocumentMouseEvent {
        when {
            it.target != nativeElement.value && editMode -> {
                submit()
                editMode = false
            }

            it.target == nativeElement.value && !editMode -> {
                editMode = true
                select()
            }
        }
    }

    Input(InputType.Text) {
        attrs()
        classes(AppStyleSheet.responsiveInput)
        placeholder(placeholder)
        refState(nativeElement)
        value(localValue)

        when {
            editMode -> {
                onInput { evt ->
                    localValue = evt.target.value
                }
                onKeyUp { evt ->
                    if (evt.key == "Enter") {
                        submit()
                    }
                }
            }
            else -> readOnly()
        }
    }
}

@Composable fun Separator() {
    Hr(attrs = { classes(AppStyleSheet.separator) })
}

@Composable fun Items(
    items: Map<String, Boolean>,
    onItemChecked: (item: String, value: Boolean) -> Unit = { _, _ -> },
    onItemDeleted: (item: String) -> Unit = {}
) {
    items.keys.forEachIndexed { index, item ->
        Item(
            item,
            isFinished = items[item]!!,
            isLast = index == items.count() - 1,
            onItemChecked=onItemChecked,
            onItemDeleted=onItemDeleted
        )
    }
}

@Composable fun Item(
    item: String,
    isFinished: Boolean,
    isLast: Boolean = false,
    onItemChecked: (item: String, value: Boolean) -> Unit = { _, _ -> },
    onItemDeleted: (item: String) -> Unit = {}
) {
    Div(attrs = { classes(AppStyleSheet.item) }) {
        ColContainer {
            Span(attrs = {
                classes("material-symbols-outlined")
                onClick {
                    onItemChecked(item, !isFinished)
                }
            }) {
                if (isFinished) Text("check_box")
                else Text("check_box_outline_blank")
            }
            P(attrs = {
                if (isFinished) classes(AppStyleSheet.finished)
            }) {
                Text(item)
            }
            Span(attrs = {
                classes("material-symbols-outlined")
                onClick {
                    onItemDeleted(item)
                }
            }) {
                Text("delete")
            }
        }
    }
    if (!isLast) {
        Separator()
    }
}

@Composable fun Title(title: String) {
    H1(attrs = { classes(AppStyleSheet.title) }) { Text(title) }
}