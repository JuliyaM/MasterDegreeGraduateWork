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
            .sortedBy {
                it.solutionEfficient
            }
    }

    fun averageRiskSolutions(projectAnalyzeResult: ProjectAnalyzeResult): List<AverageRiskSolution> {
        return projectAnalyzeResult.projectsVariants
            .map { getSolutions(it) }
            .transpose()
            .map { AverageRiskSolution(it) }
    }
}