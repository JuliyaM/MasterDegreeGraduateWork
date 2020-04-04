package main.java.server.view.fragments

import AnalyzedProject
import extentions.round
import kotlinx.html.*
import main.java.extentions.arrayTag
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import processors.MathJaxHelper

class ChoseOneWeightAlgorithmFragment(
    private val project: AnalyzedProject,
    private val laborsToWeights: Map<List<Int>, List<Double>>
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        val labors = laborsToWeights.keys.toList()
        val weights = laborsToWeights.values.toList()

        p {
            +"""
                Произведем дальнейшие расчеты для начального процесса ${project.startProcessIndex}. Опираясь на работу Полячека, 
                Кендела, Хинчена известно, что погрешность при переходе от детерминированных величин к стохастическим 
                может увеличивать изначальную трудоемкость практически в 2 раза (только если рассматриваем среднее время 
                ожидания заявки в очереди, в том случае если экспоненциальный закон). 
            """
        }
        p {
            +"""
                Таким образом вместо начальных трудоемкостей ${MathJaxHelper.latexExp("(m_1,m_2,...,m_n)")}
                можно использовать оценки ${MathJaxHelper.latexExp("(m^{min}_1,m^{min}_2,...,m^{min}_n)")} 
                и все их комбинации, где ${MathJaxHelper.latexExp("m^{max}_i = 2m^{min}_i")}.
            """
        }
        p {
            +"При помощи данных комбинаций получим следующее распределение весов:"
        }

        include(UiTableFragment(
            tableName = "Распределение весов",
            tHead = {
                tr {
                    th { +"#" }
                    th { +"Трудоемкости" }
                    weights.first().forEachIndexed { index, _ ->
                        th { +"Состояние $index" }
                    }
                }
            },
            tBody = {
                weights.forEachIndexed { index, weight ->
                    tr {
                        td { +index.toString() }
                        td { arrayTag(labors[index].toList(), "m") }
                        weight.forEach {
                            td { +"${it.round(2)}" }
                        }
                    }
                }
            }
        ))
    }

}

