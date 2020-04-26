package main.java.processors.repository

import main.java.AnalyzedProject
import main.java.prediction.ContainerPredictionModel
import main.java.prediction.PredictionModel
import main.java.prediction.PredictionProcessModel
import main.java.prediction.ProjectStructurePrediction
import kotlin.random.Random


class ProjectsProvider(
    private val processesProvider: MockProcessesProvider
) {
    fun randomProject(processesCount: Int): AnalyzedProject {
        val processes = (0 until (processesCount - 1)).map { processIndex ->
            val randomProcess = processesProvider.randomProcess()
            val randomTransitions = (0 until processesCount).map {
                if (it == processIndex) 0.0
                else Random.nextInt(100).toDouble() / 100
            }
            val transitSum = randomTransitions.sumByDouble { it }
            randomProcess.copy(transitions = randomTransitions.map { it / transitSum })
        }

        return AnalyzedProject(
            name = "Проект${Random.nextInt(1000)}",
            processes = processes.toMutableList() + processesProvider.endProcess(processesCount),
            startProcessIndex = 0,
            endProjectIndex = processesCount - 1
        )
    }

    fun predictionProjectProvider(predictionProcessModels: List<PredictionProcessModel>): AnalyzedProject {
        val processesCount = predictionProcessModels.size + 1

        val processes = predictionProcessModels.mapIndexed { processIndex, model ->
            val randomProcess = processesProvider.predictionProcess(model)
            val randomTransitions = (0 until processesCount).map {
                if (it == processIndex) 0.0
                else Random.nextInt(100).toDouble() / 100
            }
            val transitSum = randomTransitions.sumByDouble { it }
            randomProcess.copy(transitions = randomTransitions.map { it / transitSum })
        }

        return AnalyzedProject(
            name = "Проект${Random.nextInt(1000)}",
            processes = processes.toMutableList() + processesProvider.endProcess(processesCount),
            startProcessIndex = 0,
            endProjectIndex = processesCount - 1
        )
    }
}