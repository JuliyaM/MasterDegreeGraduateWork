package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.prediction.ProjectStructurePrediction
import main.java.processors.ProjectAnalyzer
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.SolutionsAnalyzer
import main.java.processors.repository.Store
import main.java.server.view.MainPageView
import main.java.processors.repository.ProjectsProvider
import kotlin.random.Random

class MainPageController(
    routingPath: String,
    minimalPermission: Int,
    private val projectProvider: ProjectsProvider,
    private val projectAnalyzer: ProjectAnalyzer,
    private val solutionsAnalyzer: SolutionsAnalyzer,
    private val sequentialAnalysisOfWaldProcessor: SequentialAnalysisOfWaldProcessor,
    private val projectStructurePrediction: ProjectStructurePrediction,
    private val store: Store
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val processPredictions = projectStructurePrediction.fullModel.data
            val projectID = call.request.queryParameters["projectID"]?.toIntOrNull()
            val processCount = call.request.queryParameters["processCount"]?.toIntOrNull() ?: processPredictions.size
            val restoredProject = projectID?.let { store.restoreProject(it) }
            val project = restoredProject
                ?: projectProvider.randomProject(processPredictions.take(processCount)).also { store.saveProject(it) }

            if (restoredProject == null) return@get call.respondRedirect {
                this.parameters["projectID"] = project.id.toString()
            }

            val projectAnalyzeResult = store.restoreProjectAnalyzeResult(project.id)
                ?: projectAnalyzer.analyze(project).also { store.saveProjectAnalyzeResult(project, it) }

            val averageRiskSolutions = solutionsAnalyzer.averageRiskSolutions(projectAnalyzeResult.projectsVariants)

            store.saveAverageRiskSolutions(averageRiskSolutions)

            val solutionEfficientWaldResults = averageRiskSolutions
                .map {
                    sequentialAnalysisOfWaldProcessor.analyze(it.riskSolutions) { riskSolution -> riskSolution.solutionEfficient }
                }
                .sortedBy {
                    it.solution.solutionEfficient
                }

            store.saveWaldResults(solutionEfficientWaldResults)


            val clearedProjectBySolutionEfficientVariants = projectAnalyzer.getProjectVariants(
                project.clearBy(solutionEfficientWaldResults)
            )


            call.respondHtml(
                block = MainPageView(
                    project = project,
                    clearedProjectBySolutionEfficientVariants = clearedProjectBySolutionEfficientVariants,
                    projectAnalyzeResult = projectAnalyzeResult,
                    solutionEfficientWaldResults = solutionEfficientWaldResults
                ).getHTML()
            )
        }
    }
}


