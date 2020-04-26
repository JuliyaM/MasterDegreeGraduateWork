package main.java.processors.repository

import main.java.Risk
import kotlin.random.Random

class MockRiskProvider(
    private val mockRiskCauseProvider: MockRiskCauseProvider
) {
    private val randomNames = listOf(
        "Риск потери репутации из-за задержки поставки АПИ.",
        "Риск потери репутации из-за нарушения договора по затраченным ресурсам."
    )


    fun randomRisk(riskCauseCount: Int = Random.nextInt(2,10)): Risk {
        val riskCauses = (0 until riskCauseCount).map {
            mockRiskCauseProvider.randomRiskCause()
        }

        val riskCauseWeightSum = riskCauses.sumByDouble { it.weight }
        return Risk(
            name = randomNames.random(),
            riskCauses = riskCauses.map { it.copy(weight = it.weight / riskCauseWeightSum) }
//            weight = Random.nextDouble()
        )
    }
}