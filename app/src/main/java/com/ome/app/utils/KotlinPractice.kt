package com.ome.app.utils

fun <T> doSomething(t: T): T {
    return t
}

/*fun <T> calculateLength(items: List<T>): Int {
    return items.size
}*/



fun <T> List<T>.calculateList(): Int {
    return this.size
}

fun <N1, N2> multiplySomething(multiplier: N1, multiply: N2): Int {
    return multiplier as Int * multiply as Int
}


fun main() {
    listOf("foo", "bar").calculateList()
    println(
        multiplySomething(5, 10)
    )
}