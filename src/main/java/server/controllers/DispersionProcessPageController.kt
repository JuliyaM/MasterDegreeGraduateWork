package main.java.server.controllers

import main.java.OneRiskSolution
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import koma.pow
import koma.sqrt
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.server.view.DispersionPageView
import main.java.processors.repository.MockProcessesProvider
import kotlin.random.Random

class DispersionProcessPageController(
    routingPath: String,
    minimalPermission: Int,
    private val mockProcessesProvider: MockProcessesProvider
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 100_000

            val xi = (0..count)
                .map {
                    mockProcessesProvider.randomProcess().withWeight(Random.nextDouble()).let { process ->
                        process.risks.map { risk -> OneRiskSolution(process, risk) }.map { it.solutionEfficient }
                    }
                }
                .flatten()

            val avg = xi.average()
            val sigma = sqrt(xi.sumByDouble { it - avg } / xi.size)

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


