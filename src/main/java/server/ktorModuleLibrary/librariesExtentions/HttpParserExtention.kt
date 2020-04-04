package ktorModuleLibrary.librariesExtentions

import io.ktor.http.Parameters
import io.ktor.util.toMap


fun Parameters.httpParams(): String = toMap().httpParams()

fun Map<String, List<String>>.httpParams(): String = map { (key, value) ->
    val firstValue = value.firstOrNull()
    if (firstValue != null) "$key=$firstValue" else null
}.filterNotNull().joinToString("&")


fun String.base64ProcessToLink(): String {
    return this
        .replace("\n", "")
        .replace("+", "-")
        .replace("=", "~")
        .replace("/", "_")
}


fun String.base64unProcessToLink(): String {
    return this
        .replace("\n", "")
        .replace("-", "+")
        .replace("_", "/")
        .replace("~", "=")
}
