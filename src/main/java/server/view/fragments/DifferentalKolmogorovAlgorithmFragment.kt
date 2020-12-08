package main.java.server.view.fragments

import main.java.extentions.round
import main.java.extentions.withIndexLatex
import koma.matrix.Matrix
import kotlinx.html.FlowContent
import kotlinx.html.p
import main.java.extentions.systemeTag
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment

class DifferentalKolmogorovAlgorithmFragment(
    private val matrix: Matrix<Double>
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        val rowsCount = matrix.numRows()

        val indexRange = 0 until rowsCount
        val qList = indexRange.map { "q".withIndexLatex(it) }

        val qiToInboxToOutbox = qList.mapIndexed { qIndex, qi ->
            val curIndexRange = indexRange.filter { it != qIndex }

            val inbox = curIndexRange.joinToString("+") {
                "p".withIndexLatex(it, qIndex) + "\\cdot " + "q".withIndexLatex(it)
            }

            val outBox = StringBuilder().apply {
                append("(")
                append(curIndexRange.joinToString("+") {
                    "p".withIndexLatex(qIndex, it)
                })
                append(")")
                append("\\cdot ")
                append(qi)
            }
            Triple(qi, inbox, outBox)
        }


        systemeTag(qiToInboxToOutbox.map { (qi, inbox, outBox) -> "$qi' = $inbox - $outBox" })
        p {
            +"""Так как предельные вероятности постоянны, то, заменяя в уравнениях Колмогорова
|               их производные нулевыми значениями, получим:""".trimMargin()
        }
        systemeTag(qiToInboxToOutbox.map { (_, inbox, outBox) -> "$outBox = $inbox" })


        systemeTag(qList.mapIndexed { qIndex, qi ->
            val curIndexRange = indexRange.filter { it != qIndex }


            val inbox = curIndexRange.joinToString("+") {
                matrix.to2DArray()[it][qIndex].round(2).toString() + "\\cdot " + "q".withIndexLatex(it)
            }

            val outBox = StringBuilder().apply {
                append("(")
                append(curIndexRange.joinToString("+") {
                    matrix.to2DArray()[qIndex][it].round(2).toString()
                })
                append(")")
                append("\\cdot ")
                append(qi)
            }
            "$outBox = $inbox"
        })
    }

}

