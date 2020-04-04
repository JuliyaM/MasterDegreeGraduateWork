package processors.solvers

import extentions.diagonalZeros
import extentions.onesRow
import extentions.zerosWithOneIn
import koma.extensions.mapIndexed
import koma.matrix.Matrix

class DifferentialKolmogorovSolver {
    fun solve(P: Matrix<Double>): Matrix<Double> {
        val P1 = P.diagonalZeros()
        val rowSum = P1.mapRowsToList {
            it.elementSum() * -1
        }

        val P2 = P1.mapIndexed { row, col, ele ->
            if (row == col) rowSum[row]
            else ele
        }.transpose()

        val P3 = P2.copy().apply { onesRow(P2.numRows() - 1) }

        val result = P3.solve(zerosWithOneIn(P3.numRows(), P2.numRows() - 1))
        return result
    }
}