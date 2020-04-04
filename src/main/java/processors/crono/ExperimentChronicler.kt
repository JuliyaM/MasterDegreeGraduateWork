package processors.crono

import AnalyzedProject
import DeltaResult
import RiskCause
import extentions.*
import koma.extensions.forEachIndexed
import koma.extensions.map
import koma.matrix.Matrix
import kotlinx.html.*
import org.nield.kotlinstatistics.medianBy
import processors.MathJaxHelper
import processors.solvers.EndProjectExperimentSolver
import java.lang.StringBuilder
import kotlin.math.absoluteValue

class ExperimentChronicler {
    private val mathJaxHelper by lazy {
        MathJaxHelper()
    }

    private val outputInfoBuilder by lazy {
        OutputInfoBuilder(mathJaxHelper)
    }

    fun defaultAlgorithm(
        start: Matrix<Double>,
        labors: List<Int>,
        endRowIndex: Int?,
        projectMatrix: Matrix<Double>,
        projectDiffKolmogorovResult: List<Double>,
        avgExpSteps: Int,
        avgExperimentsCount: Int,
        avgResult: List<Double>
    ) {
        outputInfoBuilder.clear()
        outputInfoBuilder.text("Пользователь задал нам матрицу и трудоемкости:")
        val startMatrixRound = start.map { it.round(2) }
        outputInfoBuilder.matrix(startMatrixRound, "P".withIndexLatex("start"), "p")
        outputInfoBuilder.array(labors.toList(), "m")
        outputInfoBuilder.showMarkovMatrix(startMatrixRound)
        outputInfoBuilder.text("Из этого была построена матрица проекта:")

        printAlgorithmProjectBuild(startMatrixRound, labors, endRowIndex)

        outputInfoBuilder.matrix(projectMatrix.map { it.round(2) }, "P", "p")
        outputInfoBuilder.showMarkovMatrix(projectMatrix)
        outputInfoBuilder.text("Для анализа рисков в данном проекте нам необходимо оценить относительное количество времени проведенное в каждом процессе, что будет соответсвовать весам процессов.")
        outputInfoBuilder.text("Для этого мы можем использовать дифференциальные уравнения Колмогорова.")
        printADifferentalKolmogorovAlgorithm(projectMatrix, outputInfoBuilder)
        outputInfoBuilder.text("Решим полученную систему:")

        outputInfoBuilder.array(projectDiffKolmogorovResult.map { it.round(2).absoluteValue }, "q")

        outputInfoBuilder.text("Для проверки результатов проведем эксперемент: будем совершать переходы в системе $avgExpSteps раз и повторим такой эксперимент $avgExperimentsCount раз и усредним результаты.")
        outputInfoBuilder.text("В результате получим следующее  срнеднее количество времени в состояниях:")

        val avgSum = avgResult.sum()
        outputInfoBuilder.array(avgResult.map { (it / avgSum).round(2) }, "q".withIndexLatex("avg"))
    }


