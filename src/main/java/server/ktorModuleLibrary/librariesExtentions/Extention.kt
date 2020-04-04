package ktorModuleLibrary.librariesExtentions

import com.sun.jndi.toolkit.url.Uri
import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.http.Parameters
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveParameters
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun <E> List<E>.removeLast(): List<E> {
    return this.subList(0, this.size - 1)
}

fun File.getFileInto(fileName: String): File {
    return File("${this.absolutePath}${File.separator}$fileName")
}

val headerKeys = listOf(
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
)

fun ApplicationRequest.realIP(): String {
//    println("local host : ${this.local.remoteHost}")
//    println("origin host : ${this.origin.remoteHost}")
//
//    runBlocking {
//        val receiveChannel = this@realIP.receiveChannel()
//
//        var readSize: Int
//        while (receiveChannel.availableForRead > 0) {
//            readSize = min(receiveChannel.availableForRead, 12)
//            println("read : ${receiveChannel.readUTF8Line(readSize)}")
//        }
//    }
    return headerKeys.map {
        val s = headers[it]
        s
    }.firstOrNull {
        it != null && it.isNotEmpty() && it != "unknown"
    } ?: origin.remoteHost
}


public inline
fun <T, R : Comparable<R>> Iterable<T>.getMaximumListBy(crossinline selector: (T) -> R?): List<T> {
    val sortedList = this.sortedWith(compareBy(selector))
    return sortedList.filter { selector(it) == selector(sortedList.last()) }
}

fun Uri.params(): Map<String, String>? = this.toString().linkParams()

fun String.linkPath() = this.split("://").first()
fun String.linkHost() = this.split("://").last().split("?").first()

fun String.linkParams(): Map<String, String> {
    val splitHost = this.split("?")
    if (splitHost.size < 2) return mapOf()

    var params = splitHost[1]
    splitHost.subList(1, splitHost.size).forEach {
        params +=
            if (params.isNotEmpty()) "&$it"
            else it
    }

    return try {
        params.split("&").map {
            val split = it.split("=")
            split[0] to split[1]
        }.toMap()
    } catch (e: Exception) {
        mapOf()
    }
}

public fun Long.millsToString(dateTimeFormat: String): String = SimpleDateFormat(dateTimeFormat).format(Date(this))
public fun stringToMills(date: String, time: String, dateTimeFormat: String): Long =
    SimpleDateFormat(dateTimeFormat).parse("$date $time").time


suspend fun ApplicationCall.safeReceiveParameters(): Parameters? {
    return try {
        this.receiveParameters()
    } catch (e: Exception) {
        null
    }
}