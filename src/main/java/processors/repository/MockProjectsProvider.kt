package processors.repository

import AnalyzedProject
import kotlin.random.Random

class MockProjectsProvider(
    private val processesProvider: MockProcessesProvider
) {
    fun randomProject(processesCount: Int = Random.nextInt(3, 10)): AnalyzedProject {
        val processes = (0 until (processesCount - 1)).map { processIndex ->
            val randomProcess = processesProvider.randomProcess()
            val randomTransitions = (0 until processesCount).map {
                if (it == processIndex) 0.0
                else Random.nextDouble()
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