package nl.avwie.vdom

class Renderer(val target: RenderTarget<*>) {

    fun render(node: Node) {
        target.reset()
        renderVNode(node)
    }

    private fun renderVNode(node: Node) {
        node.toDefinition().write(target)
    }
}