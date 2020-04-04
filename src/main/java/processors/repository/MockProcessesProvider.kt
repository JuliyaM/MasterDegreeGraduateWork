package processors.repository

import AnalyzedProcess
import kotlin.random.Random

class MockProcessesProvider(
    private val riskProvider: MockRiskProvider
) {
    private val randomNames = listOf(
        "Проектирование",
        "Анализ",
        "Разработка",
        "Тестирование",
        "Изучение",
        "DataMining",
        "DevOps"
    )

    fun randomProcess(riskCount: Int = Random.nextInt(2,6)): AnalyzedProcess {
        val risks = (0 until riskCount).map {
            riskProvider.randomRisk()
        }

        val risksCauseWeightSum = risks.sumByDouble { it.weight }
        return AnalyzedProcess.EMPTY.copy(
            name = randomNames.random(),
            risks = risks.map { it.copy(weight = it.weight / risksCauseWeightSum) },
            labor = Random.nextInt(10, 100)
        )
    }

    fun endProcess(processCount: Int): AnalyzedProcess {
        return AnalyzedProcess.EMPTY.copy(
            name = "Завершение",
            transitions = (0 until processCount).map { 0.0 }
        )
    }
}