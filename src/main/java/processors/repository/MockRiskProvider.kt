package main.java.processors.repository

import main.java.Risk
import main.java.RiskCause
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
        return risk(riskCauses, predictionRiskModel.key)
    }

    fun randomRisk(riskCauseCount: Int = Random.nextInt(2, 10)): Risk {
        val riskCauses = (0 until riskCauseCount).map {
            mockRiskCauseProvider.randomRiskCause()
        }
        return risk(riskCauses, randomNames.random())
    }

    private fun risk(riskCauses: List<RiskCause>, name: String): Risk {
        val riskCauseWeightSum = riskCauses.sumByDouble { it.weight }
        return Risk(
            name = name,
            riskCauses = riskCauses.map { it.copy(weight = it.weight / riskCauseWeightSum) }
        )
    }
}