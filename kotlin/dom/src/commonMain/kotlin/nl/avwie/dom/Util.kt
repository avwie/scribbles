package nl.avwie.dom

fun xml(
    root: String,
    namespace: String?,
    vararg args: Pair<String?, Any?>?,
    block: DefinitionBuilderScope.() -> Unit = {}
): Definition {
    return Definition.build(namespace) {
        root.invoke(*args, block=block)
    }
}

fun xml(root: String, block: DefinitionBuilderScope.() -> Unit = {}) = xml(root, namespace=null, args= arrayOf(), block = block)
fun xml(root: String, vararg  args: Pair<String?, Any?>?, block: DefinitionBuilderScope.() -> Unit = {}) = xml(root, namespace=null, args=args, block = block)

fun svg(
    width: Int?,
    height: Int?,
    viewBox: Boolean,
    block: DefinitionBuilderScope.() -> Unit = {}
): Definition {
    return Definition.build("http://www.w3.org/2000/svg") {
        "svg"(
            "xmlns" to "http://www.w3.org/2000/svg",
            "width" to width?.toString(),
            "height" to height?.toString(),
            if (viewBox && width != null && height != null) "viewBox" to "0 0 $width $height" else null,
            block = block
        )
    }
}