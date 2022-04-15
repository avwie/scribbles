package nl.avwie.vdom

sealed interface Patch
data class Add(val node: Node): Patch
object Remove : Patch
data class Patches(val attrOps: List<AttrPatch>, val childOps: List<Patch>): Patch
object Skip : Patch

sealed interface AttrPatch
data class RemoveAttr(val key: String): AttrPatch
data class SetAttr(val key: String, val value: String): AttrPatch
