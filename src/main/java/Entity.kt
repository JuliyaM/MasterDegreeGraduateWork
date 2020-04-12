package main.java

import koma.matrix.Matrix
import main.java.extentions.average
import main.java.extentions.round

data class RiskCause(
    val name: String,
    val probability: Double,
    val detectability: Double,
    val significance: Double,
    val solutionCost: Double,
    val weight: Double
) {
    val rpn: Double = detectability * probability * significance * weight

    val id = ID++

    companion object {
        private var ID = 0
    }
}

data class Risk(
    val name: String,
    val riskCauses: List<RiskCause>
) {
    val rpn: Double = riskCauses.sumByDouble { it.rpn }
    val solutionCost: Double = riskCauses.sumByDouble { it.solutionCost }

    val id = ID++

    companion object {
        private var ID = 0
    }
}

data class AnalyzedProcess(
    val name: String,
    val risks: List<Risk>,
    val labor: Int,
    val weight: Double = 0.0,
    val transitions: List<Double>,
    val id: Int = ID++
) {
    val rpn: Double = risks.sumByDouble { it.rpn } * weight
    fun withWeight(weightValue: Double) = copy(weight = weightValue)

    companion object {
        private var ID = 0

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
    val endProjectIndex: Int?,
    val id: Int = ID++,
    val isOriginal: Boolean = true
) {
    val rpn: Double = processes.sumByDouble { it.rpn }

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

    fun clearBy(solutionEfficientWaldResults: List<SequentialAnalysisOfWaldResult>): AnalyzedProject {
        return copy(processes = processes
            .map { process ->
                val clearedRisks = process.risks.mapNotNull { risk ->
                    when (solutionEfficientWaldResults.find { it.solution.risk.id == risk.id }?.solutionDecision) {
                        SolutionDecision.NONE -> risk
                        SolutionDecision.ACCEPT -> null
                        SolutionDecision.DECLINE -> risk
                        null -> throw Exception("no decision")
                    }
                }

                //normalize weights
                process.copy(risks = clearedRisks)
            })
    }

    companion object {
        private var ID = 0
    }

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
    val id: Int
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
    override val removedRpn: Double = (riskCause.rpn * process.weight)
    override val solutionCost: Double = riskCause.solutionCost
    override val solutionEfficient: Double = removedRpn * 100 / riskCause.solutionCost
}

data class OneRiskSolution(
    override val process: AnalyzedProcess,
    override val risk: Risk,
    override val id: Int = ID++
) : RiskSolution {
    override val removedRpn: Double = (risk.rpn * process.weight)
    override val solutionCost: Double = risk.solutionCost
    override val solutionEfficient: Double = removedRpn * 100 / risk.solutionCost


    companion object {
        private var ID = 0
    }
}

class AverageRiskCauseSolution(val riskCauseSolutions: List<OneRiskCauseSolution>) : RiskCauseSolution {
    override val process: AnalyzedProcess = riskCauseSolutions.first().process
    override val risk: Risk = riskCauseSolutions.first().risk
    override val riskCause: RiskCause = riskCauseSolutions.first().riskCause
    override val removedRpn: Double = riskCauseSolutions.sumByDouble { it.removedRpn }
    override val solutionCost: Double = riskCauseSolutions.sumByDouble { it.solutionCost }
    override val solutionEfficient: Double = riskCauseSolutions.sumByDouble { it.solutionEfficient }
}

class AverageRiskSolution(val riskSolutions: List<OneRiskSolution>, override val id: Int = ID++) : RiskSolution {
    override val process: AnalyzedProcess = riskSolutions.first().process //todo remove and set process to risk
    override val risk: Risk = riskSolutions.first().risk
    override val removedRpn: Double = riskSolutions.sumByDouble { it.removedRpn / riskSolutions.count() }
    override val solutionCost: Double = riskSolutions.sumByDouble { it.solutionCost / riskSolutions.count() }
    override val solutionEfficient: Double = riskSolutions.sumByDouble { it.solutionEfficient / riskSolutions.count() }


    companion object {
        private var ID = 0
    }
}

data class WaldProps(
    val sigma: Double,
    val u1: Double,
    val u0: Double,
    val alpha: Double,
    val betta: Double
)

enum class SolutionDecision(val russianName: String) {
    NONE("недостаточно наблюдений"),
    ACCEPT("принять"),
    DECLINE("отклонить")
}

data class SequentialAnalysisOfWaldResult(
    val solution: RiskSolution,
    val solutionDecision: SolutionDecision,
    val resultStep: Int?,
    val aiList: List<Double>,
    val riList: List<Double>,
    val cumulativeEfficient: List<Double>,
    val id: Int = ID++
) {
    companion object {
        private var ID = 0
    }
}

val List<SequentialAnalysisOfWaldResult>.acceptCost
    get() = this.filter { it.solutionDecision == SolutionDecision.ACCEPT }
        .sumByDouble { it.solution.solutionCost }

val List<SequentialAnalysisOfWaldResult>.efficient
    get() = this.filter { it.solutionDecision == SolutionDecision.ACCEPT }
        .average { it.solution.removedRpn * 100 / it.solution.solutionCost }