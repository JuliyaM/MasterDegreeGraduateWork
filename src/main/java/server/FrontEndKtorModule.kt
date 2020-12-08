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
import main.java.server.ktorModuleLibrary.ktorHtmlExtentions.include
import main.java.WaldProps
import main.java.prediction.ProjectStructurePrediction
import main.java.processors.ProjectAnalyzer
import main.java.processors.ProjectLifecycleBuilder
import main.java.processors.SequentialAnalysisOfWaldProcessor
import main.java.processors.SolutionsAnalyzer
import main.java.server.ktorModuleLibrary.modules.KtorModule
import main.java.processors.repository.MockProcessesProvider
import main.java.processors.repository.ProjectsProvider
import main.java.processors.repository.MockRiskCauseProvider
import main.java.processors.repository.MockRiskProvider
import main.java.processors.repository.Store
import main.java.processors.solvers.DifferentialKolmogorovSolver
import main.java.processors.solvers.EndProjectExperimentSolver
import main.java.server.controllers.*

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
        ProjectsProvider(mockProcessesProvider)
    }

    private val projectAnalyzer by lazy {
        ProjectAnalyzer()
    }

    private val solutionsAnalyzer by lazy {
        SolutionsAnalyzer()
    }

    private val solutionEfficientProps by lazy {
        WaldProps(
            sigma = 4.52,
            u1 = 4.81,
            u0 = 1.47,
            alpha = 0.1,
            betta = 0.13
        )
    }

    private val sequentialAnalysisOfWaldSolutionEfficientProcessor by lazy {
        SequentialAnalysisOfWaldProcessor(solutionEfficientProps)
    }

    private val store by lazy {
        Store()
    }

    private val mainPageController by lazy {
        MainPageController(
            routingPath = "main",
            minimalPermission = 0,
            projectProvider = mockProjectProvider,
            projectAnalyzer = projectAnalyzer,
            solutionsAnalyzer = solutionsAnalyzer,
            sequentialAnalysisOfWaldProcessor = sequentialAnalysisOfWaldSolutionEfficientProcessor,
            projectStructurePrediction = projectStructurePrediction,
            store = store
        )
    }

    private val modificationAverageTestController by lazy {
        ModificationAverageTestController(
            routingPath = "modificationTest",
            minimalPermission = 0,
            projectProvider = mockProjectProvider,
            kolmogorovSolver = DifferentialKolmogorovSolver(),
            projectLifecycleBuilder = ProjectLifecycleBuilder(),
            endProjectExperimentSolver = EndProjectExperimentSolver(),
            projectStructurePrediction = projectStructurePrediction
        )
    }

    private val waldPageController by lazy {
        WaldPageController(
            routingPath = "wald",
            minimalPermission = 0,
            store = store
        )
    }

    private val courierController by lazy {
        CourierController(
            routingPath = "courier",
            minimalPermission = 0
        )
    }

    private val dispersionProjectPageController by lazy {
        DispersionProjectPageController(
            routingPath = "dispersion/project",
            minimalPermission = 0,
            projectsProvider = mockProjectProvider,
            predictionModel = projectStructurePrediction.fullModel,
            projectAnalyzer = projectAnalyzer
        )
    }

    private val projectStructurePrediction by lazy {
        ProjectStructurePrediction()
    }

    private val predictionPageController by lazy {
        PredictionPageController(
            routingPath = "prediction",
            minimalPermission = 0,
            projectStructurePrediction = projectStructurePrediction
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
                    include(modificationAverageTestController)
                    include(dispersionProjectPageController)
                    include(predictionPageController)
                    include(waldPageController)
                    include(courierController)
                }

            }
        }
    }

    override fun errorHandling(): StatusPages.Configuration.() -> Unit = {

    }

    override fun cancelModule(p0: Boolean): Application.() -> Unit = {

    }
}