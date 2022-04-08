package externals.virtualDom

import org.w3c.dom.Element
import kotlin.js.Json

@JsModule("virtual-dom/vnode/vnode")
@JsNonModule
external interface VNode

@JsModule("virtual-dom/h")
@JsNonModule
external fun h(selector: String, properties: Json = definedExternally, children: Array<dynamic> = definedExternally): VNode

@JsModule("virtual-dom/create-element")
@JsNonModule
external fun createElement(vNode: VNode): Element

external interface Patches

@JsModule("virtual-dom/diff")
@JsNonModule
external fun diff(a: VNode, b: VNode): Patches

@JsModule("virtual-dom/patch")
@JsNonModule
external fun patch(element: Element, patches: Patches): Element