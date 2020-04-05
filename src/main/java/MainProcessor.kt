package main.java

import AnalyzedProject
import koma.matrix.Matrix
import main.java.processors.solvers.AllVariantsProcessWeightsBuilder
import main.java.processors.solvers.ProcessWeightSolver
import main.java.processors.solvers.WithEndProcessWeightSolveResult
import main.java.processors.solvers.WithoutEndProcessWeightSolveResult
import processors.*
import processors.solvers.*

class MainProcessor {
    private val projectLifecycleBuilder by lazy {
        ProjectLifecycleBuilder()
    }

    private val differentialKolmogorovSolver by lazy {
        DifferentialKolmogorovSolver()
    }

    private val endProjectExperimentSolver by lazy {
        EndProjectExperimentSolver()
    }

    private val avgExperimentSolver by lazy {
        AvgExperimentSolver()
    }

    private val processWeightSolver by lazy {
        ProcessWeightSolver(
            differentialKolmogorovSolver = differentialKolmogorovSolver,
            avgExperimentSolver = avgExperimentSolver,
            endProjectExperimentSolver = endProjectExperimentSolver
        )
    }

    private val allVariantsProcessWeightsBuilder by lazy {
        AllVariantsProcessWeightsBuilder(
            projectLifecycleBuilder = projectLifecycleBuilder,
            processWeightSolver = processWeightSolver
        )
    }

    fun analyze(randomProject: AnalyzedProject): ProjectAnalyzeResult {
        val (startMatrix, startLabors, endProjectIndex) = randomProject.getSolveStartInfo()
        val projectMatrix = projectLifecycleBuilder.build(
            start = startMatrix,
            labors = startLabors,
            endRowIndex = endProjectIndex
        )

        val withoutEndProjectProcessesWeights = processWeightSolver.getWithoutEndProjectProcessesWeights(
            labors = startLabors,
            projectMatrix = projectMatrix
        )

        val projectWithEndProcessWeights = if (endProjectIndex != null) {
            processWeightSolver.getProjectWithEndProcessWeights(projectMatrix, randomProject)
        } else null


        val laborsToWeights =
            allVariantsProcessWeightsBuilder.getAllVariantsWeights(
                startLabors = startLabors,
                start = startMatrix,
                startProcessIndex = randomProject.startProcessIndex,
                endRowIndex = endProjectIndex
            )


        val processesWithoutEnd =
            if (endProjectIndex != null) randomProject.processes.toMutableList().apply { removeAt(endProjectIndex) }
            else randomProject.processes

        val projectsVariants =
            laborsToWeights
                .map { (labor, weight) ->
                    randomProject.copy(
                        processes = processesWithoutEnd.mapIndexed { index, analyzedProcess ->
                            analyzedProcess.copy(
                                labor = labor.getOrNull(index) ?: 0,
                                weight = weight.getOrNull(index) ?: 0.0
                            )
                        }
                    )
                }
                .sortedBy {
                    it.rpn
                }


        return ProjectAnalyzeResult(
            projectMatrix = projectMatrix,
            withoutEndProcessWeightSolveResult = withoutEndProjectProcessesWeights,
            withEndProcessWeightSolveResult = projectWithEndProcessWeights,
            projectsVariants = projectsVariants
        )
    }

}

data class ProjectAnalyzeResult(
    val projectMatrix: Matrix<Double>,
    val withoutEndProcessWeightSolveResult: WithoutEndProcessWeightSolveResult,
    val withEndProcessWeightSolveResult: WithEndProcessWeightSolveResult?,
    val projectsVariants: List<AnalyzedProject>
)