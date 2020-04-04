package main.java.server.view.fragments

import extentions.round
import extentions.withIndexLatex
import koma.extensions.map
import koma.matrix.Matrix
import kotlinx.html.*
import kotlinx.html.p
import main.java.extentions.arrayTag
import main.java.extentions.markovChainTag
import main.java.extentions.matrixTag
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

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

