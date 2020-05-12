package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import koma.pow
import koma.sqrt
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.OneRiskSolution
import main.java.prediction.PredictionModel
import main.java.processors.ProjectAnalyzer
import main.java.processors.repository.ProjectsProvider
import main.java.server.view.DispersionPageView
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class DispersionProjectPageController(
    routingPath: String,
    minimalPermission: Int,
    private val projectsProvider: ProjectsProvider,
    private val predictionModel: PredictionModel,
    private val projectAnalyzer: ProjectAnalyzer
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val queryCount = max(
                call.request.queryParameters["count"]?.toIntOrNull() ?: 100_000,
                predictionModel.data.size
            )
            val count = queryCount / predictionModel.data.size

            val xi = (0..count)
                .map {
                    val project = projectsProvider.predictionProjectProvider(predictionModel.data)
                    val projectVariants = projectAnalyzer.getProjectVariants(project)
                    projectVariants.map { projectVariant ->
                        projectVariant.processes.map { process ->
                            process.risks.map { risk ->
                                OneRiskSolution(process, risk).solutionEfficient
                            }
                        }
                    }
                }.flatten().flatten().flatten()

            val avg = xi.average()
            val sigma = sqrt(xi.sumByDouble { pow(it - avg, 2) } / xi.size)


            call.respondHtml(
                block = DispersionPageView(
                    sigma = sigma,
                    xi = xi,
                    processCount = count
                ).getHTML()
            )
        }
    }
}


