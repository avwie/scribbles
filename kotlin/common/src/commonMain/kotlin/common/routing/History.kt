package common.routing

import androidx.compose.runtime.State
import common.UUID
import common.uuid

interface History {
    val activeLocation: State<Location>

    fun push(location: Location)
    fun forward()
    fun back()

    fun peekForward(): Location?
    fun peekBack(): Location?
}

data class Location(
    val pathName: String,
    val query: String? = null,
    val hash: String? = null,
    val id: UUID = uuid()
) {
    fun toURL(): String {
        val builder = StringBuilder()
        builder.append(pathName)

        if (query != null) builder.append("?$query")
        if (hash != null) builder.append("#$hash")
        return builder.toString()
    }

    companion object {
        fun parse(str: String): Location {
            val pathNameBuilder = StringBuilder()
            val queryBuilder = StringBuilder()
            val hashBuilder = StringBuilder()

            val parsedControlChars = mutableSetOf<Char>()
            var currentBuilder = pathNameBuilder

            str.forEach { c ->
                when {
                    (c == '?' || c == '#') && parsedControlChars.contains(c) -> throw IllegalArgumentException("Malformed Location input: $str")
                    c == '?' && parsedControlChars.contains('#') -> throw IllegalArgumentException("Malformed Location input: $str")

                    c == '?' -> {
                        parsedControlChars.add(c)
                        currentBuilder = queryBuilder
                    }
                    c == '#' -> {
                        parsedControlChars.add(c)
                        currentBuilder = hashBuilder
                    }
                    else -> currentBuilder.append(c)
                }
            }
            return Location(
                pathNameBuilder.toString(),
                queryBuilder.toString().let { it.ifBlank { null } },
                hashBuilder.toString().let { it.ifBlank { null } },
            )
        }
    }
}