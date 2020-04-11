package main.java.server.view.fragments

import main.java.SequentialAnalysisOfWaldResult
import kotlinx.html.*
import main.java.extentions.OneChartInfo
import main.java.extentions.chartColors
import main.java.extentions.chartTag
import main.java.extentions.round
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class OneSequentialAnalysisOfWaldFragment(
    private val waldResult: SequentialAnalysisOfWaldResult
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        if (waldResult.resultStep != 0) {
            include(UiTableFragment(
                tableName = "Анализ вальда решения ${waldResult.solution.id}",
                tHead = {
                    tr {
                        th { +"Наблюдение" }
                        th { +"Кумулятивная эффективность решения" }
                        th { +"Верхняя граница" }
                        th { +"Нижняя граница" }
                    }
                },
                tBody = {
                    waldResult.aiList.indices.forEach {
                        tr {
                            td { +it.toString() }
                            td { +waldResult.cumulativeEfficient[it].round(2).toString() }
                            td { +waldResult.riList[it].round(2).toString() }
                            td { +waldResult.aiList[it].round(2).toString() }
                        }
                    }
                },
                tFoot = {
                    tr {
                        td {
                            colSpan = "4"
                            +"Решение ${waldResult.solutionDecision.russianName}"
                        }
                    }
                }
            ))

            chartTag(
                labels = waldResult.aiList.indices.map { it.toString() },
                oneChartInfos = listOf(
                    OneChartInfo.EMPTY.copy(
                        borderColor = chartColors[0],
                        label = "Верхняя граница",
                        data = waldResult.riList
                    ), OneChartInfo.EMPTY.copy(
                        borderColor = chartColors[1],
                        label = "Нижняя граница",
                        data = waldResult.aiList
                    ),
                    OneChartInfo.EMPTY.copy(
                        borderColor = chartColors[2],
                        label = "Эффективность Решения",
                        data = waldResult.cumulativeEfficient
                    )
                )
            )
        } else {
            p {
                +"Анализ вальда решения ${waldResult.solution.id} дает ответ на первом шаге - ${waldResult.solutionDecision.russianName}"
            }
        }
    }
}


