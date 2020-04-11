package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.repository.Store
import main.java.server.view.WaldPageView

class WaldPageController(
    routingPath: String,
    minimalPermission: Int,
    private val store: Store
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val solutionID = call.request.queryParameters["solutionID"]?.toIntOrNull()
                ?: return@get call.respondRedirect("/main")
            val analysisOfWaldResult = store.restoreWaldResults(solutionID)
                ?: return@get call.respondRedirect("/main")

            call.respondHtml(
                block = WaldPageView(analysisOfWaldResult).getHTML()
            )
        }
    }
}


