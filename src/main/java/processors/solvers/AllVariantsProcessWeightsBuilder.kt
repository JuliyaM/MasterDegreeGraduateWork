package main.java.processors.solvers

import main.java.extentions.getBinaryDistribution
import koma.matrix.Matrix
import koma.pow
import main.java.processors.ProjectLifecycleBuilder

class AllVariantsProcessWeightsBuilder(
    private val projectLifecycleBuilder: ProjectLifecycleBuilder,
    private val processWeightSolver: ProcessWeightSolver
) {
    fun getAllVariantsWeights(
        startLabors: List<Int>,
        start: Matrix<Double>,
        startProcessIndex: Int,
        endRowIndex: Int?
    ): Map<List<Int>, List<Double>> {
        val laborsToWeights =
            (0 until 2.pow(startLabors.size).toInt())
                .map { representation ->
                    // Для получения всех вариантов трудоемксотей
                    // используем двоичное распределение чисел
                    // 0000, 0001, 0010, 0011... -> 3,1,2,5; 3,1,2,10; 3,1,4,5; 3,1,4,10
                    val laborsRepr =
                        getBinaryDistribution(startLabors.size, representation)
                        .mapIndexed { index, value ->
                            val labor = startLabors[index]
                            if (value > 0) labor * 2
                            else labor
                        }
                    // Для полученных трудомкостей строим матрицу проекта
                    val projectMatrix = projectLifecycleBuilder
                        .build(start, laborsRepr, endRowIndex)
                    // С полученной матрицей расчитываем веса процессов
                    val weights = processWeightSolver
                        .getProcessWeightsShort(projectMatrix, startProcessIndex, endRowIndex)
                    // Возвращаем Map весов и трудоемкостей
                    laborsRepr to weights
                }
        return laborsToWeights.toMap()
    }
}
