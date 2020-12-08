package main.java.server.view

import kotlinx.html.*
import main.java.extentions.*
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.controllers.CourierController
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class ModificationTestView(
    private val normalizedDelta: List<Double>,
    private val modifStep1Delta: List<Double>,
    private val modifStep2Delta: List<Double>
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

                            chartTag(
                                labels = normalizedDelta.indices.map { it.toString() },
                                oneChartInfos = listOf(
                                    OneChartInfo.EMPTY.copy(
                                        borderColor = chartColors[0],
                                        label = "Среднее отклонение эксп. с решением для Pnorm",
                                        data = normalizedDelta
                                    ),
                                    OneChartInfo.EMPTY.copy(
                                        borderColor = chartColors[1],
                                        label = "Среднее отклонение эксп. с решением для Pmodif1",
                                        data = modifStep1Delta
                                    ),
                                    OneChartInfo.EMPTY.copy(
                                        borderColor = chartColors[2],
                                        label = "Среднее отклонение эксп. с решением для Pmodif2",
                                        data = modifStep2Delta
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
}
