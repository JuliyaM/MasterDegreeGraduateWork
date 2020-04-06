package main.java.server.view.fragments

import Solution
import kotlinx.html.*
import ktorModuleLibrary.librariesExtentions.median
import main.java.extentions.*
import main.java.processors.SolutionsAnalyzer
import main.java.server.LoggerHelper
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class SolutionsFragment(
    private val solutions: List<Solution>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        p {
            +"""Для выбора путей решений возьмем последнюю вариацию проекта с максимальным RPN. Расммотрим возможные решения
                каждого из рисков:
            """
        }
        include(UiTableFragment(
            tableName = "Возможные решения",
            tHead = {
                tr {
                    th { +"Процесс" }
                    th { +"Риск" }
                    th { +"Избавляет от RPN" }
                    th { +"Эффективность решения" }
                }
            },
            tBody = {
                solutions.forEach {
                    tr {
                        td { +it.process.name }
                        td { +it.risk.name }
                        td { +it.removedRpn.round(3).toString() }
                        td { +it.solutionEfficient.round(3).toString() }
                    }
                }
            },
            tFoot = {
                tr {
                    td {
                        colSpan = "2"
                        +"Среднее значение"
                    }
                    td { +solutions.map { it.removedRpn }.average().round(2).toString() }
                    td { +solutions.map { it.solutionEfficient }.average().round(2).toString() }
                }
                tr {
                    td {
                        colSpan = "2"
                        +"Медианное значение"
                    }
                    td { +solutions.map { it.removedRpn }.median()?.round(2).toString() }
                    td { +solutions.map { it.solutionEfficient }.median()?.round(2).toString() }
                }
            }
        ))

        chartTag(
            labels = solutions.indices.map { it.toString() },
            colorLabelDatas = listOf(
                Triple(
                    chartColors.random(),
                    "Решения",
                    solutions.map { it.solutionEfficient }
                )
            )
        )
    }
}

