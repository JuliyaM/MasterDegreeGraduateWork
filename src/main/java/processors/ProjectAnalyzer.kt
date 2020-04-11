package main.java.processors

import main.java.AnalyzedProject
import koma.matrix.Matrix
import main.java.processors.solvers.*
import processors.solvers.*

class ProjectAnalyzer {
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

    fun analyze(startProject: AnalyzedProject): ProjectAnalyzeResult {
        val (startMatrix, startLabors, endProjectIndex) = startProject.getSolveStartInfo()
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
            processWeightSolver.getProjectWithEndProcessWeights(projectMatrix, startProject)
        } else null


        val projectsVariants =
            getProjectVariants(startLabors, startMatrix, startProject, endProjectIndex)


        return ProjectAnalyzeResult(
            startProject = startProject,
            projectMatrix = projectMatrix,
            withoutEndProcessWeightSolveResult = withoutEndProjectProcessesWeights,
            withEndProcessWeightSolveResult = projectWithEndProcessWeights,
            projectsVariants = projectsVariants
        )
    }

    fun getProjectVariants(startProject: AnalyzedProject): List<AnalyzedProject> {
        val (startMatrix, startLabors, endProjectIndex) = startProject.getSolveStartInfo()
        return getProjectVariants(
            startLabors = startLabors,
            startMatrix = startMatrix,
            startProject = startProject,
            endProjectIndex = endProjectIndex
        )
    }

    private fun getProjectVariants(
        startLabors: List<Int>,
        startMatrix: Matrix<Double>,
        startProject: AnalyzedProject,
        endProjectIndex: Int?
    ): List<AnalyzedProject> {
        val laborsToWeights =
            allVariantsProcessWeightsBuilder.getAllVariantsWeights(
                startLabors = startLabors,
                start = startMatrix,
                startProcessIndex = startProject.startProcessIndex,
                endRowIndex = endProjectIndex
            )


        val processesWithoutEnd =
            if (endProjectIndex != null) startProject.processes.toMutableList().apply { removeAt(endProjectIndex) }
            else startProject.processes

        val projectsVariants =
            laborsToWeights
                .map { (labor, weight) ->
                    startProject.copy(
                        processes = processesWithoutEnd.mapIndexed { index, analyzedProcess ->
                            analyzedProcess.copy(
                                labor = labor.getOrNull(index) ?: 0,
                                weight = weight.getOrNull(index) ?: 0.0
                            )
                        },
                        isOriginal = false
                    )
                }
                .sortedBy {
                    it.rpn
                }
        return projectsVariants
    }

}

data class ProjectAnalyzeResult(
    val startProject: AnalyzedProject,
    val projectMatrix: Matrix<Double>,
    val withoutEndProcessWeightSolveResult: WithoutEndProcessWeightSolveResult,
    val withEndProcessWeightSolveResult: WithEndProcessWeightSolveResult?,
    val projectsVariants: List<AnalyzedProject>
)