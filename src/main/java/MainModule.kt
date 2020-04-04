import extentions.*
import koma.matrix.Matrix
import processors.*
import processors.crono.ExperimentChronicler
import processors.repository.MockProcessesProvider
import processors.repository.MockProjectsProvider
import processors.repository.MockRiskCauseProvider
import processors.repository.MockRiskProvider
import processors.solvers.*

class MainModule {

    private val stationaryDistributionProcessor by lazy {
        StationaryDistributionSolver()
    }
    private val projectLifecycleBuilder by lazy {
        ProjectLifecycleBuilder()
    }

    private val differentialKolmogorovSolver by lazy {
        DifferentialKolmogorovSolver()
    }

    private val endProjectExperimentSolver by lazy {
        EndProjectExperimentSolver()
    }

    private val experimentChronicler by lazy {
        ExperimentChronicler()
    }

    private val avgExperimentSolver by lazy {
        AvgExperimentSolver()
    }

    private val processWeightSolver by lazy {
        ProcessWeightSolver(
            differentialKolmogorovSolver = differentialKolmogorovSolver,
            avgExperimentSolver = avgExperimentSolver,
            experimentChronicler = experimentChronicler,
            endProjectExperimentSolver = endProjectExperimentSolver
        )
    }

    private val allVariantsProcessWeightsBuilder by lazy {
        AllVariantsProcessWeightsBuilder(
            projectLifecycleBuilder = projectLifecycleBuilder,
            processWeightSolver = processWeightSolver
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

    fun start() {


        val randomProject = mockProjectProvider.randomProject()

        val startMatrix = randomProject.toProjectTransitionsMatrix()
        val startLabors = randomProject.processes.map { it.labor }


        validateMatrix(startMatrix, startLabors)


        val projectMatrix = projectLifecycleBuilder.build(
            start = startMatrix,
            labors = startLabors,
            endRowIndex = randomProject.endProjectIndex
        )


        //for print algoritm
        processWeightSolver.printProcessWeightsAlgorithm(
            start = startMatrix,
            labors = startLabors,
            projectMatrix = projectMatrix,
            startProcessIndex = randomProject.startProcessIndex,
            endRowIndex = randomProject.endProjectIndex
        )


        val laborsToWeights =
            allVariantsProcessWeightsBuilder.getAllVariantsWeights(
                startLabors = startLabors,
                start = startMatrix,
                startProcessIndex = randomProject.startProcessIndex,
                endRowIndex = randomProject.endProjectIndex
            )

        experimentChronicler.choseOneWeightsAlgorithm(randomProject.startProcessIndex, laborsToWeights)

        val processesWithoutEnd = randomProject.processes.toMutableList().apply {
            removeAt(randomProject.endProjectIndex)
        }

        val projects = laborsToWeights.map { (labor, weight) ->
            randomProject.copy(
                processes = processesWithoutEnd.mapIndexed { index, analyzedProcess ->
                    analyzedProcess.copy(
                        labor = labor[index],
                        weight = weight[index]
                    )
                }
            )
        }

        experimentChronicler.projectsResult(projects)

        println("----------")
        println(experimentChronicler.print())
        println("----------")
    }
}

private fun validateMatrix(start: Matrix<Double>, labors: List<Int>) {
    if (labors.size != start.numRows())
        error("Invalid labors count")

    if (start.numRows() < 1) error("Invalid matrix size")

    start.forEachRowO { rowIndex, row ->
        val elementSum = row.elementSum()
//        if (elementSum != 1.0 && elementSum != 0.0) error("Invalid row sum index: $rowIndex, row: $row")
    }
}

private fun findEndlessRowIndex(P: Matrix<Double>): Int? {
    val onlyReturnedP = P.firstOrNullRow { rowIndex, row ->
        row.anyIndexed { _: Int, colIndex: Int, ele: Double ->
            colIndex == rowIndex && ele == 1.0
        }
    }
    val nullRow = P.firstOrNullRow { _, row -> row.elementSum() == 0.0 }
    val endRow = onlyReturnedP ?: nullRow ?: return null
    return endRow.index
}