package nl.avwie.common.routing

interface Routing<R> {
    fun getRoute(location: Location): R
}

fun <R> Routing<R>.getRoute(pathName: String, query: String? = null, hash: String? = null) = getRoute(Location(pathName, query, hash))

class RoutingBuilder<R>(
    private val errorRoute: R
) : Routing<R> {

    fun interface Rule<R> {
        fun match(location: Location): R?
    }

    private val rules = mutableListOf<Rule<R>>()

    fun matchPathName(pathname: String, builder: (location: Location) -> R) {
        val rule = Rule { location ->
            when (location.pathName) {
                pathname -> builder(location)
                else -> null
            }
        }
        rules.add(rule)
    }

    fun matchRegex(pattern: String, builder: (groups: List<String>) -> R) {
        val regex = Regex(pattern)
        val rule = Rule { location ->
            val result = regex.matchEntire(location.pathName) ?: return@Rule null
            builder(result.groupValues.drop(1))
        }
        rules.add(rule)
    }

    fun <T1> matchFragments(f1: Fragment<T1>,
                            builder: (T1) -> R
    ) = innerMatchFragments(f1) { (s1) ->
        builder(f1.parse(s1))
    }

    fun <T1, T2> matchFragments(f1: Fragment<T1>,
                                f2: Fragment<T2>,
                                builder: (T1, T2) -> R
    ) = innerMatchFragments(f1, f2) { (s1, s2) ->
        builder(f1.parse(s1), f2.parse(s2))
    }

    fun <T1, T2, T3> matchFragments(f1: Fragment<T1>,
                                    f2: Fragment<T2>,
                                    f3: Fragment<T3>,
                                    builder: (T1, T2, T3) -> R
    ) = innerMatchFragments(f1, f2, f3) { (s1, s2, s3) ->
        builder(f1.parse(s1), f2.parse(s2), f3.parse(s3))
    }

    fun <T1, T2, T3, T4> matchFragments(f1: Fragment<T1>,
                                        f2: Fragment<T2>,
                                        f3: Fragment<T3>,
                                        f4: Fragment<T4>,
                                        builder: (T1, T2, T3, T4) -> R
    ) = innerMatchFragments(f1, f2, f3, f4) { (s1, s2, s3, s4) ->
        builder(f1.parse(s1), f2.parse(s2), f3.parse(s3), f4.parse(s4))
    }

    fun <T1, T2, T3, T4, T5> matchFragments(f1: Fragment<T1>,
                                            f2: Fragment<T2>,
                                            f3: Fragment<T3>,
                                            f4: Fragment<T4>,
                                            f5: Fragment<T5>,
                                            builder: (T1, T2, T3, T4, T5) -> R
    ) = innerMatchFragments(f1, f2, f3, f4, f5) { (s1, s2, s3, s4, s5) ->
        builder(f1.parse(s1), f2.parse(s2), f3.parse(s3), f4.parse(s4), f5.parse(s5))
    }

    private fun innerMatchFragments(vararg fragments: Fragment<*>, builder: (List<String>) -> R) {
        val regex = Regex("/" + fragments.joinToString("/") { it.regexPattern })
        val rule = Rule { location ->
            val result = regex.matchEntire(location.pathName) ?: return@Rule null
            val parts = result.groupValues.drop(1)
            builder(parts)
        }
        rules.add(rule)
    }

    override fun getRoute(location: Location): R = rules
        .firstNotNullOfOrNull { rule -> rule.match(location) } ?: errorRoute
}

fun <R> createRouting(errorRoute: R, routingBuilderScope: RoutingBuilder<R>.() -> Unit): Routing<R> {
    val scope = RoutingBuilder(errorRoute)
    routingBuilderScope(scope)
    return scope
}