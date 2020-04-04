package main.java.server

import com.google.gson.GsonBuilder
import main.java.server.features.FileProviderFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import ktorModuleLibrary.ktorHtmlExtentions.include
import main.java.server.controllers.StaticRecursiveRoutingController
import main.java.server.ktorModuleLibrary.modules.KtorModule

const val PRODUCTION = false

class FrontEndKtorModule : KtorModule() {

    private val fileProviderFeature = FileProviderFeature()

    private val staticRecursiveRoutingController by lazy {
        StaticRecursiveRoutingController("static", 0, fileProviderFeature.pathToStaticFiles)
    }

    @KtorExperimentalAPI
    override fun initializeModule(coroutineScope: CoroutineScope): Application.() -> Unit {

        return {
            runBlocking {
                installFeature(fileProviderFeature)

                install(DefaultHeaders)
                install(ContentNegotiation) {
                    gson {
                        register(ContentType.Application.Json, GsonConverter(GsonBuilder().apply {
                        }.create()))
                    }
                }

                routing {
                    include(staticRecursiveRoutingController)
                }

            }
        }
    }

    override fun errorHandling(): StatusPages.Configuration.() -> Unit = {

    }

    override fun cancelModule(p0: Boolean): Application.() -> Unit = {

    }
}