package main.java.server.view.fragments

import main.java.extentions.round
import main.java.extentions.withIndexLatex
import koma.matrix.Matrix
import kotlinx.html.*
import kotlinx.html.p
import main.java.extentions.arrayTag
import main.java.extentions.markovChainTag
import main.java.extentions.matrixTag
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment

class StartInfoFragment(
    private val startMatrix: Matrix<Double>,
    private val labors: List<Int>
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        p { +"Была задана следующая матрица и трудоемкости:" }
        matrixTag(startMatrix.round(2), "P".withIndexLatex("start"), "p")
        arrayTag(labors, "m")
        markovChainTag(startMatrix)
    }

}

