package main.java.processors.repository

import main.java.Risk
import main.java.prediction.PredictionRiskModel
import kotlin.random.Random

class MockRiskProvider(
    private val mockRiskCauseProvider: MockRiskCauseProvider
) {
    private val randomNames = listOf(
        "Риск потери репутации из-за задержки поставки АПИ.",
        "Риск потери репутации из-за нарушения договора по затраченным ресурсам."
    )

    fun predictionRisk(predictionRiskModel: PredictionRiskModel): Risk {
        val riskCauses = predictionRiskModel.data.map {
            mockRiskCauseProvider.predictionRiskCause(it)
        }
        return Risk(riskTitle = predictionRiskModel.key, riskCauses = riskCauses)
    }

    fun randomRisk(riskCauseCount: Int = Random.nextInt(2, 10)): Risk {
        val riskCauses = (0 until riskCauseCount).map {
            mockRiskCauseProvider.randomRiskCause()
        }
        return Risk(riskTitle = randomNames.random(), riskCauses = riskCauses)
    }
}