package ktorModuleLibrary.librariesExtentions

import kotlin.random.Random


fun Double.less(number: Int): Boolean {
    return this < number
}

fun Double.less(number: Double): Boolean {
    return this < number
}

fun Double.inRange(intRange: IntRange): Boolean {
    return this >= intRange.first && this <= intRange.last
}
fun List<Double>.median(): Double? {
    val centralElement = this.size / 2
    return when {
        this.isEmpty() -> null
        this.size > centralElement + 1 && this.size % 2 == 0 -> (this[centralElement] + this[centralElement + 1]) / 2
        else -> this[centralElement]
    }
}


fun <T> List<Pair<T, Double>>.randomByWeightPairs(): T? {
    var accumSum = 0.0
    val weightSum = this.sumByDouble {
        it.second
    }

    val randomedValue = Random.nextDouble()
    val accumulatedByWeightElemToWeightPairs = this
        .map {
            it.first to if (weightSum > 0) it.second / weightSum else 1.0
        }
        .sortedBy {
            it.second
        }
        .map {
            val newValue = it.second + accumSum
            accumSum += it.second
            it.first to newValue
        }
    return accumulatedByWeightElemToWeightPairs.firstOrNull() {
        it.second >= randomedValue
    }?.first
}