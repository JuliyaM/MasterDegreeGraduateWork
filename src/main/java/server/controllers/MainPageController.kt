package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.MainProcessor
import main.java.server.view.MainPageView
import processors.repository.MockProjectsProvider

class MainPageController(
    routingPath: String,
    minimalPermission: Int,
    private val mockProjectProvider : MockProjectsProvider,
    private val mainProcessor : MainProcessor
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val randomProject = mockProjectProvider.randomProject()
            val projectAnalyzeResult = mainProcessor.analyze(randomProject)
            call.respondHtml(block = MainPageView(randomProject,projectAnalyzeResult).getHTML())
        }
    }
}


