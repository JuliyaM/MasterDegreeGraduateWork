package main.java.server.view.fragments

import RiskCauseSolution
import kotlinx.html.*
import main.java.extentions.*
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import org.nield.kotlinstatistics.median

class RiskCauseSolutionsFragment(
    private val riskCauseSolutions: List<RiskCauseSolution>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        p {
            +"""Для выбора путей решений возьмем последнюю вариацию проекта с максимальным RPN. Расммотрим возможные решения
                каждой из причин появления рисков:
            """
        }
        include(UiTableFragment(
            tableName = "Возможные решения",
            tHead = {
                tr {
                    th { +"#" }
                    th { +"Процесс" }
                    th { +"Риск" }
                    th { +"Причина появления" }
                    th { +"Избавляет от RPN" }
                    th { +"Эффективность решения" }
                }
            },
            tBody = {
                riskCauseSolutions.forEachIndexed { index, riskCauseSolution ->
                    tr {
                        td { +index.toString() }
                        td { +riskCauseSolution.process.name }
                        td { +riskCauseSolution.risk.name }
                        td { +riskCauseSolution.riskCause.name }
                        td { +riskCauseSolution.removedRpn.round(3).toString() }
                        td { +riskCauseSolution.solutionEfficient.round(3).toString() }
                    }
                }
            },
            tFoot = {
                tr {
                    td {
                        colSpan = "4"
                        +"Среднее значение"
                    }
                    td { +riskCauseSolutions.map { it.removedRpn }.average().round(2).toString() }
                    td { +riskCauseSolutions.map { it.solutionEfficient }.average().round(2).toString() }
                }
                tr {
                    td {
                        colSpan = "4"
                        +"Медианное значение"
                    }
                    td { +riskCauseSolutions.map { it.removedRpn }.median().round(2).toString() }
                    td { +riskCauseSolutions.map { it.solutionEfficient }.median().round(2).toString() }
                }
            }
        ))

        chartTag(
            labels = riskCauseSolutions.indices.map { it.toString() },
            colorLabelDatas = listOf(
                Triple(
                    chartColors.random(),
                    "Решения",
                    riskCauseSolutions.map { it.solutionEfficient }
                )
            )
        )
    }
}

