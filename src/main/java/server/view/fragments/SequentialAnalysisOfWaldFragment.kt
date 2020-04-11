package main.java.server.view.fragments

import main.java.SequentialAnalysisOfWaldResult
import kotlinx.html.*
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class SequentialAnalysisOfWaldFragment(
    private val waldResults: List<SequentialAnalysisOfWaldResult>
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        waldResults.forEach { include(OneSequentialAnalysisOfWaldFragment(it)) }
    }
}

