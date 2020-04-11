package main.java.server.view.fragments

import main.java.AnalyzedProject
import main.java.extentions.powLatex
import main.java.extentions.round
import main.java.extentions.withIndexLatex
import kotlinx.html.*
import main.java.extentions.arrayTag
import main.java.extentions.markovChainTag
import main.java.extentions.matrixTag
import main.java.processors.solvers.WithEndProcessWeightSolveResult
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import kotlin.math.absoluteValue

class ProjectWithEndFingWeightsAlgorithmFragment(
    private val project: AnalyzedProject,
    private val withEndProcessWeightSolveResult: WithEndProcessWeightSolveResult
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        with(withEndProcessWeightSolveResult) {
            p {
                +"""Результаты совпали, однако из-за того, что в системе присуствует конечное состояние, 
                то результаты говорят, что рано или поздно мы попадем в это состояние и не выйдем из него. 
                Что не пригодно для использования при анализе рисков. Для анализа нам необходимо относительное время 
                проведенное в процессах до попадения в процесс, который завершает проект. Тогда мы можем провести другой 
                эксперимет, будем совершать переходы в системе до попадения в конечный процесс и вычислим относительное 
                количество времени проведенное в процессах и среднее количество времени необходимое для завершения процесса.
                При этом повторим эксперимент для различных начальных состояний и повторим такие эксперименты 
                $endProjectExpCount раз усредняя результаты."""
            }

            include(UiTableFragment(
                tableName = "Таблица состояний",
                tHead = {
                    tr {
                        th { +"" }
                        th { +"Среднее время проекта" }
                        endProjectResultList.first().averageResult.forEachIndexed { index, _ ->
                            th { +"Вес процесса ${project.processes[index].name}" }
                        }
                    }
                },
                tBody = {
                    endProjectResultList.forEach {
                        tr {
                            td { +"Начало в процессе ${project.processes[it.startIndex].name}" }
                            td { +it.averageDayCount.round(2).toString() }
                            it.averageResult.forEach {
                                td { +it.round(2).toString() }
                            }
                        }
                    }
                },
                tFoot = {
                    tr {
                        td { +"Среднее значение" }
                        td { +endProjectResultListAverage.averageDayCount.round(2).toString() }
                        endProjectResultListAverage.averageResult.forEach {
                            td { +it.round(2).toString() }
                        }
                    }
                }
            ))

            p {
                +"""Для получения аналогичного результата аналитическим способом из системы необходимо удалить конечные 
                состояния и составить систему дифференциальных уравнений Колмогорова."""
            }

            matrixTag(dropEndMatrix.round(2), "P1", "p")
            p {
                +"Данную матрицу необходимо нормировать:"
            }


            matrixTag(normalizedDropEndMatrix.round(2), "P1".withIndexLatex("normalized"), "p")
            markovChainTag(normalizedDropEndMatrix)
            p { +"Решим систему дифферциальных уравнений маркова:" }
            arrayTag(resultKolmogorovDropEnd.map { it.round(2).absoluteValue }, "q")

            p {
                +"""Данное решение соответствует усредненному значению из эксперимента, т.к. в нем не учитывается начальное 
                состояние системы."). Для того чтобы это исправить и задать i-e начальное состояние необходимо 
                совершить шаг системы (возвести матрицу в квадрат) и заменить i строку на i строку из начальной матрицы.
                Матрица 2го шага (в 2й степени):"""
            }

            matrixTag(dropEndMatrixPow2.round(2), "P1".withIndexLatex("normalized").powLatex("2"), "p")


            include(UiTableFragment(
                tableName = "Веса процессов",
                tHead = {
                    tr {
                        th { +"" }
                        th { +"Матрица" }
                        endProjectResultList.first().averageResult.forEachIndexed { index, _ ->
                            th { +"Вес процесса ${project.processes[index].name}" }
                        }
                    }
                },
                tBody = {
                    multiKolmogorovResult.forEachIndexed { index, (Pi, resultI) ->
                        tr {
                            td { +"Начало в процессе ${project.processes[index].name}" }
                            td { matrixTag(Pi.round(2), "", "p") }
                            resultI
                                .map { it.absoluteValue.round(2).toString() }
                                .forEach {
                                    td { +it }
                                }
                        }
                    }
                }
            ))


            p {
                +"""Расчитаем отклонение разницу в отклонениях экспериментальных значений и аналитических (при использовании
|                   модифицированных матриц и начальной):""".trimMargin()
            }

            include(UiTableFragment(
                tableName = "",
                tHead = {
                    tr {
                        th { +"" }
                        endProjectToKolmogorovDelta.first().forEachIndexed { index, _ ->
                            th { +"Вес процесса ${project.processes[index].name}" }
                        }
                    }
                },
                tBody = {
                    endProjectToKolmogorovDelta.forEachIndexed { index, deltaResult ->
                        tr {
                            td { +"Начало в процессе ${project.processes[index].name}" }
                            deltaResult
                                .forEach {
                                    td { +"${it.absolute.round(2)}" }  // (на ${((1 - it.relative) * 100).round(2)}% меньше)"
                                }
                        }
                    }
                }
            ))

            p {
                +"""Таким обраазом мы видим близость экпериментальных и аналитических значений, что говорит о возможности
                использования расчета системы дифференциальных уравнений для сиситемы без конечных состояний с 
                измененными вероятностями i-строки для анализа весов прцоессов."""
            }
        }
    }
}


