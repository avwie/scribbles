package router.fragment

import common.UUID
import common.uuid

interface Fragment<T> {
    val regexPattern: String
    fun parse(str: String): T
}

inline fun <T> fragment(pattern: String, crossinline parse: (String) -> T) = object : Fragment<T> {
    override val regexPattern: String = pattern
    override fun parse(str: String): T = parse(str)
}

object UUID : Fragment<common.UUID> {
    override val regexPattern: String = "([0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12})"
    override fun parse(str: String): UUID = uuid(str)
}

object Number : Fragment<Int> {
    override val regexPattern: String = "(\\d*)"
    override fun parse(str: String): Int = str.toInt()
}

object Text : Fragment<String> {
    override val regexPattern: String = "([\\w\\d\\-]*)"
    override fun parse(str: String): String = str
}


class TextMatch(val match: String) : Fragment<String> {
    override val regexPattern: String = "($match)"
    override fun parse(str: String): String = str
}