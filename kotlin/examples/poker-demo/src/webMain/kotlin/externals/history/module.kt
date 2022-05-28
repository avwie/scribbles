@file:JsNonModule
@file:JsModule("history")

package externals.history

import kotlin.js.*


external enum class Action {
    Pop /* = "POP" */,
    Push /* = "PUSH" */,
    Replace /* = "REPLACE" */
}

external interface Path {
    var pathname: Pathname
    var search: Search
    var hash: Hash
}

external interface PathPartial {
    var pathname: Pathname?
        get() = definedExternally
        set(value) = definedExternally
    var search: Search?
        get() = definedExternally
        set(value) = definedExternally
    var hash: Hash?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Location : Path {
    var state: Any
    var key: Key
}

external interface LocationPartial : PathPartial {
    var state: Any?
        get() = definedExternally
        set(value) = definedExternally
    var key: Key?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Update {
    var action: Action
    var location: Location
}

external interface Transition : Update {
    fun retry()
}

external interface History {
    var action: Action
    var location: Location
    fun createHref(to: String): String
    fun createHref(to: PathPartial): String
    fun push(to: String, state: Any = definedExternally)
    fun push(to: String)
    fun push(to: PathPartial, state: Any = definedExternally)
    fun push(to: PathPartial)
    fun replace(to: String, state: Any = definedExternally)
    fun replace(to: String)
    fun replace(to: PathPartial, state: Any = definedExternally)
    fun replace(to: PathPartial)
    fun go(delta: Number)
    fun back()
    fun forward()
    fun listen(handler: (update: Update) -> Unit): () -> Unit
    fun block(blocker: (transition: Transition) -> Unit): () -> Unit
}

external interface BrowserHistory : History
external interface HashHistory : History
external interface MemoryHistory : History

external interface BrowserHistoryOptions {
    var window: org.w3c.dom.Window?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HashHistoryOptions {
    var window: org.w3c.dom.Window?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MemoryHistoryOptions {
    var initialEntries: Array<dynamic /* String | LocationPartial */>?
        get() = definedExternally
        set(value) = definedExternally
    var initialIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createBrowserHistory(options: BrowserHistoryOptions = definedExternally): BrowserHistory
external fun createHashHistory(options: HashHistoryOptions = definedExternally): HashHistory
external fun createMemoryHistory(options: MemoryHistoryOptions = definedExternally): MemoryHistory
external fun createPath(__0: PathPartial): String
external fun parsePath(path: String): PathPartial