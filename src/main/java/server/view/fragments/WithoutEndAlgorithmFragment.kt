package main.java.server.view.fragments

import main.java.extentions.round
import main.java.extentions.withIndexLatex
import koma.matrix.Matrix
import kotlinx.html.FlowContent
import kotlinx.html.p
import main.java.extentions.arrayTag
import main.java.processors.solvers.WithoutEndProcessWeightSolveResult
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include

class WithoutEndAlgorithmFragment(
    private val projectMatrix: Matrix<Double>,
    private val withoutEndProcessWeightSolveResult: WithoutEndProcessWeightSolveResult
) : HtmlFragment {
    override fun getFragment(): FlowContent.() -> Unit = {
        p {
            +"""
        Для анализа рисков в данном проекте нам необходимо оценить относительное количество времени, 
        проведенное в каждом процессе,что будет соответствовать весам процессов.
        Для этого мы можем использовать дифференциальные уравнения Колмогорова."""
        }
        include(DifferentalKolmogorovAlgorithmFragment(projectMatrix))
        p { +"Решим полученную систему:" }

        with(withoutEndProcessWeightSolveResult) {
            arrayTag(projectDiffKolmogorovResult.round(2), "q")
            p {
                +"""Для проверки результатов проведем эксперемент: будем совершать переходы в системе $avgExpSteps раз 
                и повторим такой эксперимент $avgExperimentsCount раз и усредним результаты. В результате получим 
                следующее  срнеднее количество времени в состояниях:"""
            }
            arrayTag(avgResult.round(2), "q".withIndexLatex("avg"))
        }
    }

}

