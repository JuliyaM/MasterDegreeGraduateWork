package processors.repository

import RiskCause
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
            name = randomNames.random(),
            probability = Random.nextDouble(),
            detectability = Random.nextDouble(),
            significance = Random.nextInt(11),
            weight = Random.nextDouble(),
            solutionCost = Random.nextDouble()
        )
}