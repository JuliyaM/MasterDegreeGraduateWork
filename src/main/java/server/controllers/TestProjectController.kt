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
import main.java.processors.repository.MockProjectsProvider
import kotlin.random.Random

class TestProjectController(
    routingPath: String,
    minimalPermission: Int,
    private val mockProjectProvider: MockProjectsProvider,
    private val projectAnalyzer: ProjectAnalyzer,
    private val solutionsAnalyzer: SolutionsAnalyzer,
    private val sequentialAnalysisOfWaldProcessor: SequentialAnalysisOfWaldProcessor,
    private val prediction: ProjectStructurePrediction,
    private val sequentialAnalysisOfWaldRpnProcessor: SequentialAnalysisOfWaldProcessor
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val project = mockProjectProvider.randomProject(3)
            val projectAnalyzeResult = projectAnalyzer.analyze(project)
            val averageRiskSolutions = solutionsAnalyzer.averageRiskSolutions(projectAnalyzeResult)

            val solutionEfficientWaldResults = averageRiskSolutions
                .map {
                    sequentialAnalysisOfWaldProcessor.analyze(it.riskSolutions) { riskSolution -> riskSolution.solutionEfficient }
                }
                .sortedBy {
                    it.solution.solutionEfficient
                }

            val rpnWaldResults = averageRiskSolutions
                .map {
                    sequentialAnalysisOfWaldRpnProcessor.analyze(it.riskSolutions) { riskSolution -> riskSolution.removedRpn }
                }
                .sortedBy {
                    it.solution.removedRpn
                }

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


