package main.java.extentions

import AnalyzedProject
import RiskCause
import extentions.mapRowsIndexed
import extentions.round
import extentions.zerosWithOneIn
import koma.extensions.map
import koma.matrix.Matrix
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

fun FlowContent.chartTag(labels: List<Any>, colorLabelDatas: List<Triple<String, String, List<Double>>>) {
    val chardID = "myChart${Random.nextInt(100000)}"

    canvas {
        id = chardID
    }
    script {
        unsafe {
            raw("""
                new Chart(document.getElementById('$chardID').getContext('2d'), {
                  // The type of chart we want to create
                  type: 'line',
            
                  // The data for our dataset
                  data: {
                    labels: [${labels.joinToString(", ")}],
                    datasets: [
                    ${
            colorLabelDatas.joinToString(",\n") { colorLabelData ->
                """
                    {
                      label: '${colorLabelData.second}',
                      backgroundColor: 'transparent',
                      borderColor: '${colorLabelData.first}',
                      data: [${colorLabelData.third.joinToString(", ")}]
                    }
                """
            }
            }]
                  },
                  options: {}
                }); 
                    """)
        }
    }
}

fun FlowContent.markovChainTag(matrix: Matrix<Double>, matrixName: String = Random.nextInt(10000).toString()) {
    val iframeID = "iframe$matrixName"
    val matrixRows = matrix
        .mapRowsIndexed { index,row ->
            val elementSum = row.elementSum()
            when{
                elementSum == 0.0 -> zerosWithOneIn(row.numCols(),index).transpose()
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
        height = (matrix.numRows() * 150).toString()
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
    project(projectRpn, processRpns)
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
            if (rpnValue > 1.25 / processRpns.size) {
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
        +riskCause.significance.toString()
    }
}
