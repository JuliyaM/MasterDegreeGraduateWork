package processors

import koma.extensions.mapIndexed
import koma.matrix.Matrix


class ProjectLifecycleBuilder {
    fun build(start: Matrix<Double>, labors: List<Int>, endRowIndex: Int?): Matrix<Double> {
        return start.mapIndexed { row: Int, col: Int, pi: Double ->
            val mi = labors.getOrNull(row) ?: 1
            when {
                row == endRowIndex && row == col -> 1.0
                row == endRowIndex -> 0.0
                row != col -> pi / mi
                else -> 1 - 1.0 / mi
            }
        }
    }
}