package main.java.processors

import main.java.OneRiskSolution
import main.java.SequentialAnalysisOfWaldResult
import main.java.SolutionDecision
import main.java.WaldProps
import koma.ln
import koma.pow
import main.java.extentions.reductions

data class SequentialAnalysisOfWaldProcessor(
    val waldProps: WaldProps
) {

    inline fun analyze(
        riskSolutions: List<OneRiskSolution>,
        getParam: (OneRiskSolution) -> Double
    ): SequentialAnalysisOfWaldResult {
        // Получаем границы анализа Вальда
        val (aiList, riList) = processWaldProps(riskSolutions.size, waldProps)
        // Получаем значение кумулятивной (накапливаемой) эффективности решений
        val cumulativeEfficient =
            riskSolutions
                .map(getParam)
                .reductions(0.0) { d0, d1 -> d0 + d1 }.toList()

        // Проверям удовлетворят ли последовательность
        // одному из условия анализа Вальда
        val result = cumulativeEfficient.withIndex().find { (index, efficient) ->
            val ai = aiList[index]
            val ri = riList[index]
            efficient <= ai || efficient >= ri
        }

        return SequentialAnalysisOfWaldResult(
            solution = riskSolutions.first(),
            solutionDecision = when {
                result == null -> SolutionDecision.NONE
                result.value >= riList[result.index] -> SolutionDecision.ACCEPT
                result.value <= aiList[result.index] -> SolutionDecision.DECLINE
                else -> SolutionDecision.NONE
            },
            resultStep = result?.index,
            aiList = aiList.subList(0, result?.index?.plus(1) ?: aiList.size),
            riList = riList.subList(0, result?.index?.plus(1) ?: riList.size),
            cumulativeEfficient = cumulativeEfficient.subList(0, result?.index?.plus(1) ?: cumulativeEfficient.size)
        )
    }

    fun processWaldProps(count: Int, waldProps: WaldProps): Pair<List<Double>, List<Double>> {
        return with(waldProps) {
            val multiplEq = sigma.pow(2) / (u1 - u0)
            val A = ln(betta / (1 - alpha))
            val B = ln((1 - betta) / alpha)

            (0 until count).map { i ->
                val plusEq = (i + 1) * (u1 + u0) / 2
                val ai = multiplEq * A + plusEq
                val ri = multiplEq * B + plusEq
                ai to ri
            }.let { aiRi -> aiRi.map { it.first } to aiRi.map { it.second } }
        }
    }
}
