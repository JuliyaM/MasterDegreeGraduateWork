package main.java.processors.repository

import main.java.AnalyzedProject
import main.java.AverageRiskSolution
import main.java.SequentialAnalysisOfWaldResult
import main.java.processors.ProjectAnalyzeResult

class Store {
    private val _projects = mutableListOf<AnalyzedProject>()
    private val _projectAnalyzeResults = mutableListOf<ProjectAnalyzeResult>()
    private val _averageRiskSolutions = mutableListOf<AverageRiskSolution>()
    private val _waldResults = mutableListOf<SequentialAnalysisOfWaldResult>()

    fun saveProject(project: AnalyzedProject) {
        _projects.removeIf { it.id == project.id }
        _projects += project
    }

    fun restoreProject(projectID: Int): AnalyzedProject? {
        return _projects.find { it.id == projectID }
    }

    fun saveProjectAnalyzeResult(project: AnalyzedProject, projectAnalyzeResult: ProjectAnalyzeResult) {
        _projectAnalyzeResults.removeIf { it.startProject.id == project.id }
        _projectAnalyzeResults += projectAnalyzeResult
    }

    fun restoreProjectAnalyzeResult(projectID: Int): ProjectAnalyzeResult? {
        return _projectAnalyzeResults.find { it.startProject.id == projectID }
    }

    fun saveAverageRiskSolutions(averageRiskSolutions: List<AverageRiskSolution>) {
        val savedIDs = averageRiskSolutions.map { it.id }
        _averageRiskSolutions.removeIf { savedIDs.contains(it.id) }
        _averageRiskSolutions += averageRiskSolutions
    }

    fun restoreAverageRiskSolutionByID(riskSolutionID: Int): AverageRiskSolution? {
        return _averageRiskSolutions.find { it.id == riskSolutionID }
    }

    fun restoreRiskSolutionByRisk(riskID: Int): AverageRiskSolution? {
        return _averageRiskSolutions.find { it.risk.id == riskID }
    }

    fun restoreRiskSolutionByProcess(processID: Int): List<AverageRiskSolution> {
        return _averageRiskSolutions.filter { it.process.id == processID }.toList()
    }

    fun saveWaldResults(waldResults: List<SequentialAnalysisOfWaldResult>) {
        val savedIDs = waldResults.map { it.id }
        _waldResults.removeIf { savedIDs.contains(it.id) }
        _waldResults += waldResults
    }

    fun restoreWaldResultsBySolution(solutionID: Int): SequentialAnalysisOfWaldResult? {
        return _waldResults.find { it.solution.id == solutionID }
    }

    fun restoreWaldResultsByID(waldID: Int): SequentialAnalysisOfWaldResult? {
        return _waldResults.find { it.id == waldID }
    }

}