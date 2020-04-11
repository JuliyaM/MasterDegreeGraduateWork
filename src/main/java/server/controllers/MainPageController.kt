package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.SolutionDecision
import main.java.processors.ProjectAnalyzer
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.SolutionsAnalyzer
import main.java.processors.repository.Store
import main.java.server.view.MainPageView
import main.java.processors.repository.MockProjectsProvider

class MainPageController(
    routingPath: String,
    minimalPermission: Int,
    private val mockProjectProvider: MockProjectsProvider,
    private val projectAnalyzer: ProjectAnalyzer,
    private val solutionsAnalyzer: SolutionsAnalyzer,
    private val sequentialAnalysisOfWaldProcessor: SequentialAnalysisOfWaldProcessor,
    private val store: Store
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val projectID = call.request.queryParameters["projectID"]?.toIntOrNull()
            val restoredProject = projectID?.let { store.restoreProject(it) }
            val project = restoredProject ?: run {
                val randomProject = mockProjectProvider.randomProject()
                store.saveProject(randomProject)
                randomProject
            }
            if (restoredProject == null) return@get call.respondRedirect {
                this.parameters["projectID"] = project.id.toString()
            }
            val projectAnalyzeResult = store.restoreProjectAnalyzeResult(project.id) ?: run {
                val analyze = projectAnalyzer.analyze(project)
                store.saveProjectAnalyzeResult(project, analyze)
                analyze
            }

            val averageRiskSolutions = solutionsAnalyzer.averageRiskSolutions(projectAnalyzeResult)

            store.saveAverageRiskSolutions(averageRiskSolutions)

            val waldResults = averageRiskSolutions.map {
                sequentialAnalysisOfWaldProcessor.analyze(it.riskSolutions)
            }

            store.saveWaldResults(waldResults)


            val clearedProjectVariants = projectAnalyzer.getProjectVariants(
                project.copy(processes = project.processes
                    .map { process ->
                        val clearedRisks = process.risks.mapNotNull { risk ->
                            when (waldResults.find { it.solution.risk.id == risk.id }?.solutionDecision) {
                                SolutionDecision.NONE -> risk
                                SolutionDecision.ACCEPT -> null
                                SolutionDecision.DECLINE -> risk
                                null -> throw Exception("no decision")
                            }
                        }

                        val newRiskSumWeight = clearedRisks.sumByDouble { it.weight }
                        //normalize weights
                        process.copy(risks = clearedRisks.map { it.copy(weight = it.weight / newRiskSumWeight) })
                    })
            )

            call.respondHtml(
                block = MainPageView(
                    project = project,
                    clearedProjectVariants = clearedProjectVariants,
                    projectAnalyzeResult = projectAnalyzeResult,
                    waldResults = waldResults
                ).getHTML()
            )
        }
    }
}


