package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.processors.repository.Store
import main.java.server.view.WaldPageView

class WaldPageController(
    routingPath: String,
    minimalPermission: Int,
    private val store: Store
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val waldID = call.request.queryParameters["waldID"]?.toIntOrNull()
                ?: return@get call.respondRedirect("/main")
            val analysisOfWaldResult = store.restoreWaldResultsByID(waldID)
                ?: return@get call.respondRedirect("/main")

            call.respondHtml(
                block = WaldPageView(analysisOfWaldResult).getHTML()
            )
        }
    }
}