    fun specialAlgorithm(
        endProjectExpCount: Int,
        endProjectResultList: List<EndProjectExperimentSolver.ExperimentResult>,
        endProjectResultListAverage: EndProjectExperimentSolver.ExperimentResult,
        P1: Matrix<Double>,
        normalizedP1: Matrix<Double>,
        resultNormilizedP1: List<Double>,
        normilizedP1pow2: Matrix<Double>,
        multiKolmogorovResult: List<Pair<Matrix<Double>, List<Double>>>,
        endProjectToKolmogorovDelta: List<List<DeltaResult>>,
        averageResultDelta: List<DeltaResult>
    ) {
        outputInfoBuilder.text("Результаты совпали, однако из-за того, что в системе присуствует конечное состояние, то результаты говорят, что рано или поздно мы попадем в это состояние и не выйдем из него. Что не пригодно для использования при анализе рисков.")
        outputInfoBuilder.text("Для анализа нам необходимо относительное время проведенное в процессах до попадения в процесс, который завершает проект.")
        outputInfoBuilder.text("Тогда мы можем провести другой эксперимет, будем совершать переходы в системе до попадения в конечный процесс и вычислим относительное количество времени проведенное в процессах и среднее количество времени необходимое для завершения процесса.")
        outputInfoBuilder.text("При этом повторим эксперимент для различных начальных состояний и повторим такие эксперименты $endProjectExpCount раз усредняя результаты.")

        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"" }
                th { +"Среднее время проекта" }
                endProjectResultList.first().averageResult.forEachIndexed { index, _ ->
                    th { +"Состояние $index" }
                }
            }
            endProjectResultList.forEach {
                tr {
                    td { +"Начальное состояние ${it.startState}" }
                    td { +it.averageDayCount.round(2).toString() }
                    it.averageResult.forEach {
                        td { +it.round(2).toString() }
                    }
                }
            }

            tr {
                td {
                    b { +"Среднее значение" }
                }
                td {
                    b { +endProjectResultListAverage.averageDayCount.round(2).toString() }
                }

                endProjectResultListAverage.averageResult
                    .forEach {
                        td {
                            b { +it.round(2).toString() }
                        }
                    }
            }
        }

        outputInfoBuilder.text("Для получения аналогичного результата аналитическим способом из системы необходимо удалить конечные состояния и составить систему дифференциальных уравнений Колмогорова.")
        outputInfoBuilder.matrix(P1.map { it.round(2) }, "P1", "p")

        outputInfoBuilder.text("Данную матрицу необходимо нормировать:")
        outputInfoBuilder.matrix(normalizedP1.map { it.round(2) }, "P1".withIndexLatex("normalized"), "p")
        outputInfoBuilder.showMarkovMatrix(normalizedP1)
        printADifferentalKolmogorovAlgorithm(normalizedP1, outputInfoBuilder)

        outputInfoBuilder.text("Решим полученную систему:")
        outputInfoBuilder.array(resultNormilizedP1.map { it.round(2).absoluteValue }, "q")

        outputInfoBuilder.text("Данное решение соответствует усредненному значению из эксперимента, т.к. в нем не учитывается начальное состояние системы.")
        outputInfoBuilder.text("Для того чтобы это исправить и задать i-e начальное состояние необходимо совершить шаг системы (возвести матрицу в квадрат) и заменить i строку на i строку из начальной матрицы.")
        outputInfoBuilder.text("Матрица 2го шага (в 2й степени):")

        outputInfoBuilder.matrix(
            normilizedP1pow2.map { it.round(2) },
            "P1".withIndexLatex("normalized").powLatex("2"),
            "p"
        )

        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"" }
                th { +"Матрица" }
                endProjectResultList.first().averageResult.forEachIndexed { index, _ ->
                    th { +"Состояние $index" }
                }
            }

            multiKolmogorovResult.forEachIndexed { index, (Pi, resultI) ->
                tr {
                    td { +"Начальное состояние $index" }
                    td { +mathJaxHelper.matrix(matrix = Pi.map { it.round(2) }, matrixTag = "p") }
                    resultI
                        .map { it.absoluteValue.round(2).toString() }
                        .forEach {
                            td { +it }
                        }
                }
            }
        }

        outputInfoBuilder.text("Расчитаем отклонение экспериментальных значений и аналитических:")

        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"" }
                endProjectToKolmogorovDelta.first().forEachIndexed { index, _ ->
                    th { +"Состояние $index" }
                }
            }

            endProjectToKolmogorovDelta.forEachIndexed { index, deltaResult ->
                tr {
                    td { +"Начальное состояние $index" }
                    deltaResult
                        .forEach {
                            td { +"${it.absolute.round(2)}" }
                        }
                }
            }

            tr {
                td {
                    b { +"Среднее значение" }
                }

                averageResultDelta
                    .forEach {
                        td {
                            b { +it.absolute.round(2).toString() }
                        }
                    }
            }
        }
        outputInfoBuilder.text("Таким обраазом мы видим близость экпериментальных и аналитических значений, что говорит о возможности использования расчета системы дифференциальных уравнений для сиситемы без конечных состояний с измененными вероятностями i-строки для анализа весов прцоессов.")
    }

    fun choseOneWeightsAlgorithm(startProcessIndex: Int, laborsToWeights: Map<List<Int>, List<Double>>) {
        outputInfoBuilder.text("Произведем дальнейшие расчеты для начального процесса $startProcessIndex")
        outputInfoBuilder.text("Опираясь на работу Полячека, Кендела, Хинчена известно, что погрешность при переходе от детерминированных величин к стохастическим может увеличивать изначальную трудоемкость практически в 2 раза (только если рассматриваем среднее время ожидания заявки в очереди, в том случае если экспоненциальный закон).")
        outputInfoBuilder.text(
            "Таким образом вместо начальных трудоемкостей ${mathJaxHelper.latexExp("(m_1,m_2,...,m_n)")} можно использовать оценки ${mathJaxHelper.latexExp(
                "(m^{min}_1,m^{min}_2,...,m^{min}_n)"
            )} и все их комбинации, где ${mathJaxHelper.latexExp("m^{max}_i = 2m^{min}_i")}."
        )
        outputInfoBuilder.text("Получим следующее распределение весов:")

        val labors = laborsToWeights.keys.toList()
        val weights = laborsToWeights.values.toList()

