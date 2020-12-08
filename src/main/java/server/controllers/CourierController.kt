package main.java.server.controllers

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import koma.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.extentions.average
import main.java.server.LoggerHelper
import main.java.server.view.CourierPageView
import org.nield.kotlinstatistics.WeightedCoin
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class CourierController(
    routingPath: String,
    minimalPermission: Int
) : RoutingController(routingPath, minimalPermission) {

    data class OrderExperimentResult(
        val averageAwaitTime: Double,
        val averageCourierRating: Double,
        val orderCount: Int
    )

    data class Courier(
        val rating: Double,
        var isBusyCycles: Int,
        val takeCoin: WeightedCoin
    ) {
        companion object {
            fun create() = Courier(Random.nextDouble(1.0, 5.0), 0, WeightedCoin(0.33))
        }
    }

    data class Order(
        var awaitTime: Int,
        var courier: Courier?
    ) {
        companion object {
            fun create() = Order(0, null)
        }
    }

    data class ExperimentResult(
        val defaultPoolResult: OrderExperimentResult,
        val ratingPoolResultsMap: List<Pair<String, OrderExperimentResult>>
    )

    private val computationScope = CoroutineScope(Dispatchers.Default)

    override fun createFormRouting(): Route.() -> Unit {
        return {
            get(routingPath) {
                val defaultCourierPoll = (0..5000).map { Courier.create() }
                val result = with(computationScope) {
                    (5..75 step 5).map { orderFlowSize ->
                        orderFlowSize to async {
                            val defaultPoolResult = async {
                                defaultCourierPollExperiment(
                                    defaultCourierPoll = defaultCourierPoll.map { it.copy() },
                                    daysCount = 2,
                                    orderBusyTime = 100,
                                    generateOrders = { orderFlowSize }
                                )
                            }

                            val ratingExperimentList =
                                listOf(
                                    10 to 1,
                                    8 to 1,
                                    6 to 1,
                                    2 to 1
                                ).map { (minutes, step) ->
                                    async {
                                        "Рейтинговое($minutes,$step)" to ratingCourierPollExperiment(
                                            allCouriers = defaultCourierPoll.map { it.copy() },
                                            daysCount = 2.0,
                                            orderBusyTime = 100,
                                            minutesToIncrease = minutes,
                                            ratingPoolCreator = { linearRatingPool(it, step) },
                                            generateOrders = { orderFlowSize }
                                        )
                                    }
                                } 

                            ExperimentResult(defaultPoolResult.await(), ratingExperimentList.map { it.await() })
                        }
                    }.map {
                        it.first to it.second.await()
                    }
                }

                call.respondHtml(
                    block = CourierPageView(
                        result = result
                    ).getHTML()
                )
            }
        }
    }


    inline fun defaultCourierPollExperiment(
        defaultCourierPoll: List<Courier>,
        daysCount: Int,
        orderBusyTime: Int,
        generateOrders: (Int) -> Int
    ): OrderExperimentResult {
        val orderPoll = mutableListOf<Order>()

        val maxSize = 60 * 24 * daysCount
        var lastPercent = 0
        repeat(maxSize) {
            val percent = (it.toDouble() * 100 / maxSize).roundToInt()
            if (percent > lastPercent) {
                LoggerHelper.log("Now $percent%")
                lastPercent = percent
            }


            val times = generateOrders(it)
            repeat(times) { orderPoll.add(Order.create()) }
            defaultCourierPoll.forEach { courier -> if (courier.isBusyCycles > 0) --courier.isBusyCycles }
            val nonBussyCouriers = defaultCourierPoll.shuffled()
                .filter { courier -> courier.isBusyCycles == 0 && courier.takeCoin.flip() }

            orderPoll
                .filter { it.courier == null }
                .sortedByDescending { it.awaitTime }
                .forEach { order ->
                    order.awaitTime++
                    nonBussyCouriers.firstOrNull { courier -> courier.isBusyCycles == 0 }?.let { courier ->
                        courier.isBusyCycles = orderBusyTime
                        order.courier = courier
                    }
                }
        }

        return getResults(orderPoll, orderBusyTime.toDouble() / 2)
    }

    inline fun ratingCourierPollExperiment(
        allCouriers: List<Courier>,
        daysCount: Double,
        orderBusyTime: Int,
        minutesToIncrease: Int,
        ratingPoolCreator: (List<Courier>) -> List<List<Courier>>,
        generateOrders: (Int) -> Int
    ): OrderExperimentResult {

        val ratingPoll = ratingPoolCreator(allCouriers)

        val orderPoll = mutableListOf<Order>()

        repeat((60 * 24 * daysCount).roundToInt()) {
            val times = generateOrders(it)
            repeat(times) { orderPoll.add(Order.create()) }

            allCouriers.forEach { courier -> if (courier.isBusyCycles > 0) --courier.isBusyCycles }

            val nonBusyCouriers = ratingPoll
                .map { courierList -> courierList.filter { courier -> courier.isBusyCycles == 0 } }
                .toMutableList()
                .apply { removeIf { courierList -> courierList.isEmpty() } }
                .map { courierList -> courierList.filter { courier -> courier.takeCoin.flip() } }

            orderPoll
                .filter { order -> order.courier == null }
                .sortedByDescending { order -> order.awaitTime }
                .forEach { order ->
                    order.awaitTime++

                    val courierPoll =
                        nonBusyCouriers
                            .take(1 + order.awaitTime / minutesToIncrease).flatten()
                            .filter { courier -> courier.isBusyCycles == 0 }

                    courierPoll.firstOrNull()?.let { courier ->
                        courier.isBusyCycles = orderBusyTime
                        order.courier = courier
                    }
                }
        }

        return getResults(orderPoll, orderBusyTime.toDouble() / 2)
    }

    fun linearRatingPool(
        allCouriers: List<Courier>,
        ratingStep: Int
    ): List<List<Courier>> {
        val plusValue = 50 % ratingStep

        val sortedByDescending = allCouriers
            .groupBy {
                val intRating = (it.rating * 10).toInt()
                (intRating - ((intRating + plusValue) % ratingStep)).toDouble() / 10
            }
            .toList()
            .sortedByDescending { it.first }
        return sortedByDescending.map { it.second.shuffled() }
    }

    fun quadRatingPoolCreator(
        allCouriers: List<Courier>
    ): List<List<Courier>> {
        val steps = (1..50)
            .map { max(50 - it.pow(2).roundToInt(), 10).toDouble() / 10 }
            .distinct()

        val sortedByDescending = allCouriers
            .groupBy { courier ->
                steps.first { courier.rating > it }
            }
            .toList()
            .sortedByDescending { it.first }
        return sortedByDescending.map { it.second.shuffled() }
    }

    fun getResults(orderPoll: MutableList<Order>, deliverTime: Double): OrderExperimentResult {
        val averageAwaitTime = orderPoll.average { it.awaitTime.toDouble() }
        val averageCourierRating = orderPoll.mapNotNull { it.courier?.rating }.average()

        return OrderExperimentResult(
            averageAwaitTime + deliverTime,
            averageCourierRating,
            orderPoll.count()
        )
    }

}