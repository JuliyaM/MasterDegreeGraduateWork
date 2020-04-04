package main.java.processors.solvers

import AnalyzedProject
import DeltaResult
import extentions.average
import extentions.averageResult
import koma.extensions.map
import koma.matrix.Matrix
import processors.solvers.AvgExperimentSolver
import processors.solvers.DifferentialKolmogorovSolver
import processors.solvers.EndProjectExperimentSolver
import kotlin.math.absoluteValue

data class WithEndProcessWeightSolveResult(
    val processResultWeights: List<Double>,
    val dropEndMatrix: Matrix<Double>,
    val normalizedDropEndMatrix: Matrix<Double>,
    val resultKolmogorovDropEnd: List<Double>,
    val dropEndMatrixPow2: Matrix<Double>,
    val multiKolmogorovResult: List<Pair<Matrix<Double>, List<Double>>>,
    val endProjectExpCount: Int,
    val endProjectResultList: List<EndProjectExperimentSolver.ExperimentResult>,
    val endProjectResultListAverage: EndProjectExperimentSolver.ExperimentResult,
    val endProjectToKolmogorovDelta: List<List<DeltaResult>>,
    val averageResultDelta: List<DeltaResult>
)

data class WithoutEndProcessWeightSolveResult(
    val projectDiffKolmogorovResult: List<Double>,
    val avgExpSteps: Int,
    val avgExperimentsCount: Int,
    val avgResult: List<Double>
)
class ProcessWeightSolver(
    private val differentialKolmogorovSolver: DifferentialKolmogorovSolver,
    private val avgExperimentSolver: AvgExperimentSolver,
    private val endProjectExperimentSolver : EndProjectExperimentSolver
) {

    fun getWithoutEndProjectProcessesWeights(
        labors: List<Int>,
        projectMatrix: Matrix<Double>
    ): WithoutEndProcessWeightSolveResult {
        val avgExpSteps = labors.sum() * 50
        val avgExperimentsCount = 100
        val avgResult = avgExperimentSolver.solve(projectMatrix, avgExpSteps, avgExperimentsCount, 0)

        val projectDiffKolmogorovResult = differentialKolmogorovSolver.solve(projectMatrix).toList()

        val sum = avgResult.sum()
        return WithoutEndProcessWeightSolveResult(
            projectDiffKolmogorovResult = projectDiffKolmogorovResult,
            avgExpSteps = avgExpSteps,
            avgExperimentsCount = avgExperimentsCount,
            avgResult = avgResult.map { it / sum }
        )
    }

    fun getProjectWithEndProcessWeights(
        projectMatrix: Matrix<Double>,
        project: AnalyzedProject
    ): WithEndProcessWeightSolveResult {
        val startProcessIndex = project.startProcessIndex
        val endProjectIndex = project.endProjectIndex ?: error("Project without end")

        val idxs = (0 until projectMatrix.numRows()).filter { it != endProjectIndex }.toIntArray()
        val dropEndMatrix = projectMatrix.selectRows(*idxs).selectCols(*idxs)

        val normilizedDropEndMatrix = dropEndMatrix.mapRows { row -> row.map { it / row.elementSum() } }

        val resultKolmogorovDropEnd = differentialKolmogorovSolver.solve(normilizedDropEndMatrix).toList()

        val dropEndMatrixpow2 = normilizedDropEndMatrix.pow(2)
        val multiKolmogorovResult = (0 until normilizedDropEndMatrix.numRows()).map {
            val Pi = dropEndMatrixpow2.copy().apply { setRow(it, normilizedDropEndMatrix.getRow(it)) }
            val resultI = differentialKolmogorovSolver.solve(Pi).toList()

            Pi to resultI
        }

        val kolmogorovResultWeights = multiKolmogorovResult[startProcessIndex].second

        val endProjectExpCount = 1000
        val endProjectResultList =
            endProjectExperimentSolver.solve(projectMatrix, endProjectExpCount, endProjectIndex)

        val endProjectResultListAverage = EndProjectExperimentSolver.ExperimentResult(
            startState = -1,
            averageResult = endProjectResultList.map { it.averageResult }.averageResult(),
            averageDayCount = endProjectResultList.average { it.averageDayCount }
        )

        val endProjectToKolmogorovDelta = endProjectResultList.mapIndexed { i, endProjectI ->
            val kolmogorovI = multiKolmogorovResult[i].second

            endProjectI.averageResult.mapIndexed { j, averageValue ->
                val absolute = (averageValue - kolmogorovI[j]).absoluteValue
                DeltaResult(absolute = absolute, relative = absolute / averageValue)
            }
        }

        val averageResultDelta = endProjectResultListAverage.averageResult.mapIndexed { j, averageValue ->
            val absolute = (averageValue - resultKolmogorovDropEnd[j]).absoluteValue
            DeltaResult(absolute = absolute, relative = absolute / averageValue)
        }

        return WithEndProcessWeightSolveResult(
            endProjectExpCount = endProjectExpCount,
            endProjectResultList = endProjectResultList,
            endProjectResultListAverage = endProjectResultListAverage,
            endProjectToKolmogorovDelta = endProjectToKolmogorovDelta,
            averageResultDelta = averageResultDelta,
            processResultWeights = kolmogorovResultWeights,
            dropEndMatrix = dropEndMatrix,
            normalizedDropEndMatrix = normilizedDropEndMatrix,
            resultKolmogorovDropEnd = resultKolmogorovDropEnd,
            dropEndMatrixPow2 = dropEndMatrixpow2,
            multiKolmogorovResult = multiKolmogorovResult
        )
    }

    fun getProcessWeightsShort(
        projectMatrix: Matrix<Double>,
        startProcessIndex: Int,
        endRowIndex: Int?
    ): List<Double> {
        return endRowIndex?.let {
            val idxs = (0 until projectMatrix.numRows()).filter { it != endRowIndex }.toIntArray()
            val P1 = projectMatrix.selectRows(*idxs).selectCols(*idxs)
            val normalizedP1 = P1.mapRows { row -> row.map { it / row.elementSum() } }
            val Pi = normalizedP1.pow(2).apply { setRow(startProcessIndex, normalizedP1.getRow(startProcessIndex)) }
            differentialKolmogorovSolver.solve(Pi).toList()
        } ?: differentialKolmogorovSolver.solve(projectMatrix).toList()
    }
}


