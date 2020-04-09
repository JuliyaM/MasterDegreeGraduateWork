package main.java.processors

import AnalyzedProject
import OneRiskCauseSolution
import OneRiskSolution

class SolutionsAnalyzer {
    fun getSolutions(project: AnalyzedProject): Pair<List<OneRiskCauseSolution>, List<OneRiskSolution>> {
        val riskCauseSolutions = project.processes
            .map { process ->
                process.risks.map { risk ->
                    risk.riskCauses.map {
                        OneRiskCauseSolution(process, risk, it)
                    }
                }.flatten()
            }
            .flatten()
            .sortedBy {
                it.solutionEfficient
            }

        val riskSolutions = project.processes
            .map { process ->
                process.risks.map { risk -> OneRiskSolution(process, risk) }
            }
            .flatten()
            .sortedBy {
                it.solutionEfficient
            }

        return riskCauseSolutions to riskSolutions
    }
}