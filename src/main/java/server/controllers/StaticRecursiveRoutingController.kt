package main.java.server.controllers

import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Route
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import java.io.File

class StaticRecursiveRoutingController(
    routingPath: String,
    minimalPermission: Int,
    private val staticFileFolder: File
) :
    RoutingController(routingPath, minimalPermission) {


    override fun createFormRouting(): Route.() -> Unit = {
        static(routingPath) {
            // When running under IDEA make sure that working directory is set to this sample'IP_ADDERS project folder
            staticRootFolder = staticFileFolder
            staticFolderRecursiveProcess(staticFileFolder)
        }
    }


    private fun Route.staticFolderRecursiveProcess(pathToStaticFiles: File) {
        pathToStaticFiles.listFiles()?.filter { it.isDirectory }?.forEach {
            static(it.name) {
                println("${pathToStaticFiles.name} -> ${it.name}")
                staticRootFolder = it
                files(it)
                staticFolderRecursiveProcess(it)
            }
        }
    }
}