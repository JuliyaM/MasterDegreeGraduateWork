package extentions

import koma.extensions.forEachIndexed
import koma.extensions.mapIndexed
import koma.matrix.Matrix
import koma.ones
import koma.zeros

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return koma.round(this * multiplier) / multiplier
}


fun Iterable<Double>.showPercents(decimals: Int) =
    joinToString(", ") { it.percent(decimals) }

fun Double.percent(decimals: Int) =
    round(decimals).toString() + "%"


fun Matrix<Double>.onesRow(index: Int) =
    setRow(index, ones(1, this.numCols()))


fun zerosWithOneIn(rows: Int, i: Int) =
    zeros(rows, 1).apply { setInt(i, 0, 1) }

fun Matrix<Double>.diagonalZeros() = mapIndexed { row, col, ele ->
    if (row == col) 0.0
    else ele
}

inline fun <T> Matrix<T>.forEachRowO(f: (rowIndex: Int, row: Matrix<T>) -> Unit) {
    for (row in 0 until this.numRows())
        f(row, this.getRow(row))
}

inline fun <T> Matrix<T>.anyIndexed(crossinline predicate: (rowIndex: Int, colIndex: Int, ele: T) -> Boolean): Boolean {
    var result = false
    this.forEachIndexed { row: Int, col: Int, ele: T ->
        if (predicate(row, col, ele)) result = true
    }
    return result
}


inline fun <T> Matrix<T>.firstOrNullRow(predicate: (rowIndex: Int, row: Matrix<T>) -> Boolean): IndexedValue<Matrix<T>>? {
    this.forEachRowO { rowIndex, row ->
        if (predicate(rowIndex, row)) return IndexedValue(rowIndex, row)
    }
    return null
}

fun <E : Number> Iterable<Iterable<E>>.averageResult(): List<Double> {
    val integrator = DoubleArray(this.first().count())
    this.forEach {
        it.forEachIndexed { index, d -> integrator[index] += d.toDouble() / this.count() }
    }
    return integrator.toList()
}

fun <E : Number> Iterable<E>.normalized(): List<Double> {
    val sum = this.sumByDouble { it.toDouble() }
    return map {
        it.toDouble() / sum
    }
}

fun Iterable<Double>.round(decimals: Int): List<Double> {
    return this.map {
        it.round(decimals)
    }
}

inline fun <E> Iterable<E>.average(selector: (E) -> Double): Double = this.sumByDouble(selector) / this.count()


fun List<List<Double>>.joinToMatrix(): Matrix<Double> {
    return Matrix(rows = this.size, cols = this.first().size) { rowIndex, colIndex ->
        this[rowIndex][colIndex]
    }
}


fun <T> List<List<T>>.transpose(): List<List<T>> {
    val transposedList = mutableListOf<MutableList<T>>()

    this.forEachIndexed { i, list ->
        list.forEachIndexed { j, value ->
            val containList = transposedList.getOrNull(j) ?: run {
                val newlist = mutableListOf<T>()
                transposedList.add(j, newlist)
                newlist
            }
            containList.add(i, value)
        }
    }

    return transposedList
}

const val LS = "\\!"
fun frac(numerator: Any, denominator: Any) = "\\frac{$numerator}{$denominator}"
fun String.withIndexLatex(vararg any: Any) = this + "_{${any.joinToString("")}}"
fun String.powLatex(vararg any: Any) = this + "^{${any.joinToString("")}}"

fun getBinaryDistribution(size: Int, representation: Int): List<Int> {
    return IntArray(size)
        .let { array ->
            val mutableBits = array.toMutableList()

            Integer
                .toBinaryString(representation)
                .toList()
                .map { it.toString().toInt() }
                .reversed()
                .forEachIndexed { index, value ->
                    mutableBits[index] = value
                }

            mutableBits
        }
}

public inline fun <T> Iterable<T>.sumByListDouble(selector: (T) -> List<Double>): List<Double> {
    val sum = mutableListOf<Double>()
    for (element in this) {
        val list = selector(element)
        if(sum.isEmpty()) sum.addAll(list)
        else sum.mutableSumWith(list)
    }
    return sum
}

public inline fun <T> Iterable<T>.minByListDouble(selector: (T) -> List<Double>): List<Double> {
    return this
        .map(selector)
        .transpose()
        .map {
            it.min() ?: 0.0
        }
}

public inline fun <T> Iterable<T>.maxByListDouble(selector: (T) -> List<Double>): List<Double> {
    return this
        .map(selector)
        .transpose()
        .map {
            it.max() ?: 0.0
        }
}

fun List<Double>.sumWith(element: List<Double>): List<Double> {
    return this.mapIndexed { index, d ->
        d + element[index]
    }
}

fun MutableList<Double>.mutableSumWith(element: List<Double>) {
    element.forEachIndexed { index, d ->
        this[index] += d
    }
}
