package main.java.server.view

import kotlinx.css.TextAlign
import kotlinx.html.*
import main.java.extentions.*
import main.java.prediction.ProjectStructurePrediction
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import main.java.server.ktorModuleLibrary.ktorHtmlExtentions.innerStyle
import main.java.server.view.fragments.UiTableFragment

class PredictionPageView(
    private val projectStructurePrediction: ProjectStructurePrediction
) : HtmlView() {

    override fun getHTML(): HTML.() -> Unit =
        {
            head {
                meta {
                    httpEquiv = "Content-Type"
                    content = "text/html"
                    charset = "utf-8"
                }
                include(MyUiKitBundle)
                include(MyMathBundle)
            }


            body {
                div(classes = "tm-main uk-section uk-section-default") {
                    div(classes = "uk-container uk-container-medium tm-container-docs uk-position-relative") {
                        div(classes = "uk-width-1-1 uk-row-first") {
                            div(classes = "uk-text-center") {
                                h1 {
                                    +"Модель процессов,рисков и их причин"
                                }
                            }

                            val processData = projectStructurePrediction.riskModel.data
                            val risksData = processData.map { it.data }.flatten()
                            val riskCauseData = risksData.map { it.data }.flatten()
                            val riskNames = risksData.map { it.key }.distinct()

                            p { +"Процессов : ${processData.size}" }
                            p { +"Рисков : ${riskNames.size}" }
                            p { +"Причин появления рисков : ${riskCauseData.map { it.key }.distinct().size}" }


                            val processToCauseCount = processData.map { it.key to it.data.size }.toMap()
                            val riskToCauseCount = processData.map { processModel ->
                                processModel.key to riskNames.map { name ->
                                    processModel.data.find { it.key == name }?.data?.size ?: 0
                                }
                            }


                            chartTag(
                                chartType = ChartType.BAR,
                                labels = processToCauseCount.keys.map { it.russianName },
                                oneChartInfos = listOf(
                                    OneChartInfo.EMPTY.copy(
                                        backgroundColor = chartColors[0],
                                        borderColor = chartColors[0],
                                        label = "Количество рисков в процессе",
                                        data = processToCauseCount.values
                                    )
                                ),
                                startFromZero = true
                            )

                            chartTag(
                                chartType = ChartType.BAR,
                                labels = riskNames,
                                oneChartInfos = riskToCauseCount.mapIndexed { index, value ->
                                    OneChartInfo.EMPTY.copy(
                                        backgroundColor = chartColors[index.rem(chartColors.size)],
                                        borderColor = chartColors[index.rem(chartColors.size)],
                                        label = value.first.russianName,
                                        data = value.second
                                    )
                                },
                                startFromZero = true
                            )

                            include(UiTableFragment(
                                tableName = "",
                                tHead = {
                                    tr {
                                        th { +"Риск" }
                                        th { +"Причина появления" }
                                    }
                                },
                                tBody = {
                                    processData.forEach { process ->
                                        tr {
                                            td {
                                                innerStyle {
                                                    this.textAlign = TextAlign.center
                                                }
                                                colSpan = "2"
                                                b { +process.key.russianName }
                                            }
                                        }

                                        process.data.forEach { risk ->
                                            tr {
                                                td {
                                                    rowSpan = risk.data.size.toString()
                                                    +risk.key
                                                }
                                                td { +risk.data.first().key }
                                            }
                                            risk.data.drop(1).forEach { cause ->
                                                tr {
                                                    td { +cause.key }
                                                }
                                            }
                                        }

                                    }
                                }
                            ))
                        }
                    }
                }
            }
        }
}
