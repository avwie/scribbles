package nl.avwie.vdom

sealed interface PatchOp
data class Add(val node: Node): PatchOp
object Remove : PatchOp
data class Patches(val attrOps: List<AttrPatch>, val childOps: List<PatchOp>): PatchOp
object Next : PatchOp

sealed interface AttrPatch
data class RemoveAttr(val key: String): AttrPatch
data class SetAttr(val key: String, val value: String): AttrPatch
