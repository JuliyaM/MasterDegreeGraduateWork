package main.java.server.view

import koma.sqrt
import kotlinx.html.*
import main.java.extentions.*
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import kotlin.math.roundToInt

class DispersionPageView(
    private val dispersion: Double,
    private val xi: List<Double>,
    private val processCount: Int
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
                                    +"Анализ дисперсии"
                                }
                                p(classes = "uk-text-lead") {
                                    +"Эффективности решений по минимизации рисков процессов"
                                }
                            }

                            p {
                                +"""
                                    Для вычисления дисперсии был проведен эксперимент с генерацией $processCount процессов
                                    в результате которого была получена выборка из ${xi.size} возможных решений по минимизации рисков.
                                    Далее был проведен анализ эффективности решений данной выборки
                                """.trimIndent()
                            }

                            p {
                                +"""Генерация экспериментов имеет равномерное распределение, следовательно вероятность результатов 
                                  равномерная $$ p_i = 1 / ${xi.size} $$. Расчитаем дисперсию: """.trimMargin()
                            }

                            latexExpTag(
                                """D(X) = \sum_{i=1}^{${xi.size}} x^2_i \cdot p_i + (\sum_{i=1}^{${xi.size}} x_i \cdot p_i)^2 = ${dispersion.round(
                                    2
                                )}"""
                            )
                            latexExpTag("""\sigma = \sqrt{D(X)} = ${sqrt(dispersion).round(2)}""")

                            val roundedValues = xi.map { it.round(2) }.sorted()

                            val countOfUniqueMap = roundedValues.groupBy { it }.mapValues { it.value.count() }

                            val lowerBorder = roundedValues[(roundedValues.size * 0.25).roundToInt()]
                            val upperBorder = roundedValues[(roundedValues.size * 0.85).roundToInt()]

                            p {
                                +"Треть элементов меньше $lowerBorder, треть элементов больше $upperBorder"
                            }

                            val dropBorderMap = countOfUniqueMap.mapValues {
                                if (it.key != lowerBorder && it.key != upperBorder) it.value
                                else 0
                            }

                            val borderMap = countOfUniqueMap.mapValues {
                                if (it.key != lowerBorder && it.key != upperBorder) 0
                                else it.value
                            }



                            chartTag(
                                chartType = ChartType.BAR,
                                labels = dropBorderMap.keys.map { it.toString() },
                                oneChartInfos = listOf(
                                    OneChartInfo.EMPTY.copy(
                                        backgroundColor = chartColors[0],
                                        borderColor = chartColors[0],
                                        label = "Колчиество повторений",
                                        data = dropBorderMap.values
                                    ),
                                    OneChartInfo.EMPTY.copy(
                                        backgroundColor = chartColors[1],
                                        borderColor = chartColors[1],
                                        label = "Границы",
                                        data = borderMap.values
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
}
