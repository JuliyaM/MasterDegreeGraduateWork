package main.java.server.controllers

import main.java.OneRiskSolution
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import koma.pow
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.server.view.DispersionPageView
import processors.repository.MockProcessesProvider
import kotlin.random.Random

class DispersionPageController(
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
                        process.risks.map { risk -> OneRiskSolution(process, risk) }.map { it.removedRpn }
                    }
                }
                .flatten()

            val p = 1.0 / xi.size

            val dispersion = xi.sumByDouble { it.pow(2) * p } - (xi.sumByDouble { it * p }).pow(2)

            call.respondHtml(
                block = DispersionPageView(
                    dispersion = dispersion,
                    xi = xi,
                    processCount = count
                ).getHTML()
            )
        }
    }
}


