package main.java.server.view.fragments

import kotlinx.html.*
import main.java.SequentialAnalysisOfWaldResult
import main.java.extentions.OneChartInfo
import main.java.extentions.chartColors
import main.java.extentions.chartTag
import main.java.extentions.round
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class RiskSolutionsFragment(
    private val waldResults: List<SequentialAnalysisOfWaldResult>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {

        val riskSolutions = waldResults.map { it.solution }

        include(UiTableFragment(
            tableName = "Возможные решения",
            tHead = {
                tr {
                    th { +"#" }
                    th { +"Процесс" }
                    th { +"Риск" }
                    th { +"Избавляет от RPN" }
                    th { +"Эффективность решения" }
                    th { +"Рекомендация" }
                }
            },
            tBody = {
                riskSolutions.forEachIndexed { index, riskSolution ->
                    tr {
                        td { +index.toString() }
                        td { +riskSolution.process.name }
                        td { +riskSolution.risk.name }
                        td { +riskSolution.removedRpn.round(3).toString() }
                        td { +riskSolution.solutionEfficient.round(3).toString() }
                        td {
                            a {
                                href = "wald?solutionID=${riskSolution.id}"
                                +waldResults[index].solutionDecision.russianName
                            }
                        }
                    }
                }
            }
        ))

        val solutionEfficients = riskSolutions.map { it.solutionEfficient }
        val maxSolutionEfficient = solutionEfficients.max() ?: 1.0
        val solutionCosts = riskSolutions.map { it.solutionCost }
        val maxSolutionCost = solutionCosts.max() ?: 1.0
        val removedRpns = riskSolutions.map { it.removedRpn }
        val maxRemovedRpn = removedRpns.max() ?: 1.0


        chartTag(
            labels = riskSolutions.indices.map { it.toString() },
            oneChartInfos = listOf(
                OneChartInfo.EMPTY.copy(
                    borderColor = chartColors[0],
                    label = "Эффективность решения",
                    data = solutionEfficients.map { it / maxSolutionEfficient }
                ),
                OneChartInfo.EMPTY.copy(
                    borderColor = chartColors[1],
                    label = "Стоимость решения",
                    data = solutionCosts.map { it / maxSolutionCost }
                ),
                OneChartInfo.EMPTY.copy(
                    borderColor = chartColors[2],
                    label = "Избалвяет от RPN",
                    data = removedRpns.map { it / maxRemovedRpn }
                )
            )
        )
    }
}

