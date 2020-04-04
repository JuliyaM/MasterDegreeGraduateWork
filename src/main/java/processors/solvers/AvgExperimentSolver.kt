package processors.solvers

import MatrixWalker
import koma.matrix.Matrix

class AvgExperimentSolver {
    fun solve(
        P: Matrix<Double>,
        steps: Int,
        experimentsCount: Int,
        startRow: Int
    ): List<Double> {
        val matrixWalker = MatrixWalker(P, startRow)
        val rows = P.numRows()
        var sumStatesWalkCount: List<Int> = IntArray(rows).toList()

        val experimentalRange = 0 until experimentsCount
        val stepRange = 0 until steps

        for (i in experimentalRange) {
            matrixWalker.clear()
            for (j in stepRange) {
                matrixWalker.walkNext()
            }
            sumStatesWalkCount = sumStatesWalkCount
                .mapIndexed { index, value -> value + matrixWalker.statesWalkCount[index] }
        }

        val avg = sumStatesWalkCount.map { it.toDouble() / experimentsCount }
        return avg
    }

}