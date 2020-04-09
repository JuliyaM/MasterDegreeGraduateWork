package main.java.server.view.fragments

import AnalyzedProject
import main.java.extentions.round
import kotlinx.html.*
import main.java.extentions.arrayTag
import main.java.extentions.transpose
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import org.nield.kotlinstatistics.median
import processors.MathJaxHelper

class ChoseOneWeightAlgorithmFragment(
    private val project: AnalyzedProject,
    private val projectsVariants: List<AnalyzedProject>
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
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
                    projectsVariants.first().processes.forEach { th { +"Вес процесса ${it.name}" } }
                }
            },
            tBody = {
                projectsVariants.forEachIndexed { index, project ->
                    tr {
                        val labors = project.processes.map { it.labor }
                        val weights = project.processes.map { it.weight }
                        td { +index.toString() }
                        td { arrayTag(labors, "m") }
                        weights.forEach {
                            td { +"${it.round(2)}" }
                        }
                    }
                }
            },
            tFoot = {
                val projectWeights = projectsVariants.map { analyzedProject ->
                    analyzedProject.processes.map { analyzedProcess -> analyzedProcess.weight }
                }
                    .transpose()
                tr {
                    td {
                        colSpan = "2"
                        +"Максимальное значение"
                    }
                    projectWeights.map { it.max() ?: 0.0 }.forEach {
                        td { +"${it.round(2)}" }
                    }
                }
                tr {
                    td {
                        colSpan = "2"
                        +"Среднее значение"
                    }
                    projectWeights.map { it.average() }.forEach {
                        td { +"${it.round(2)}" }
                    }
                }
            }
        ))
    }

}

