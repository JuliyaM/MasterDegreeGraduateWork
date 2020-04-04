package processors.crono

import koma.matrix.Matrix
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import processors.MathJaxHelper
import kotlin.random.Random


class OutputInfoBuilder(private val mathJaxHelper: MathJaxHelper) {
    private val innerStringBuilder = StringBuilder()
    fun clear() = innerStringBuilder.clear()
    fun print() = innerStringBuilder.toString()

    fun lineBreak() {
        innerStringBuilder
            .appendHTML().br()
            .append("\n")
    }

    fun matrix(matrix: Matrix<*>, matrixName: String, matrixTag: String = "") {
        innerStringBuilder
            .append(mathJaxHelper.matrix(matrix, matrixName, matrixTag))
            .append("\n")
    }

    fun latexExp(latexExp: String) {
        innerStringBuilder
            .append(mathJaxHelper.latexExp(latexExp))
            .append("\n")
    }


    fun text(text: String) {
        innerStringBuilder
            .appendHTML().p { +text }
            .append("\n")
    }

    fun array(array: List<*>, arrayName: String) =
        innerStringBuilder
            .append(mathJaxHelper.array(array, arrayName))
            .append("\n")

    fun showMarkovMatrix(matrix: Matrix<*>) {
        val iframeID = "iframe${Random.nextInt(100000)}"
        lineBreak()
        text("")
        innerStringBuilder
            .append(mathJaxHelper.showMarkovMatrix(matrix, iframeID))
            .append("\n")
        text("")
        lineBreak()
    }

    fun table(init: TABLE.() -> Unit) {
        innerStringBuilder
            .appendHTML()
            .table(block = init)
            .append("\n")
    }

    inline fun build(f: OutputInfoBuilder.() -> Unit): String {
        clear()
        f()
        return print()
    }

    fun systeme(vararg equaq: String) {
        innerStringBuilder
            .append(mathJaxHelper.systeme(equaq))
            .append("\n")
    }

    fun chart(labels: List<Any>, colorLabelDatas: List<Triple<String, String, List<Double>>>) {
        val chardID = "myChart${Random.nextInt(100000)}"

        innerStringBuilder
            .appendHTML()
            .let {
                it.canvas {
                    id = chardID
                }
                it.script {
                    +"""
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
                                                            """.trimIndent()
                    }
                    }]
                          },
                          options: {}
                        }); 
                    """.trimIndent()
                }
            }
            .append("\n")
    }
}