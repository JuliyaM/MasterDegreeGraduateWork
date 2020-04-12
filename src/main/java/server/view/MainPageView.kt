package main.java.server.view

import kotlinx.html.*
import main.java.*
import main.java.extentions.round
import main.java.processors.ProjectAnalyzeResult
import main.java.server.MyMathBundle
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlView
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import main.java.server.view.fragments.*

class MainPageView(
    private val project: AnalyzedProject,
    private val projectAnalyzeResult: ProjectAnalyzeResult,
    private val solutionEfficientWaldResults: List<SequentialAnalysisOfWaldResult>,
    private val rpnWaldResults: List<SequentialAnalysisOfWaldResult>,
    private val clearedProjectBySolutionEfficientVariants: List<AnalyzedProject>,
    private val clearedProjectByRpnVariants: List<AnalyzedProject>
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
                                    +"Использования алгоритма анализа RPN проекта"
                                }
                            }
                            with(projectAnalyzeResult) {
                                include(AlgorithmProjectBuildFragment(project, projectMatrix))
                                include(
                                    WithoutEndAlgorithmFragment(
                                        projectMatrix = projectMatrix,
                                        withoutEndProcessWeightSolveResult = withoutEndProcessWeightSolveResult
                                    )
                                )

                                if (withEndProcessWeightSolveResult != null)
                                    include(
                                        ProjectWithEndFingWeightsAlgorithmFragment(
                                            project = project,
                                            withEndProcessWeightSolveResult = withEndProcessWeightSolveResult
                                        )
                                    )

                                include(
                                    ChoseOneWeightAlgorithmFragment(
                                        project = project,
                                        projectsVariants = projectsVariants
                                    )
                                )

                                include(ResultProjectAnalyzeFragment(projectsVariants))

                                p {
                                    +"""Для выбора путей решений возьмем среднее значение показателей решений для 
                                        |всех вариаций проекта и рассмотрим принятие решений при помощи 
                                        |последовательного алгортима Вальда основываясь на эффективности решений
                                    """.trimMargin()
                                }

                                include(RiskSolutionsFragment(solutionEfficientWaldResults))

                                p {
                                    +"""Для альтернативы рассмотрим принятие решений при помощи 
                                        |последовательного алгортима Вальда основываясь на RPN
                                    """.trimMargin()
                                }

                                include(RiskSolutionsFragment(rpnWaldResults))

                                p {
                                    +"""Если рассмотреть ситуацию, что рекомендации на основании эффективности решений 
|                                       будут выполненны в полном объеме проект получит следующую статистику:
                                    """.trimMargin()
                                }
                                p {
                                    +"""Затрты составят: ${solutionEfficientWaldResults.acceptCost.round(2)},
                                        |средняя эффективность решений : ${solutionEfficientWaldResults.efficient.round(2)}
                                    """.trimMargin()
                                }

                                include(RpnProjectFragment(clearedProjectBySolutionEfficientVariants))

                                p {
                                    +"""Если рассмотреть ситуацию, что рекомендации на основании RPN 
|                                       будут выполненны в полном объеме проект получит следующую статистику:
                                    """.trimMargin()
                                }
                                p {
                                    +"""Затрты составят: ${rpnWaldResults.acceptCost.round(2)},
                                        |средняя эффективность решений : ${rpnWaldResults.efficient.round(2)}
                                    """.trimMargin()
                                }

                                include(RpnProjectFragment(clearedProjectByRpnVariants))

                            }
                        }
                    }
                }
            }
        }
}
