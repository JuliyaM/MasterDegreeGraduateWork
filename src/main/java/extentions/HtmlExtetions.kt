package main.java.extentions

import main.java.AnalyzedProject
import main.java.RiskCause
import koma.extensions.map
import koma.matrix.Matrix
import kotlinx.css.Color
import kotlinx.html.*
import processors.MathJaxHelper
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

fun FlowContent.matrixTag(
    matrix: Matrix<*>, matrixName: String = "", matrixTag: String = ""
) {
    div {
        +MathJaxHelper.matrix(matrix, matrixName, matrixTag)
    }
}

fun FlowContent.arrayTag(array: List<*>, arrayName: String) {
    div {
        +MathJaxHelper.array(array, arrayName)
    }
}

fun FlowContent.latexExpTag(exp: String) {
    div {
        +MathJaxHelper.latexExp(exp)
    }
}

fun FlowContent.systemeTag(equaq: Iterable<String>) {
    div {
        +MathJaxHelper.systeme(equaq)
    }
}

val chartColors = listOf(
    "#E0BBE4",
    "#957DAD",
    "#D291BC",
    "#FEC8D8",
    "#FFDFD3",
    "#F2CDE3",
    "#FFCBA3",
    "#C8E0CF",
    "#FEE3EE"
).map { Color(it) }.shuffled()


enum class ChartType(val text: String) {
    LINE("line"),
    BAR("bar")
}

data class OneChartInfo(
    val backgroundColor: Color,
    val borderColor: Color,
    val label: String,
    val borderWidth: Int?,
    val data: Iterable<Number>
) {
    companion object {
        val EMPTY = OneChartInfo(
            backgroundColor = Color.transparent,
            borderColor = Color.transparent,
            label = "",
            borderWidth = null,
            data = listOf()
        )
    }
}

fun FlowContent.chartTag(
    labels: List<Any>,
    oneChartInfos: Iterable<OneChartInfo>,
    chartType: ChartType = ChartType.LINE,
    startFromZero: Boolean = false
) {
    val chardID = "myChart${Random.nextInt(100000)}"

    canvas {
        id = chardID
    }
    script {
        unsafe {
            raw("""
                new Chart(document.getElementById('$chardID').getContext('2d'), {
                  // The type of chart we want to create
                  type: '${chartType.text}',
            
                  // The data for our dataset
                  data: {
                    labels: [${labels.joinToString(", ") { "\"$it\"" }}],
                    datasets: [
                    ${
            oneChartInfos.joinToString(",\n") { oneChartInfo ->
                """
                    {
                      label: '${oneChartInfo.label}',
                      backgroundColor: '${oneChartInfo.backgroundColor}',
                      borderColor: '${oneChartInfo.borderColor}',
                      ${if (oneChartInfo.borderWidth != null) "borderWidth: ${oneChartInfo.borderWidth}," else ""}
                      data: [${oneChartInfo.data.joinToString(", ")}]
                    }
                """
            }
            }]
                  },
                  options: {
                    ${if (!startFromZero) ""
            else """
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                """.trimIndent()
            }
                  }
                }); 
                    """)
        }
    }
}

fun FlowContent.markovChainTag(matrix: Matrix<Double>, matrixName: String = Random.nextInt(10000).toString()) {
    val iframeID = "iframe$matrixName"
    val matrixRows = matrix
        .mapRowsIndexed { index, row ->
            val elementSum = row.elementSum()
            when {
                elementSum == 0.0 -> zerosWithOneIn(row.numCols(), index).transpose()
                elementSum != 1.0 -> row.map { it / elementSum }
                else -> row
            }
        }
        .mapRowsToList { row -> "[${row.toList().joinToString(",") { it.toString() }}]" }
        .joinToString(",")
    val matrixString = "[$matrixRows]"

    val matrixJson = URLEncoder.encode("""{"tm":$matrixString}""", StandardCharsets.UTF_8.toString())
    br { }
    iframe(classes = "playground") {
        id = iframeID
        attributes["scrolling"] = "no"
        style = "display: block; float: left;"
        width = "100%"
        height = (matrix.numRows() * 100).toString()
        src = "/static/markov_chain_visualization/index.html#$matrixJson"
    }
    p { +"Матрица $matrixName" }
    br { }
}

fun TR.project(project: AnalyzedProject) {
    val projectRpn = project.rpn
    val processRpns = project.processes
        .map {
            it.rpn
        }
    this.project(projectRpn, processRpns)
}

fun TR.project(
    processRpns: List<Double>
) {
    this.project(processRpns.sum(), processRpns)
}

fun TR.project(
    projectRpn: Double,
    processRpns: List<Double>
) {
    td {
        val rpnValue = projectRpn.round(3)

        if (rpnValue > 2.5) {
            b {
                +rpnValue.toString()
            }
        } else +rpnValue.toString()
    }
    processRpns.forEach {
        td {
            val rpnValue = it.round(3)
            if (rpnValue > 1.1) {
                b {
                    +rpnValue.toString()
                }
            } else +rpnValue.toString()
        }
    }
}

fun TR.riskCause(riskCause: RiskCause) {
    td {
        +riskCause.name
    }
    td {
        +riskCause.rpn.round(2).toString()
    }
    td {
        +(riskCause.weight).round(2).toString()
    }
    td {
        +riskCause.probability.round(2).toString()
    }
    td {
        +riskCause.detectability.round(2).toString()
    }
    td {
        +riskCause.significance.round(2).toString()
    }
    td {
        +riskCause.solutionCost.round(2).toString()
    }
}
