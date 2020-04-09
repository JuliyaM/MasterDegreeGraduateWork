package main.java.server.view.fragments

import RiskSolution
import kotlinx.html.*
import main.java.extentions.chartColors
import main.java.extentions.chartTag
import main.java.extentions.round
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import org.nield.kotlinstatistics.median

class RiskSolutionsFragment(
    private val riskSolutions: List<RiskSolution>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        include(UiTableFragment(
            tableName = "Возможные решения",
            tHead = {
                tr {
                    th { +"#" }
                    th { +"Процесс" }
                    th { +"Риск" }
                    th { +"Избавляет от RPN" }
                    th { +"Эффективность решения" }
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
                    }
                }
            },
            tFoot = {
                tr {
                    td {
                        colSpan = "3"
                        +"Среднее значение"
                    }
                    td { +riskSolutions.map { it.removedRpn }.average().round(2).toString() }
                    td { +riskSolutions.map { it.solutionEfficient }.average().round(2).toString() }
                }
                tr {
                    td {
                        colSpan = "3"
                        +"Медианное значение"
                    }
                    td { +riskSolutions.map { it.removedRpn }.median().round(2).toString() }
                    td { +riskSolutions.map { it.solutionEfficient }.median().round(2).toString() }
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
            colorLabelDatas = listOf(
                Triple(
                    chartColors[0],
                    "Эффективность решения",
                    solutionEfficients.map { it / maxSolutionEfficient }
                ),
                Triple(
                    chartColors[1],
                    "Стоимость решения",
                    solutionCosts.map { it / maxSolutionCost }
                ),
                Triple(
                    chartColors[2],
                    "Избалвяет от RPN",
                    removedRpns.map { it / maxRemovedRpn }
                )
            )
        )
    }
}

