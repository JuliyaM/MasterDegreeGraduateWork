package main.java.server

import main.java.RpnSolutionEfficientProps
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
import main.java.processors.ProjectAnalyzer
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.SolutionsAnalyzer
import main.java.server.controllers.MainPageController
import main.java.server.controllers.StaticRecursiveRoutingController
import main.java.server.ktorModuleLibrary.modules.KtorModule
import processors.repository.MockProcessesProvider
import main.java.processors.repository.MockProjectsProvider
import processors.repository.MockRiskCauseProvider
import main.java.processors.repository.MockRiskProvider
import main.java.processors.repository.Store
import main.java.server.controllers.DispersionPageController
import main.java.server.controllers.WaldPageController

const val PRODUCTION = false

class FrontEndKtorModule : KtorModule() {

    private val fileProviderFeature = FileProviderFeature()

    private val staticRecursiveRoutingController by lazy {
        StaticRecursiveRoutingController(
            routingPath = "static",
            minimalPermission = 0,
            staticFileFolder = fileProviderFeature.pathToStaticFiles
        )
    }

    private val mockRiskCauseProvider by lazy {
        MockRiskCauseProvider()
    }

    private val mockRiskProvider by lazy {
        MockRiskProvider(mockRiskCauseProvider)
    }

    private val mockProcessesProvider by lazy {
        MockProcessesProvider(mockRiskProvider)
    }

    private val mockProjectProvider by lazy {
        MockProjectsProvider(mockProcessesProvider)
    }

    private val projectAnalyzer by lazy {
        ProjectAnalyzer()
    }

    private val solutionsAnalyzer by lazy {
        SolutionsAnalyzer()
    }

    private val rpnSolutionEfficientProps by lazy {
        RpnSolutionEfficientProps(
            sigma = 1.73,
            upperRpnSolutionEfficientBound = 1.48,
            lowerRpnSolutionEfficientBound = 0.12,
            alpha = 0.1,
            betta = 0.15
        )
    }

    private val sequentialAnalysisOfWaldProcessor by lazy {
        SequentialAnalysisOfWaldProcessor(rpnSolutionEfficientProps)
    }

    private val store by lazy {
        Store()
    }

    private val mainPageController by lazy {
        MainPageController(
            routingPath = "main",
            minimalPermission = 0,
            mockProjectProvider = mockProjectProvider,
            projectAnalyzer = projectAnalyzer,
            solutionsAnalyzer = solutionsAnalyzer,
            sequentialAnalysisOfWaldProcessor = sequentialAnalysisOfWaldProcessor,
            store = store
        )
    }

    private val waldPageController by lazy {
        WaldPageController(
            routingPath = "wald",
            minimalPermission = 0,
            store = store
        )
    }

    private val dispersionController by lazy {
        DispersionPageController(
            routingPath = "dispersion",
            minimalPermission = 0,
            mockProcessesProvider = mockProcessesProvider
        )
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
                    include(mainPageController)
                    include(dispersionController)
                    include(waldPageController)
                }

            }
        }
    }

    override fun errorHandling(): StatusPages.Configuration.() -> Unit = {

    }

    override fun cancelModule(p0: Boolean): Application.() -> Unit = {

    }
}