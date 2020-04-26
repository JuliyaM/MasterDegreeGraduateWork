package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
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
    private val sequentialAnalysisOfWaldRpnProcessor: SequentialAnalysisOfWaldProcessor,
    private val store: Store
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val projectID = call.request.queryParameters["projectID"]?.toIntOrNull()
            val processCount = call.request.queryParameters["processCount"]?.toIntOrNull()
            val restoredProject = projectID?.let { store.restoreProject(it) }
            val project = restoredProject ?: run {
            val randomProject = projectProvider.randomProject(processCount ?: Random.nextInt(3, 10))
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

            val solutionEfficientWaldResults = averageRiskSolutions
                .map {
                    sequentialAnalysisOfWaldProcessor.analyze(it.riskSolutions) { riskSolution -> riskSolution.solutionEfficient }
                }
                .sortedBy {
                    it.solution.solutionEfficient
                }

            store.saveWaldResults(solutionEfficientWaldResults)

            val rpnWaldResults = averageRiskSolutions
                .map {
                    sequentialAnalysisOfWaldRpnProcessor.analyze(it.riskSolutions) { riskSolution -> riskSolution.removedRpn }
                }
                .sortedBy {
                    it.solution.removedRpn
                }

            store.saveWaldResults(rpnWaldResults)

            val clearedProjectBySolutionEfficientVariants = projectAnalyzer.getProjectVariants(
                project.clearBy(solutionEfficientWaldResults)
            )

            val clearedProjectByRpnVariants = projectAnalyzer.getProjectVariants(
                project.clearBy(rpnWaldResults)
            )

            call.respondHtml(
                block = MainPageView(
                    project = project,
                    clearedProjectBySolutionEfficientVariants = clearedProjectBySolutionEfficientVariants,
                    clearedProjectByRpnVariants = clearedProjectByRpnVariants,
                    projectAnalyzeResult = projectAnalyzeResult,
                    solutionEfficientWaldResults = solutionEfficientWaldResults,
                    rpnWaldResults = rpnWaldResults
                ).getHTML()
            )
        }
    }
}


