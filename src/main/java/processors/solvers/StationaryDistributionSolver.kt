package processors.solvers

import main.java.extentions.onesRow
import main.java.extentions.zerosWithOneIn
import koma.eye
import koma.matrix.Matrix

class StationaryDistributionSolver {
    fun process(P: Matrix<Double>): Matrix<Double> {
        val newP = P - eye(P.numRows())
        newP.onesRow(newP.numRows() - 1)
        val rows = newP.numRows()
        val result = zerosWithOneIn(rows, rows - 1)
        return newP.solve(result)
    }
}
