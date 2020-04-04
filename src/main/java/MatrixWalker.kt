import koma.matrix.Matrix
import org.nield.kotlinstatistics.WeightedDice

//TODO typed with process class?
class MatrixWalker(P: Matrix<Double>, private val startRow : Int = 0 ) {
    private val _statesWalkCount = IntArray(P.numRows()).toMutableList()
    private var _curRow: Int = startRow
    init {
        updateDayCountIn(_curRow)
    }

    private val dices = P.to2DArray().map {
        WeightedDice(
            *it.mapIndexed { index, d -> index to d }.toTypedArray()
        )
    }

    val statesWalkCount: List<Int>
        get() = _statesWalkCount

    val curRow: Int
        get() = _curRow

    fun walkNext(): Int {
        val newRow = dices[_curRow].roll()
        updateDayCountIn(newRow)
        _curRow = newRow
        return newRow
    }

    private fun updateDayCountIn(newRow: Int) = _statesWalkCount[newRow]++

    fun clear() {
        _curRow = startRow
        repeat(_statesWalkCount.size) {
            _statesWalkCount[it] = 0
        }
        updateDayCountIn(_curRow)
    }
}

