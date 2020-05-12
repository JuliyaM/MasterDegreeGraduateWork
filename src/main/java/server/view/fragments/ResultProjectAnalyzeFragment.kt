package main.java.server.view.fragments

import main.java.AnalyzedProject
import kotlinx.html.*
import main.java.extentions.*
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class ResultProjectAnalyzeFragment(
    private val projectsVariants: List<AnalyzedProject>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        p {
            +"""У нас получилось ${projectsVariants.count()} возможных реализаци проекта. Т.к. риски статичны относительно всех 
                реализаций расчитаем их RPN отдельно.
            """
        }
        include(UiTableFragment(
            tableName = "RPN рисков",
            tHead = {
                tr {
                    th { +"Риск" }
                    th { +"RPN" }
                    th { +"Причина появления риска" }
                    th { +"RPN" }
                    th { +"Вероятность появления" }
                    th { +"Вероятность обнаружения" }
                    th { +"Значимость" }
                    th { +"Стоимость решения" }
                }
            },
            tBody = {
                projectsVariants.first().processes.forEach { process ->
                    tr {
                        td {
                            colSpan = "10"
                            +process.name
                        }
                    }
                    process.risks.forEach { risk ->
                        val riskCauseCount = risk.riskCauses.size
                        tr {
                            td {
                                rowSpan = riskCauseCount.toString()
                                +risk.riskTitle
                            }
                            td {
                                rowSpan = riskCauseCount.toString()
                                +risk.rpn.round(2).toString()
                            }

                            risk.riskCauses.firstOrNull()?.let {
                                this.riskCause(it)
                            }
                        }
                        if (risk.riskCauses.size > 1) {
                            risk.riskCauses.subList(1, risk.riskCauses.size).forEach {
                                tr {
                                    this.riskCause(it)
                                }
                            }
                        }
                    }
                }
            }
        ))
        include(RpnProjectFragment(projectsVariants))
    }
}

