package nl.avwie.dom

import externals.virtualDom.Patches
import externals.virtualDom.VNode
import externals.virtualDom.createElement
import org.w3c.dom.Element

fun Definition.toElement(): Element = BrowserWriter().also { writer ->
    this.write(writer)
}.result!!

fun Definition.toVNode(): VNode = VirtualDomWriter().also { writer ->
    this.write(writer)
}.result!!

fun VNode.toElement(): Element = createElement(this)
fun VNode.diff(other: VNode): Patches = externals.virtualDom.diff(this, other)
fun Element.patch(patches: Patches): Element = externals.virtualDom.patch(this, patches)