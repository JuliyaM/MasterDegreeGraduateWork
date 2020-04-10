package main.java.server.view.fragments

import SequentialAnalysisOfWaldResult
import WaldProps
import kotlinx.html.*
import main.java.extentions.chartColors
import main.java.extentions.chartTag
import main.java.extentions.round
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class SequentialAnalysisOfWaldFragment(
    private val waldResults: List<SequentialAnalysisOfWaldResult>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        waldResults.forEachIndexed { waldIndex, waldResult ->
            include(UiTableFragment(
                tableName = "Анализ вальда решения $waldIndex",
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
                colorLabelDatas = listOf(
                    Triple(
                        chartColors[0],
                        "Верхняя граница",
                        waldResult.riList
                    ),
                    Triple(
                        chartColors[1],
                        "Нижняя граница",
                        waldResult.aiList
                    ),
                    Triple(
                        chartColors[2],
                        "Эффективность Решения",
                        waldResult.cumulativeEfficient
                    )
                )
            )
        }
    }
}

