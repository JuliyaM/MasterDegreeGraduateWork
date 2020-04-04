package ktorModuleLibrary.librariesExtentions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.math.max
import kotlin.system.measureTimeMillis


fun CoroutineScope.timer(interval: Long, fixedRate: Boolean = true, action: suspend TimerScope.() -> Unit): TimerScope {
    val scope = TimerScope()

    launch {
        while (!scope.isCanceled) {
            val time = measureTimeMillis {
                try {
                    action(scope)
                } catch (ex: Exception) {

                }
            }

            if (fixedRate) {
                delay(max(0, interval - time))
            } else {
                delay(interval)
            }

            yield()
        }
    }

    return scope
}

class TimerScope {
    var isCanceled: Boolean = false
        private set

    fun cancel() {
        isCanceled = true
    }
}