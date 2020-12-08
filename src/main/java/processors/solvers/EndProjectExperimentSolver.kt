package main.java.processors.solvers

import MatrixWalker
import main.java.extentions.averageResult
import koma.matrix.Matrix

class EndProjectExperimentSolver {

    data class ExperimentResult(
        val startIndex: Int,
        val averageResult: List<Double>,
        val averageDayCount: Double
    )

    fun solve(P: Matrix<Double>, experimentsCount: Int, endRowIndex: Int): List<ExperimentResult> {
        return (0 until P.numRows())
            .filter { it != endRowIndex }
            .map { rowStartIndex ->
                getOneRowResult(P, rowStartIndex, experimentsCount, endRowIndex)
            }
    }

    fun getOneRowResult(
        P: Matrix<Double>,
        rowStartIndex: Int,
        experimentsCount: Int,
        endRowIndex: Int
    ): ExperimentResult {
        val matrixWalker = MatrixWalker(P, rowStartIndex)

        val walkResults = mutableListOf<List<Int>>()
        for (i in 0 until experimentsCount) {
            matrixWalker.clear()
            while (matrixWalker.curRow != endRowIndex) {
                matrixWalker.walkNext()
            }
            val notEndSteps = matrixWalker.statesWalkCount.dropLast(1)
            walkResults.add(notEndSteps)
        }
        val avgDayResult = walkResults.sumBy { it.sum() }.toDouble() / walkResults.count()

        val normalized = walkResults.map { walkResult ->
            walkResult.map { it.toDouble() / walkResult.sum() }
        }
        val averageResult = normalized.averageResult()

        return ExperimentResult(
            rowStartIndex,
            averageResult,
            avgDayResult
        )
    }
}