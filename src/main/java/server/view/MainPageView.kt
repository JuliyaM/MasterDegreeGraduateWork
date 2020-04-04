package main.java.server.view

import AnalyzedProject
import kotlinx.html.*
import main.java.ProjectAnalyzeResult
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import main.java.server.view.fragments.*

class MainPageView(
    private val project: AnalyzedProject,
    private val projectAnalyzeResult: ProjectAnalyzeResult
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
                                    +"Документация"
                                }
                                p(classes = "uk-text-lead") {
                                    + "Использования алгоритма анализа RPN проекта"
                                }
                            }
                            with(projectAnalyzeResult) {
                                include(AlgorithmProjectBuildFragment(project, projectMatrix))
                                include(
                                    WithoutEndAlgorithmFragment(
                                        projectMatrix = projectMatrix,
                                        withoutEndProjectProcessesWeights = withoutEndProjectProcessesWeights
                                    )
                                )

                                if (projectWithEndProcessWeights != null)
                                    include(ProjectWithEndFingWeightsAlgorithmFragment(projectWithEndProcessWeights))

                                include(ChoseOneWeightAlgorithmFragment(project, laborsToWeights))

                                include(ResultProjectAnalyzeFragment(projectsVariants))
                            }
                        }
                    }
                }
            }
        }
}
