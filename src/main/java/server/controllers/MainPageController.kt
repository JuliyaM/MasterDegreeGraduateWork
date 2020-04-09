package main.java.server.controllers

import AverageRiskSolution
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.extentions.transpose
import main.java.processors.ProjectAnalyzer
import main.java.processors.SolutionsAnalyzer
import main.java.server.view.MainPageView
import processors.repository.MockProjectsProvider

class MainPageController(
    routingPath: String,
    minimalPermission: Int,
    private val mockProjectProvider: MockProjectsProvider,
    private val projectAnalyzer: ProjectAnalyzer,
    private val solutionsAnalyzer: SolutionsAnalyzer
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val randomProject = mockProjectProvider.randomProject()
            val projectAnalyzeResult = projectAnalyzer.analyze(randomProject)

            val maxRpnProject = projectAnalyzeResult
                .projectsVariants
                .maxBy { it.rpn } ?: randomProject

            val averageRiskSolutions = projectAnalyzeResult.projectsVariants
                .map { solutionsAnalyzer.getSolutions(it).second }
                .transpose()
                .map { AverageRiskSolution(it) }


            val (_, maxProjectRiskSolutions) = solutionsAnalyzer.getSolutions(maxRpnProject)

            call.respondHtml(
                block = MainPageView(
                    project = randomProject,
                    projectAnalyzeResult = projectAnalyzeResult,
                    maxProjectRiskSolutions = maxProjectRiskSolutions,
                    averageRiskSolutions = averageRiskSolutions
                ).getHTML()
            )
        }
    }
}


