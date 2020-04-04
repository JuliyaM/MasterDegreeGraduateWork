package ktorModuleLibrary.ktorHtmlExtentions

import io.ktor.routing.Route

abstract class RoutingController(val routingPath: String, val minimalPermission : Int) {
    abstract fun createFormRouting(): Route.() -> Unit
}