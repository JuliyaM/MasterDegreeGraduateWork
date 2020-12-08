package main.java.server.view

import main.java.SequentialAnalysisOfWaldResult
import kotlinx.html.*
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import main.java.server.view.fragments.OneSequentialAnalysisOfWaldFragment

class WaldPageView(
    private val waldResult: SequentialAnalysisOfWaldResult
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
                                    +"Анализ Вальда"
                                }
                                p(classes = "uk-text-lead") {
                                    +"Для решения ${waldResult.solution.id}"
                                }
                            }

                            include(OneSequentialAnalysisOfWaldFragment(waldResult))
                        }
                    }
                }
            }
        }
}
