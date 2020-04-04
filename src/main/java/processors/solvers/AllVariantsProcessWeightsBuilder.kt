package processors.solvers

import extentions.getBinaryDistribution
import koma.matrix.Matrix
import koma.pow
import processors.ProjectLifecycleBuilder

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
            (0 until 2.pow(startLabors.size - 1).toInt())
                .map { representation ->
                    val laborsRepr = getBinaryDistribution(startLabors.size, representation)
                        .mapIndexed { index, value ->
                            val labor = startLabors[index]
                            if (value > 0) labor * 2
                            else labor
                        }

                    val projectMatrix = projectLifecycleBuilder.build(start, laborsRepr, endRowIndex)
                    val weights = processWeightSolver.getProcessWeightsShort(projectMatrix, startProcessIndex, endRowIndex)
                    laborsRepr to weights
                }
        return laborsToWeights.toMap()
    }
}