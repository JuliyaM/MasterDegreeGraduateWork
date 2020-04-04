package processors.solvers

import DeltaResult
import extentions.average
import extentions.averageResult
import extentions.getBinaryDistribution
import koma.extensions.map
import koma.matrix.Matrix
import koma.pow
import processors.ProjectLifecycleBuilder
import processors.crono.ExperimentChronicler
import kotlin.math.absoluteValue

class ProcessWeightSolver(
    private val differentialKolmogorovSolver: DifferentialKolmogorovSolver,
    private val avgExperimentSolver: AvgExperimentSolver,
    private val experimentChronicler: ExperimentChronicler,
    private val endProjectExperimentSolver: EndProjectExperimentSolver
) {
    fun printProcessWeightsAlgorithm(
        start: Matrix<Double>,
        labors: List<Int>,
        projectMatrix: Matrix<Double>,
        startProcessIndex: Int,
        endRowIndex: Int?
    ): List<Double> {

        val avgExpSteps = labors.sum() * 1000
        val avgExperimentsCount = 100

        val projectDiffKolmogorovResult = differentialKolmogorovSolver.solve(projectMatrix).toList()
        val avgResult = avgExperimentSolver.solve(projectMatrix, avgExpSteps, avgExperimentsCount, 0)

        experimentChronicler.defaultAlgorithm(
            start = start,
            labors = labors,
            endRowIndex = endRowIndex,
            projectMatrix = projectMatrix,
            projectDiffKolmogorovResult = projectDiffKolmogorovResult,
            avgExpSteps = avgExpSteps,
            avgExperimentsCount = avgExperimentsCount,
            avgResult = avgResult
        )

        val processResultWeights = endRowIndex?.let {
            val endProjectExpCount = 100000
            val endProjectResultList = endProjectExperimentSolver.solve(projectMatrix, endProjectExpCount, endRowIndex)

            val endProjectResultListAverage = EndProjectExperimentSolver.ExperimentResult(
                startState = -1,
                averageResult = endProjectResultList.map { it.averageResult }.averageResult(),
                averageDayCount = endProjectResultList.average { it.averageDayCount }
            )

            val idxs = (0 until projectMatrix.numRows()).filter { it != endRowIndex }.toIntArray()
            val P1 = projectMatrix.selectRows(*idxs).selectCols(*idxs)

            val normalizedP1 = P1.mapRows { row -> row.map { it / row.elementSum() } }

            val resultKolmogorovNormilizedP1 = differentialKolmogorovSolver.solve(normalizedP1).toList()

            val normilizedP1pow2 = normalizedP1.pow(2)
            val multiKolmogorovResult = (0 until normalizedP1.numRows()).map {
                val Pi = normilizedP1pow2.copy().apply { setRow(it, normalizedP1.getRow(it)) }
                val resultI = differentialKolmogorovSolver.solve(Pi).toList()

                Pi to resultI
            }

            val endProjectToKolmogorovDelta = endProjectResultList.mapIndexed { i, endProjectI ->
                val kolmogorovI = multiKolmogorovResult[i].second

                endProjectI.averageResult.mapIndexed { j, averageValue ->
                    val absolute = (averageValue - kolmogorovI[j]).absoluteValue
                    DeltaResult(absolute = absolute, relative = absolute / averageValue)
                }
            }


            val averageResultDelta = endProjectResultListAverage.averageResult.mapIndexed { j, averageValue ->
                val absolute = (averageValue - resultKolmogorovNormilizedP1[j]).absoluteValue
                DeltaResult(absolute = absolute, relative = absolute / averageValue)
            }


            val kolmogorovResultWeights = multiKolmogorovResult[startProcessIndex].second

            experimentChronicler.specialAlgorithm(
                endProjectExpCount = endProjectExpCount,
                endProjectResultList = endProjectResultList,
                P1 = P1,
                normalizedP1 = normalizedP1,
                resultNormilizedP1 = resultKolmogorovNormilizedP1,
                normilizedP1pow2 = normilizedP1pow2,
                multiKolmogorovResult = multiKolmogorovResult,
                endProjectToKolmogorovDelta = endProjectToKolmogorovDelta,
                endProjectResultListAverage = endProjectResultListAverage,
                averageResultDelta = averageResultDelta
            )

            kolmogorovResultWeights
        } ?: projectDiffKolmogorovResult
        return processResultWeights
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