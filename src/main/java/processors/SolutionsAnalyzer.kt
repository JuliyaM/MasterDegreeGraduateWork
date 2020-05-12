package main.java.processors

import main.java.AnalyzedProject
import main.java.AverageRiskSolution
import main.java.OneRiskSolution
import main.java.extentions.transpose

class SolutionsAnalyzer {
    fun getSolutions(project: AnalyzedProject): List<OneRiskSolution> {
        return project.processes
            .map { process ->
                process.risks.map { risk -> OneRiskSolution(process, risk) }
            }
            .flatten()
    }

    fun averageRiskSolutions(projectsVariants: List<AnalyzedProject>): List<AverageRiskSolution> {
        return projectsVariants
            .map { getSolutions(it) }
            .transpose()
            .map { AverageRiskSolution(it) }
    }
}