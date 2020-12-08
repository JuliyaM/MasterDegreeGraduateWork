package main.java.server.view.fragments

import main.java.AnalyzedProject
import main.java.extentions.LS
import main.java.extentions.frac
import main.java.extentions.round
import main.java.extentions.withIndexLatex
import koma.extensions.forEachIndexed
import koma.matrix.Matrix
import kotlinx.html.FlowContent
import kotlinx.html.p
import main.java.extentions.latexExpTag
import main.java.extentions.markovChainTag
import main.java.extentions.matrixTag
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class AlgorithmProjectBuildFragment(
    private val project: AnalyzedProject,
    private val projectMatrix: Matrix<Double>
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        with(project.getSolveStartInfo()) {
            include(StartInfoFragment(startMatrix, startLabors))
            p { +"Из этого была построена матрица проекта:" }
            startMatrix.forEachIndexed { row: Int, col: Int, pi: Double ->
                val mi = startLabors.getOrNull(row) ?: 1

                val identifier = "($row,$col)"
                val isEndState = row == endProjectIndex
                val condition = when {
                    isEndState -> "конечное $LS состояние"
                    row != col -> "недиагональный $LS элемент"
                    else -> "диагональный $LS элемент"
                }

                val p_row_col = "p".withIndexLatex(row, col)
                val m_row = "m".withIndexLatex(row)
                val formula = when {
                    isEndState -> p_row_col
                    row != col -> frac(p_row_col, m_row)
                    else -> "1 - ${frac(1, m_row)}"
                }

                val valueOfLabor = if (!isEndState) "($m_row = $mi) " else ""

                //todo get result from constructor
                val result = when {
                    isEndState -> pi
                    row != col -> pi / mi
                    else -> 1 - 1.0 / mi
                }.round(2)

                latexExpTag("$identifier -> $condition -> $p_row_col = $formula -> $valueOfLabor $LS $p_row_col = $result")
            }

            matrixTag(projectMatrix.round(2), "P", "p")
            markovChainTag(projectMatrix)
        }
    }
}

