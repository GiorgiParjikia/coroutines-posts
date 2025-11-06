package coroutines.homework10

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking {
    println("=== Cancellation examples ===")
    cancellationQuestion1()
    cancellationQuestion2()
}

/*
Вопрос №1
*/
suspend fun cancellationQuestion1() {
    println("\nQuestion 1:")
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        launch {
            delay(500)
            println("ok") // <--
        }
        launch {
            delay(500)
            println("ok")
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Результат: ничего не вывелось, потому что job.cancelAndJoin() отменил обе корутины раньше, чем они дошли до println().")
}

/*
Вопрос №2
*/
suspend fun cancellationQuestion2() {
    println("\nQuestion 2:")
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        val child = launch {
            delay(500)
            println("ok") // <--
        }
        launch {
            delay(500)
            println("ok")
        }
        delay(100)
        child.cancel()
    }
    delay(100)
    job.join()
    println("Результат: строка не вывелась, потому что конкретный child был отменён раньше delay(500).")
}