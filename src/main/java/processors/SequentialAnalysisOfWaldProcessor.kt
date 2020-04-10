package main.java.processors

import OneRiskSolution
import SequentialAnalysisOfWaldResult
import SolutionDecision
import WaldProps
import koma.internal.default.utils.accumulateRight
import koma.ln
import koma.pow
import main.java.extentions.reductions

class SequentialAnalysisOfWaldProcessor {

    fun analyze(waldProps: WaldProps, riskSolutions: List<OneRiskSolution>): SequentialAnalysisOfWaldResult {
        val (aiList, riList) = riskSolutions.indices.map { i ->
            with(waldProps) {
                val plusEq = (i + 1) * (u1 + u0) / 2
                val ai = (sigma.pow(2) / (u1 - u0)) * ln(betta / (1 - alpha)) + plusEq
                val bi = (sigma.pow(2) / (u1 + u0)) * ln((1 - betta) / alpha) + plusEq
                ai to bi
            }
        }.let { aiRi -> aiRi.map { it.first } to aiRi.map { it.second } }


        val cumulativeEfficient =
            riskSolutions.map { it.solutionEfficient }.reductions(0.0) { d0, d1 -> d0 + d1 }.toList()


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
            aiList = aiList,
            riList = riList,
            cumulativeEfficient = cumulativeEfficient
        )
    }
}
