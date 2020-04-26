package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.prediction.ProjectStructurePrediction
import main.java.server.view.PredictionPageView

class PredictionPageController(
    routingPath: String,
    minimalPermission: Int,
    private val projectStructurePrediction : ProjectStructurePrediction
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            call.respondHtml(
                block = PredictionPageView(projectStructurePrediction).getHTML()
            )
        }
    }
}


