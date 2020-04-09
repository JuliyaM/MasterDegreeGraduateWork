import koma.matrix.Matrix

data class RiskCause(
    val name: String,
    val probability: Double,
    val detectability: Double,
    val significance: Int,
    val solutionCost: Double,
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
    val solutionCost: Double = riskCauses.sumByDouble { it.solutionCost }
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

interface Solution {
    val removedRpn: Double
    val solutionCost: Double
    val solutionEfficient: Double
}

interface RiskCauseSolution {
    val process: AnalyzedProcess
    val risk: Risk
    val riskCause: RiskCause
    val removedRpn: Double
    val solutionCost: Double
    val solutionEfficient: Double
}

interface RiskSolution {
    val process: AnalyzedProcess
    val risk: Risk
    val removedRpn: Double
    val solutionCost: Double
    val solutionEfficient: Double
}

data class OneRiskCauseSolution(
    override val process: AnalyzedProcess,
    override val risk: Risk,
    override val riskCause: RiskCause
) : RiskCauseSolution {
    override val removedRpn: Double = (riskCause.rpn * risk.weight * process.weight)
    override val solutionCost: Double = riskCause.solutionCost
    override val solutionEfficient: Double = removedRpn / riskCause.solutionCost
}

data class OneRiskSolution(
    override val process: AnalyzedProcess,
    override val risk: Risk
) : RiskSolution {
    override val removedRpn: Double = (risk.rpn * process.weight)
    override val solutionCost: Double = risk.solutionCost
    override val solutionEfficient: Double = removedRpn / risk.solutionCost
}

class AverageRiskCauseSolution(riskCauseSolutions: List<OneRiskCauseSolution>) : RiskCauseSolution {
    override val process: AnalyzedProcess = riskCauseSolutions.first().process
    override val risk: Risk = riskCauseSolutions.first().risk
    override val riskCause: RiskCause = riskCauseSolutions.first().riskCause
    override val removedRpn: Double = riskCauseSolutions.sumByDouble { it.removedRpn }
    override val solutionCost: Double = riskCauseSolutions.sumByDouble { it.solutionCost }
    override val solutionEfficient: Double = riskCauseSolutions.sumByDouble { it.solutionEfficient }
}

class AverageRiskSolution(riskSolutions: List<OneRiskSolution>) : RiskSolution {
    override val process: AnalyzedProcess = riskSolutions.first().process
    override val risk: Risk = riskSolutions.first().risk
    override val removedRpn: Double = riskSolutions.sumByDouble { it.removedRpn / riskSolutions.count() }
    override val solutionCost: Double = riskSolutions.sumByDouble { it.solutionCost / riskSolutions.count() }
    override val solutionEfficient: Double = riskSolutions.sumByDouble { it.solutionEfficient / riskSolutions.count() }
}