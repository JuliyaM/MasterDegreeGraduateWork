package main.java.processors.solvers

import koma.extensions.get
import main.java.AnalyzedProject
import main.java.DeltaResult
import main.java.extentions.average
import main.java.extentions.averageResult
import koma.extensions.map
import koma.extensions.mapIndexed
import koma.matrix.Matrix
import processors.solvers.AvgExperimentSolver
import kotlin.math.absoluteValue

data class WithEndProcessWeightSolveResult(
    val processResultWeights: List<Double>,
    val dropEndMatrix: Matrix<Double>,
    val dropEndMatrixPow2: Matrix<Double>,
    val multiKolmogorovResult: List<Pair<Matrix<Double>, List<Double>>>,
    val endProjectExpCount: Int,
    val endProjectResultList: List<EndProjectExperimentSolver.ExperimentResult>,
    val endProjectResultListAverage: EndProjectExperimentSolver.ExperimentResult
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
    private val endProjectExperimentSolver: EndProjectExperimentSolver
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

    fun solveKomogorovWithEndState(
        projectMatrix: Matrix<Double>,
        project: AnalyzedProject
    ): KolmogorovResultWeightsResponse {
        //Получаем начальный и конечный индекс состояния
        val startProcessIndex = project.startProcessIndex
        val endProjectIndex = project.endProjectIndex ?: error("Project without end")

        //Составляем список номеров столбцов и строк без конечного состояния
        val idxs = (0 until projectMatrix.numRows()).filter { it != endProjectIndex }.toIntArray()

        //Удаляем конечное состояние, а веса переносим в диагональые элементы
        val dropEndMatrix =
            projectMatrix.mapIndexed { row, col, ele ->
                if (row == col) ele + projectMatrix[row, endProjectIndex] else ele
            }.selectRows(*idxs).selectCols(*idxs)

        //Расчитываем матрицу  2го шага
        val dropEndMatrixpow2 = dropEndMatrix.pow(2)
        val multiKolmogorovResult = (0 until dropEndMatrix.numRows()).map {
            //Проводим модификацию начальной матрицы для более корректного расчета средних
            //нормированных весов процессов
            //Берем матрицу 2го шага и заменяем в ней i строку на i строку из начальной матрицы
            val Pi = dropEndMatrixpow2.copy().apply { setRow(it, dropEndMatrix.getRow(it)) }
            //Расчитываем решение дифференциальной системы уравнений Колмогорова
            val resultI = differentialKolmogorovSolver.solve(Pi).toList()
            Pi to resultI
        }

        //Составляем ответ из полученных данных
        return KolmogorovResultWeightsResponse(
            //Матрица без конечного состояния
            dropEndMatrix = dropEndMatrix,
            //Матрица без конечного состояния второго шага
            dropEndMatrixPow2 = dropEndMatrixpow2,
            //Результат расчета уравнений Колмогорова для любых нач. сост
            multiKolmogorovResult = multiKolmogorovResult,
            //Результат для нач. сост проекта
            kolmogorovResultWeights = multiKolmogorovResult[startProcessIndex].second
        )
    }

    data class KolmogorovResultWeightsResponse(
        val dropEndMatrix: Matrix<Double>,
        val dropEndMatrixPow2: Matrix<Double>,
        val multiKolmogorovResult: List<Pair<Matrix<Double>, List<Double>>>,
        val kolmogorovResultWeights: List<Double>
    )

    fun getProjectWithEndProcessWeights(
        projectMatrix: Matrix<Double>,
        project: AnalyzedProject
    ): WithEndProcessWeightSolveResult {
        val resultWeightsResponse = solveKomogorovWithEndState(projectMatrix, project)

        val endProjectIndex = project.endProjectIndex ?: error("Project without end")

        val endProjectExpCount = 1000
        val endProjectResultList =
            endProjectExperimentSolver.solve(projectMatrix, endProjectExpCount, endProjectIndex)

        val endProjectResultListAverage = EndProjectExperimentSolver.ExperimentResult(
            startIndex = -1,
            averageResult = endProjectResultList.map { it.averageResult }.averageResult(),
            averageDayCount = endProjectResultList.average { it.averageDayCount }
        )

        val multiKolmogorovResult = resultWeightsResponse.multiKolmogorovResult

        return WithEndProcessWeightSolveResult(
            endProjectExpCount = endProjectExpCount,
            endProjectResultList = endProjectResultList,
            endProjectResultListAverage = endProjectResultListAverage,
            processResultWeights = resultWeightsResponse.kolmogorovResultWeights,
            dropEndMatrix = resultWeightsResponse.dropEndMatrix,
            dropEndMatrixPow2 = resultWeightsResponse.dropEndMatrixPow2,
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


