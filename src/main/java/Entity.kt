import koma.matrix.Matrix

data class RiskCause(
    val name: String,
    val probability: Double,
    val detectability: Double,
    val significance: Int,
    val weight: Double
) {
    val rpn: Double = detectability * probability * significance * weight
}

data class Risk(
    val name: String,
    val weight: Double,
    val riskCauses: List<RiskCause>
) {
    val rpn: Double = riskCauses.sumByDouble { it.rpn } * weight
}

data class AnalyzedProcess(
    val name: String,
    val risks: List<Risk>,
    val labor: Int,
    val weight: Double = 0.0,
    val transitions: List<Double>
) {
    val rpn: Double = risks.sumByDouble { it.rpn } * weight
    fun withWeight(weightValue: Double) = copy(weight = weightValue)

    companion object {
        val EMPTY = AnalyzedProcess(
            weight = 0.0,
            name = "",
            labor = 0,
            transitions = listOf(),
            risks = listOf()
        )
    }
}

data class AnalyzedProject(
    val name: String,
    val processes: List<AnalyzedProcess>,
    val startProcessIndex: Int,
    val endProjectIndex: Int?
) {
    fun toProjectTransitionsMatrix(): Matrix<Double> {
        return Matrix(processes.count(), processes.count()) { rowIndex, colIndex ->
            processes[rowIndex].transitions[colIndex]
        }
    }

    fun getSolveStartInfo(): ProjectSolveStartInfo {
        val startMatrix = toProjectTransitionsMatrix()
        val startLabors = processes.filterIndexed { index, _ -> index != endProjectIndex }.map { it.labor }
        val endProjectIndex = endProjectIndex
        return ProjectSolveStartInfo(startMatrix, startLabors, endProjectIndex)
    }

    val rpn: Double = processes.sumByDouble { it.rpn }
}

data class ProjectSolveStartInfo(
    val startMatrix: Matrix<Double>,
    val startLabors: List<Int>,
    val endProjectIndex: Int?
)

data class DeltaResult(
    val relative: Double,
    val absolute: Double
)
