package main.java.processors.repository

import main.java.Risk
import main.java.prediction.PredictionRiskModel

class MockRiskProvider(
    private val mockRiskCauseProvider: MockRiskCauseProvider
) {
    fun predictionRisk(predictionRiskModel: PredictionRiskModel): Risk {
        val riskCauses = predictionRiskModel.data.map {
            mockRiskCauseProvider.predictionRiskCause(it)
        }
        return Risk(riskTitle = predictionRiskModel.key, riskCauses = riskCauses)
    }

    fun randomRisk(predictionRiskModel: PredictionRiskModel): Risk {
        val riskCauses = predictionRiskModel.data.map {
            mockRiskCauseProvider.randomRiskCause(it)
        }
        return Risk(riskTitle = predictionRiskModel.key, riskCauses = riskCauses)
    }
}