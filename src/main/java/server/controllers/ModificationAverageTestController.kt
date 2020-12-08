package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import koma.abs
import koma.extensions.get
import koma.extensions.map
import koma.extensions.mapIndexed
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.prediction.ProjectStructurePrediction
import main.java.processors.ProjectLifecycleBuilder
import main.java.processors.repository.ProjectsProvider
import main.java.processors.solvers.DifferentialKolmogorovSolver
import main.java.processors.solvers.EndProjectExperimentSolver
import main.java.server.view.ModificationTestView
import kotlin.random.Random

class ModificationAverageTestController(
    routingPath: String,
    minimalPermission: Int,
    private val projectProvider: ProjectsProvider,
    private val kolmogorovSolver: DifferentialKolmogorovSolver,
    private val projectLifecycleBuilder: ProjectLifecycleBuilder,
    private val endProjectExperimentSolver: EndProjectExperimentSolver,
    private val projectStructurePrediction : ProjectStructurePrediction
) : RoutingController(routingPath, minimalPermission) {

    override fun createFormRouting(): Route.() -> Unit = {
        get(routingPath) {
            val experimentsCount =
                call.request.queryParameters["experimentsCount"]?.toIntOrNull() ?: Random.nextInt(3, 10)

            val result = (0..experimentsCount).map {
                val (startMatrix, startLabors, endProjectIndex) = projectProvider.randomProject(
                    projectStructurePrediction.fullModel.data
                )
                    .getSolveStartInfo()
                val projectMatrix = projectLifecycleBuilder.build(
                    start = startMatrix,
                    labors = startLabors,
                    endRowIndex = endProjectIndex
                )
                endProjectIndex ?: return@get call.respond(HttpStatusCode.InternalServerError)

                val idxs = (0 until projectMatrix.numRows()).filter { it != endProjectIndex }.toIntArray()

                val dropEndMatrix =
                    projectMatrix.mapIndexed { row, col, ele ->
                        if (row == col) ele + projectMatrix[row, endProjectIndex] else ele
                    }.selectRows(*idxs).selectCols(*idxs)


                val experimentalResult =
                    endProjectExperimentSolver.getOneRowResult(
                        P = projectMatrix,
                        rowStartIndex = 0,
                        experimentsCount = 1000,
                        endRowIndex = endProjectIndex
                    ).averageResult

                val normalizedDropEndResult =
                    kolmogorovSolver.solve(projectMatrix.selectRows(*idxs).selectCols(*idxs).mapRows { row ->
                        row.map { it / row.elementSum() }
                    }).toList()
                val modificationStep1Result = kolmogorovSolver.solve(dropEndMatrix).toList()
                val modificationStep2Result = kolmogorovSolver.solve(dropEndMatrix.pow(2).apply {
                    setRow(0, dropEndMatrix.getRow(0))
                }).toList()


                Triple(
                    first = experimentalResult.deltaWith(normalizedDropEndResult).average() ?: 0.0,
                    second = experimentalResult.deltaWith(modificationStep1Result).average() ?: 0.0,
                    third = experimentalResult.deltaWith(modificationStep2Result).average() ?: 0.0
                )
            }

            val normalizedDelta = result.map { it.first }
            val modifStep1Delta = result.map { it.second }
            val modifStep2Delta = result.map { it.third }


            val normalizedDeltaAverage = normalizedDelta.average() ?: 0.0
            val modifStep2DeltaAverage = modifStep2Delta.average() ?: 0.0
            val modifStep1DeltaAverage = modifStep1Delta.average() ?: 0.0

            println(normalizedDeltaAverage)
            println(modifStep2DeltaAverage)
            println(modifStep1DeltaAverage)
            println((modifStep2DeltaAverage - normalizedDeltaAverage) / normalizedDeltaAverage)
            println((modifStep1DeltaAverage - normalizedDeltaAverage) / normalizedDeltaAverage)

            call.respondHtml(
                block = ModificationTestView(
                    normalizedDelta,
                    modifStep1Delta,
                    modifStep2Delta
                ).getHTML()
            )
        }
    }
}

private fun List<Double>.deltaWith(normalizedDropEndResult: List<Double>) =
    mapIndexed { index, value -> abs(value - normalizedDropEndResult[index]) }

