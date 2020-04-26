package main.java.processors.repository

import main.java.AnalyzedProcess
import main.java.prediction.PredictionProcessModel
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

    fun randomProcess(riskCount: Int = Random.nextInt(2, 6)): AnalyzedProcess {
        val risks = (0 until riskCount).map {
            riskProvider.randomRisk()
        }
        return AnalyzedProcess.EMPTY.copy(
            name = randomNames.random(),
            risks = risks,
            labor = Random.nextInt(10, 100)
        )
    }

    fun predictionProcess(predictionProcessModel: PredictionProcessModel): AnalyzedProcess {
        val risks = predictionProcessModel.data.map {
            riskProvider.predictionRisk(it)
        }
        return AnalyzedProcess.EMPTY.copy(
            name = predictionProcessModel.key.russianName,
            risks = risks,
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

