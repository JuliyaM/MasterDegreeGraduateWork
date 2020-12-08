package main.java.server.view

import kotlinx.html.*
import main.java.extentions.*
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.controllers.CourierController
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class CourierPageView(
    private val result: List<Pair<Int, CourierController.ExperimentResult>>
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
                                    +"Моделирование курьерской доставки"
                                }
                            }

                            val ratingKeys = result.first().second.ratingPoolResultsMap.map { it.first }
                            val defaultPoolResults = result.map { it.second.defaultPoolResult }
                            val ratingsExperiments =
                                result.map { it.second.ratingPoolResultsMap.map { it.second } }.transpose()


                            chartTag(
                                labels = result.map { it.first.toString() },
                                oneChartInfos = ratingsExperiments.mapIndexed { index, list ->
                                    OneChartInfo.EMPTY.copy(
                                        borderColor = chartColors[(index + 1).rem(chartColors.size)],
                                        label = ratingKeys[index],
                                        data = list.map { it.averageCourierRating }
                                    )
                                } + OneChartInfo.EMPTY.copy(
                                    borderColor = chartColors[0],
                                    label = "Случайное распределение",
                                    data = defaultPoolResults.map { it.averageCourierRating }
                                )
                            )

                            chartTag(
                                labels = result.map { it.first.toString() },
                                oneChartInfos = ratingsExperiments.mapIndexed { index, list ->
                                    OneChartInfo.EMPTY.copy(
                                        borderColor = chartColors[(index + 1).rem(chartColors.size)],
                                        label = ratingKeys[index],
                                        data = list.map { it.averageAwaitTime }
                                    )
                                } + OneChartInfo.EMPTY.copy(
                                    borderColor = chartColors[0],
                                    label = "Случайное распределение",
                                    data = defaultPoolResults.map { it.averageAwaitTime }
                                )
                            )

                        }
                    }
                }
            }
        }

    private fun DIV.printData(orderExperimentResult: CourierController.OrderExperimentResult) {
        p {
            +"Среднее время доставки : ${orderExperimentResult.averageAwaitTime.round(2)}"
        }

        p {
            +"Средний рейтинг курьера : ${orderExperimentResult.averageCourierRating.round(2)}"
        }

        p {
            +"Количество заказов : ${orderExperimentResult.orderCount}"
        }
    }
}
