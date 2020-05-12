package main.java.processors.repository

import main.java.RiskCause
import main.java.prediction.PredictionRiskCauseModel
import kotlin.random.Random

class MockRiskCauseProvider {
    private val randomNames = listOf(
        "Отклонение от плана по ресурсам в ходе реализации АПИ",
        "Отклонение от плана по ресурсам в ходе реализации АПИ.",
        "Высокая доля некорректно работающего функционала найденная на моменте тестирования.",
        "Высокое отклонение от плана по срокам в ходе реализации АПИ."
    )


    fun randomRiskCause() =
        RiskCause(
            causeTitle = randomNames.random(),
            probability = Random.nextDouble(),
            detectability = Random.nextDouble(),
            significance = Random.nextDouble(1.0,11.0),
            solutionCost = Random.nextDouble(1.0,11.0)
        )

    fun predictionRiskCause(predictionRiskCauseModel: PredictionRiskCauseModel) =
        RiskCause(
            causeTitle = predictionRiskCauseModel.key,
            probability = Random.nextDouble(),
            detectability = Random.nextDouble(),
            significance = Random.nextDouble(1.0,11.0),
            solutionCost = Random.nextDouble(1.0,11.0)
        )
}