package main.java.server.view.fragments

import AnalyzedProject
import extentions.*
import kotlinx.html.*
import main.java.extentions.chartTag
import main.java.extentions.project
import main.java.extentions.riskCause
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import org.nield.kotlinstatistics.medianBy

class ResultProjectAnalyzeFragment(
    private val projectsVariants: List<AnalyzedProject>
) : HtmlFragment {

    private val colors = listOf(
        "#E0BBE4",
        "#957DAD",
        "#D291BC",
        "#FEC8D8",
        "#FFDFD3",
        "#F2CDE3",
        "#FFCBA3",
        "#C8E0CF",
        "#FEE3EE"
    ).shuffled()

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
                    th { +"Вес риска" }
                    th { +"Причина появления риска" }
                    th { +"RPN" }
                    th { +"Вес причины" }
                    th { +"Вероятность появления" }
                    th { +"Вероятность обнаружения" }
                    th { +"Значимость" }
                }
            },
            tBody = {
                projectsVariants.first().processes.forEach { process ->
                    tr {
                        td {
                            colSpan = "9"
                            +process.name
                        }
                    }
                    process.risks.forEach { risk ->
                        val riskCauseCount = risk.riskCauses.size
                        tr {
                            td {
                                rowSpan = riskCauseCount.toString()
                                +risk.name
                            }
                            td {
                                rowSpan = riskCauseCount.toString()
                                +risk.rpn.round(2).toString()
                            }
                            td {
                                rowSpan = riskCauseCount.toString()
                                +risk.weight.round(2).toString()
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
        val processNames = projectsVariants.first().processes.map { it.name }

        //todo refactor logic from view
        include(UiTableFragment(
            tableName = "RPN проекта",
            tHead = {
                tr {
                    th { +"#" }
                    th { +"RPN проекта" }
                    processNames.forEach {
                        th {
                            +"RPN процесса $it"
                        }
                    }
                }
            },
            tBody = {
                projectsVariants.forEachIndexed { index, project ->
                    tr {
                        td {
                            +index.toString()
                        }

                        this.project(project)
                    }
                }
                projectsVariants.medianBy({ it }, { it.rpn }).keys.first().let {
                    tr {
                        td {
                            +"Медиана"
                        }
                        this.project(it)
                    }
                }


                val averageProcessesRpn = projectsVariants
                    .sumByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }
                    .map {
                        it / projectsVariants.size
                    }

                val averageProjectRpn = projectsVariants.average {
                    it.rpn
                }

                tr {
                    td {
                        +"Среднее"
                    }
                    this.project(averageProjectRpn, averageProcessesRpn)
                }

                val minProcessesRpn = projectsVariants
                    .minByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }

                val minProjectRpn = projectsVariants.map { it.rpn }.min() ?: 0.0

                tr {
                    td {
                        +"Минимум"
                    }
                    this.project(minProjectRpn, minProcessesRpn)
                }

                val maxProcessesRpn = projectsVariants
                    .maxByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }

                val maxProjectRpn = projectsVariants.map { it.rpn }.max() ?: 0.0

                tr {
                    td {
                        +"Максимум"
                    }
                    this.project(maxProjectRpn, maxProcessesRpn)
                }
            }
        ))

        val transposeRpns = projectsVariants
            .map { project ->
                project.processes.map { process ->
                    process.rpn.round(3)
                }
            }
            .transpose()

        chartTag(
            labels = transposeRpns.first().indices.toList(),
            colorLabelDatas = transposeRpns.mapIndexed { index, rpns ->
                Triple(colors[index.rem(colors.size)], index.toString(), rpns)
            }
        )
    }
}

