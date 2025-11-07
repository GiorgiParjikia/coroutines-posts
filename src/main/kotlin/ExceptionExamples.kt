package coroutines.homework10

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    println("=== Exception handling examples ===")

    exceptionQuestion1()
    exceptionQuestion2()
    exceptionQuestion3()
    exceptionQuestion4()
    exceptionQuestion5()
    exceptionQuestion6()
    exceptionQuestion7()
}

/*
Вопрос №1
*/
fun exceptionQuestion1() {
    println("\nQuestion 1:")
    with(CoroutineScope(EmptyCoroutineContext)) {
        try {
            launch {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
    println("catch не сработал, т.к. исключение бросается внутри launch — асинхронно, а не в том же потоке.")
}

/*
Вопрос №2
*/
fun exceptionQuestion2() {
    println("\nQuestion 2:")
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            coroutineScope {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
    println("catch сработал — coroutineScope передаёт исключение наружу в тот же контекст.")
}

/*
Вопрос №3
*/
fun exceptionQuestion3() {
    println("\nQuestion 3:")
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            supervisorScope {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
    println("catch сработал — supervisorScope ведёт себя как coroutineScope, если ошибка внутри самого блока.")
}

/*
Вопрос №4
*/
fun exceptionQuestion4() {
    println("\nQuestion 4:")
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            coroutineScope {
                launch {
                    delay(500)
                    throw Exception("something bad happened") // <--
                }
                launch {
                    throw Exception("something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Thread.sleep(1000)
    println("catch сработал — coroutineScope отменяет все дочерние при исключении одной, остальные println не выполняются.")
}

/*
Вопрос №5
*/
fun exceptionQuestion5() {
    println("\nQuestion 5:")
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            supervisorScope {
                launch {
                    delay(500)
                    println("Первая корутина завершилась с ошибкой") // лог
                    throw Exception("something bad happened (1)") // <--
                }
                launch {
                    println("Вторая корутина работает независимо") // лог
                    throw Exception("something bad happened (2)")
                }
            }
        } catch (e: Exception) {
            println("catch сработал в родителе supervisorScope") // лог
            e.printStackTrace()
        }
    }
    Thread.sleep(1000)
    println("supervisorScope не отменяет соседние корутины, но исключения в каждой из них выводятся отдельно (два stack trace).")
}

/*
Вопрос №6
*/
fun exceptionQuestion6() {
    println("\nQuestion 6:")
    CoroutineScope(EmptyCoroutineContext).launch {
        CoroutineScope(EmptyCoroutineContext).launch {
            launch {
                delay(1000)
                println("ok") // <--
            }
            launch {
                delay(500)
                println("ok")
            }
            throw Exception("something bad happened")
        }
    }
    Thread.sleep(1000)
    println("строка не вывелась — т.к. внутренний Scope без supervision, ошибка отменяет все дочерние корутины.")
}

/*
Вопрос №7
*/
fun exceptionQuestion7() {
    println("\nQuestion 7:")
    CoroutineScope(EmptyCoroutineContext).launch {
        val scope = CoroutineScope(SupervisorJob())

        scope.launch {
            delay(1000)
            println("ok") // теперь выведется
        }

        scope.launch {
            delay(500)
            println("ok")
        }

        throw Exception("something bad happened")
    }

    Thread.sleep(1500)
    println("в консоли выводятся оба ok, исключение выше не мешает их выполнению.")
}