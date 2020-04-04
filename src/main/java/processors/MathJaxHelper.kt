package processors

import koma.matrix.Matrix
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MathJaxHelper {
    fun array(array: List<*>, arrayName: String): String =
        StringBuilder()
            .apply {
                append("$$")
                append(arrayName)
                if (this.isNotBlank()) append(" = ")
                append("\\begin{pmatrix}")
                append(array.joinToString(" & ") { it.toString() })
                append("\\end{pmatrix}")
                append("$$")
            }
            .toString()

    fun matrix(matrix: Matrix<*>, matrixName: String = "", matrixTag: String = ""): String =
        StringBuilder()
            .apply {
                append("$$")
                append(matrixName)
                if (matrixName.isNotBlank()) append("=")
                append("\\begin{${matrixTag}matrix}")
                append(
                    matrix
                        .mapRowsToList { row ->
                            row.toList().joinToString(" & ") { it.toString() }
                        }
                        .joinToString("\\\\\n")
                )
                append("\\end{${matrixTag}matrix}")
                append("$$")
            }
            .toString()

    fun showMarkovMatrix(matrix: Matrix<*>, iframeID: String) =
        StringBuilder()
            .apply {
                val matrixString =
                    "[" + matrix
                        .mapRowsToList { row ->
                            "[" + row.toList().joinToString(",") { it.toString() } + "]"
                        }
                        .joinToString(",") + "]"
                append("<iframe id=\"$iframeID\"scrolling=\"no\" class=\"playground\" style=\"display: block; float: left;\" width=\"100%\" height=\"${matrix.numRows() * 150}\" src=\"")
                append("/Users/happydevil/Projects/ServerProj/uliyaMasterdegreeGraduateWork/showMatrix/markov_chain_visualization/index.html#")
                append(
                    URLEncoder.encode(
                        """{"tm":$matrixString}""",
                        StandardCharsets.UTF_8.toString()
                    )
                )
                append("\"></iframe>")
            }

    fun systeme(equaq: Array<out String>): String =
        StringBuilder()
            .apply {
                append("$$\n")
                append("\\begin{cases}\n")
                append(equaq.joinToString("\\\\\n"))
                append("\\end{cases}\n")
                append("$$")
            }
            .toString()

    fun latexExp(latexExp: String): String = "\$\$$latexExp\$\$"
}