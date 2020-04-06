package main.java.processors

import AnalyzedProject
import Solution

class SolutionsAnalyzer {
    fun getSolutions(project: AnalyzedProject): List<Solution> {
        val solutions = project.processes
            .map { process ->
                process.risks.map { risk ->
                    risk.riskCauses.map {
                        Solution(process, risk, it)
                    }
                }.flatten()
            }
            .flatten()
            .sortedBy {
                it.solutionEfficient
            }

        return solutions
    }
}