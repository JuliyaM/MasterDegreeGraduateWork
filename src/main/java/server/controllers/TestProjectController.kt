package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.prediction.*
import main.java.processors.ProjectAnalyzer
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.SolutionsAnalyzer
import main.java.server.view.MainPageView
import main.java.processors.repository.ProjectsProvider
import main.java.processors.repository.Store

class TestProjectController(
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

            val model = PredictionModel.buildModel {
                process(ProcessKey.UiUx) {
                    risk(VIOLATION_PROJECT_TIME_PLAN) {
                        cause(INVALID_STANDARDS_USAGE)
                        cause(NOT_FULLY_ELABORATE_DESIGN)
                    }
                    risk(VIOLATION_RIGHT_HOLDER) {
                        cause(AUTHOR_MEDIA_USAGE)
                    }
                    risk(BAD_USER_EXPERIENCE) {
                        cause(HARD_UX_NO_HELP)
                    }
                    risk(MISMATCH_MARKET_CONDITIONS) {
                        cause(INVALID_STANDARD_USAGE)
                    }
                }

                process(ProcessKey.Developing) {
                    risk(VIOLATION_PROJECT_TIME_PLAN) {
                        cause(TERMINATION_OF_LIBRARY_SUPPORT)
                        cause(BAD_DEVELOPER_PERFORMANCE)
                        cause(BIG_COUNT_BUGS_ON_TEST)
                        cause(BAD_TEAMS_COMMUNICATION)
                        cause(BAD_ALGORITHM_COMPLEXITY)
                        cause(BAD_DOCUMENTATION_DEVELOPING)
                        cause(CONFLICTS_IN_TEAM)
                    }

                    risk(PRODUCT_WORK_FAILURES) {
                        cause(NON_STABLE_ALGORITHMS)
                        cause(BAD_HIGH_LOAD_REALISATION)
                        cause(BAD_MULTI_THREADING)
                    }

                    risk(MISMATCH_MARKET_CONDITIONS) {
                        cause(BAD_ALGORITHM_PERFORMANCE)
                        cause(BAD_TECHNICAL_DEBT)
                        cause(BAD_ALGORITHMS_LIBRARIES)
                    }
                }

                process(ProcessKey.Testing) {
                    risk(PRODUCT_WORK_FAILURES) {
                        cause(BIG_COUNT_BUGS_LOSS_ON_TEST)
                        cause(BAD_TESTING_ENVIRONMENT)
                    }
                    risk(BAD_USER_EXPERIENCE) {
                        cause(BAD_FOUND_BUGS_TIME)
                        cause(UNEXPECTED_SYSTEM_STATE)
                    }
                }

                process(ProcessKey.Delivery) {
                    risk(PRODUCT_WORK_FAILURES) {
                        cause(BAD_USER_ENVIRONMENT)
                    }
                    risk(BAD_USER_EXPERIENCE) {
                        cause(NOT_FULLY_ENVIRONMENT)
                        cause(USER_ENV_ACCESS)
                    }
                }
            }

            val project = projectProvider.predictionProjectProvider(model.data)
            val projectAnalyzeResult = projectAnalyzer.analyze(project)
            val averageRiskSolutions = solutionsAnalyzer.averageRiskSolutions(projectAnalyzeResult)

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