//        outputInfoBuilder.array(labors.toList(), "m")


        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"#" }
                th { +"Трудоемкости" }
                weights.first().forEachIndexed { index, _ ->
                    th { +"Состояние $index" }
                }
            }

            weights
                .forEachIndexed { index, weight ->
                    tr {
                        td { +index.toString() }
                        td { +mathJaxHelper.array(labors[index].toList(), "m") }
//                    td { +labors[index].joinToString(",") }
                        weight
                            .forEach {
                                td { +"${it.round(2)}" }
                            }
                    }
                }
        }
    }

    val colors = listOf(
        "#E0BBE4",
        "#957DAD",
        "#D291BC",
        "#FEC8D8",
        "#FFDFD3",
        "#F2CDE3",
        "#FFCBA3",
        "#C8E0CF",
        "#FEE3EE"
    ).shuffled()

    fun projectsResult(projects: List<AnalyzedProject>) {
        outputInfoBuilder.text("У нас получилось ${projects.count()} возможных реализаци проекта.")
        outputInfoBuilder.text("Т.к. риски статичны относительно всех реализаций расчитаем их RPN отдельно.")

        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"Риск" }
                th { +"RPN" }
                th { +"Вес риска" }
                th { +"Причина появления риска" }
                th { +"RPN" }
                th { +"Вес причины" }
                th { +"Вероятность появления" }
                th { +"Вероятность обнаружения" }
                th { +"Значимость" }
            }
            projects.first().processes.forEach { process ->
                tr {
                    td {
                        colSpan = "9"
                        +process.name
                    }
                }
                process.risks.forEach { risk ->
                    val riskCauseCount = risk.riskCauses.size
                    tr {
                        td {
                            rowSpan = riskCauseCount.toString()
                            +risk.name
                        }
                        td {
                            rowSpan = riskCauseCount.toString()
                            +risk.rpn.round(2).toString()
                        }
                        td {
                            rowSpan = riskCauseCount.toString()
                            +risk.weight.round(2).toString()
                        }

                        risk.riskCauses.firstOrNull()?.let {
                            this.riskCause(it)
                        }
                    }
                    if (risk.riskCauses.size > 1) {
                        risk.riskCauses.subList(1, risk.riskCauses.size).forEach {
                            tr {
                                this.riskCause(it)
                            }
                        }
                    }
                }
            }
        }

        val processNames = projects.first().processes.map { it.name }


        outputInfoBuilder.table {
            attributes["border"] = "1"
            tr {
                th { +"#" }
                th { +"RPN проекта" }
                processNames.forEach {
                    th {
                        +"RPN процесса $it"
                    }
                }
            }
            projects.forEachIndexed { index, project ->
                tr {
                    td {
                        +index.toString()
                    }

                    this.project(project)
                }
            }

            projects.medianBy({ it }, { it.rpn }).keys.first().let {
                tr {
                    td {
                        +"Медиана"
                    }
                    this.project(it)
                }
            }


            val averageProcessesRpn = projects
                .sumByListDouble { project ->
                    project.processes.map { process -> process.rpn }
                }
                .map {
                    it / projects.size
                }

            val averageProjectRpn = projects.average {
                it.rpn
            }

            tr {
                td {
                    +"Среднее"
                }
                this.project(averageProjectRpn, averageProcessesRpn)
            }

            val minProcessesRpn = projects
                .minByListDouble { project ->
                    project.processes.map { process -> process.rpn }
                }

            val minProjectRpn = projects.map {it.rpn }.min() ?: 0.0

            tr {
                td {
                    +"Минимум"
                }
                this.project(minProjectRpn, minProcessesRpn)
            }

            val maxProcessesRpn = projects
                .maxByListDouble { project ->
                    project.processes.map { process -> process.rpn }
                }

            val maxProjectRpn = projects.map {it.rpn }.max() ?: 0.0

            tr {
                td {
                    +"Максимум"
                }
                this.project(maxProjectRpn, maxProcessesRpn)
            }


        }

        val transposeRpns = projects
            .map { project ->
                project.processes.map { process ->
                    process.rpn.round(3)
                }
            }
            .transpose()

        outputInfoBuilder.chart(
            labels = transposeRpns.first().indices.toList(),
            colorLabelDatas = transposeRpns.mapIndexed { index, rpns ->
                Triple(colors[index.rem(colors.size)], index.toString(), rpns)
            }
        )
    }

    fun printAlgorithmProjectBuild(
        start: Matrix<Double>,
        labors: List<Int>,
        endRowIndex: Int?
    ) {
        start.forEachIndexed { row: Int, col: Int, pi: Double ->
            val mi = labors.getOrNull(row) ?: 1

            val identifier = "($row,$col)"
            val isEndState = row == endRowIndex
            val condition = when {
                isEndState -> "конечное $LS состояние"
                row != col -> "не $LS диагональный $LS элемент"
                else -> "диагональный $LS элемент"
            }

            val p_row_col = "p".withIndexLatex(row, col)
            val m_row = "m".withIndexLatex(row)
            val formula = when {
                isEndState -> p_row_col
                row != col -> frac(p_row_col, m_row)
                else -> "1 - ${frac(1, m_row)}"
            }

            val valueOfLabor = if (!isEndState) "($m_row = $mi) $LS" else ""

            val result = when {
                isEndState -> pi
                row != col -> pi / mi
                else -> 1 - 1.0 / mi
            }.round(2)

            outputInfoBuilder.latexExp("$identifier -> $condition -> $p_row_col = $formula -> $valueOfLabor $LS $p_row_col = $result")
        }
    }

    fun print(): String = outputInfoBuilder.print()

    private fun TR.riskCause(riskCause: RiskCause) {
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

    fun printADifferentalKolmogorovAlgorithm(P: Matrix<Double>, outputInfoBuilder: OutputInfoBuilder) {
        val rowsCount = P.numRows()

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

        outputInfoBuilder.systeme(*qiToInboxToOutbox.map { (qi, inbox, outBox) -> "$qi' = $inbox - $outBox" }.toTypedArray())
        outputInfoBuilder.text("Так как предельные вероятности постоянны, то, заменяя в уравнениях Колмогорова их производные нулевыми значениями:")
        outputInfoBuilder.systeme(*qiToInboxToOutbox.map { (_, inbox, outBox) -> "$outBox = $inbox" }.toTypedArray())

        outputInfoBuilder.systeme(*qList.mapIndexed { qIndex, qi ->
            val curIndexRange = indexRange.filter { it != qIndex }


            val inbox = curIndexRange.joinToString("+") {
                P.to2DArray()[it][qIndex].round(2).toString() + "\\cdot " + "q".withIndexLatex(it)
            }

            val outBox = StringBuilder().apply {
                append("(")
                append(curIndexRange.joinToString("+") {
                    P.to2DArray()[qIndex][it].round(2).toString()
                })
                append(")")
                append("\\cdot ")
                append(qi)
            }
            "$outBox = $inbox"
        }.toTypedArray())
    }
}

private fun TR.project(project: AnalyzedProject) {
    val projectRpn = project.rpn
    val processRpns = project.processes
        .map {
            it.rpn
        }
    project(projectRpn, processRpns)
}

private fun TR.project(
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

