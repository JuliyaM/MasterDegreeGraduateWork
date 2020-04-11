package main.java.server.view.fragments

import main.java.AnalyzedProject
import kotlinx.html.*
import ktorModuleLibrary.librariesExtentions.median
import main.java.extentions.*
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class RpnProjectFragment(
    private val projectsVariants: List<AnalyzedProject>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
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
//                projectsVariants.forEachIndexed { index, project ->
//                    tr {
//                        td {
//                            +index.toString()
//                        }
//
//                        this.project(project)
//                    }
//                }

                val medianRpns = projectsVariants
                    .map { project -> project.processes.map { process -> process.rpn } }
                    .transpose()
                    .map { it.median() ?: 0.0 }

                tr {
                    td {
                        b {
                            +"Медиана"
                        }
                    }
                    this.project(medianRpns)
                }


                val averageProcessesRpn = projectsVariants
                    .sumByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }
                    .map {
                        it / projectsVariants.size
                    }

                tr {
                    td {
                        b {
                            +"Среднее"
                        }
                    }
                    this.project(averageProcessesRpn)
                }

                val minProcessesRpn = projectsVariants
                    .minByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }


                tr {
                    td {
                        b {
                            +"Минимум"
                        }
                    }
                    this.project(minProcessesRpn)
                }

                val maxProcessesRpn = projectsVariants
                    .maxByListDouble { project ->
                        project.processes.map { process -> process.rpn }
                    }

                tr {
                    td {
                        b {
                            +"Максимум"
                        }
                    }
                    this.project(maxProcessesRpn)
                }
            }
        ))
        val processVariants = projectsVariants.map { it.processes }
        val transposeProcesses = processVariants.transpose()

        chartTag(
            labels = transposeProcesses.first().indices.map { it.toString() },
            oneChartInfos = transposeProcesses.mapIndexed { index, processVariant ->
                OneChartInfo.EMPTY.copy(
                    borderColor = chartColors[index.rem(chartColors.size)],
                    label = processVariant.first().name,
                    data = processVariant.map { it.rpn }
                )
            }
        )
    }
}

