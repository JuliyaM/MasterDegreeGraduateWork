package ktorModuleLibrary.ktorHtmlExtentions

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveParameters
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.html.HTML
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

interface FormState {
    val error: String?
    val objectID: String?
}


enum class FormControllerType {
    Params,
    Multipart
}

abstract class FormController<S : FormState,User>(
    routingPath: String,
    minimalPermission: Int,
    private val OBJECT_ID_KEY: String,
    private val formControllerType: FormControllerType = FormControllerType.Params,
    private val getUserByCall: suspend (ApplicationCall) -> User?,
    private val userFilesDir: File
) :
    RoutingController(routingPath, minimalPermission) {

    val EMPTY_OBJ_ID = "Error url params"

    override fun createFormRouting(): Route.() -> Unit = {
        post(routingPath) {
            val user = getUserByCall.invoke(call)
            var objectID: String? = null
            val formBundle = when (formControllerType) {
                FormControllerType.Params -> {
                    val post = call.receiveParameters()
                    val rebuildMapOfParams = post.toMap().map { it.key to it.value.first() }.toMap().toMutableMap()
                    objectID = rebuildMapOfParams[OBJECT_ID_KEY]
                    rebuildMapOfParams.remove(OBJECT_ID_KEY)
                    FormBundle(rebuildMapOfParams)
                }
                FormControllerType.Multipart -> {
                    val multipart = call.receiveMultipart()
                    val formBundle = FormBundle()

                    multipart.forEachPart { part ->
                        val partName = part.name
                        if (partName != null) when (part) {
                            is PartData.FormItem -> {
                                if (partName != OBJECT_ID_KEY) formBundle[partName] = part.value
                                else objectID = part.value
                            }
                            is PartData.FileItem -> {
                                val originalFileName = part.originalFileName
                                if (originalFileName != null) {
                                    var file: File
                                    var i = 0
                                    do {
                                        file = File(
                                            userFilesDir,
                                            if (i > 0) "$originalFileName-$i" else originalFileName
                                        )
                                        i++
                                    } while (file.exists())

                                    file.writeBytes(part.streamProvider().readBytes())
                                    formBundle[partName] = file
                                }
                            }
                        }
                        part.dispose()
                    }
                    formBundle
                }
            }

            safeSaveForm(
                formBundle = formBundle, call = call, objectID = objectID, user = user
            )
        }
        get(routingPath) {
            val queryParameters = call.request.queryParameters

            val user = getUserByCall.invoke(call)

            val rebuildMapOfParams = queryParameters.toMap().map { it.key to it.value.first() }.toMap().toMutableMap()

            val objectID = rebuildMapOfParams[OBJECT_ID_KEY]
            rebuildMapOfParams.remove(OBJECT_ID_KEY)

            call.respondHtml(
                block = newForm(
                    state = parseMapToState(FormBundle(rebuildMapOfParams), objectID, user),
                    user = user
                )
            )
        }
    }

    abstract suspend fun parseMapToState(formBundle: FormBundle, objectID: String?, user: User?): S

    abstract suspend fun newForm(state: S, user: User?): (HTML.() -> Unit)
    abstract suspend fun errorForm(
        state: S,
        error: String,
        user: User?
    ): (HTML.() -> Unit)


    abstract suspend fun saveForm(
        call: ApplicationCall,
        state: S,
        objectID: String?,
        user: User?
    )


    private suspend fun safeSaveForm(
        formBundle: FormBundle,
        call: ApplicationCall,
        objectID: String?,
        user: User?
    ) {
        val state = parseMapToState(formBundle, objectID, user)
        val error = state.error
        return if (error != null) call.respondHtml(block = errorForm(state, error, user))
        else saveForm(call, state, objectID, user)
    }
}


class FormBundle(outerMap: Map<String, Any> = mutableMapOf()) {

    val innerMap: MutableMap<String, Any> = outerMap.toMutableMap()

    public operator fun <V> get(key: String): V? {
        innerMap["a"] = "b"
        return try {
            innerMap[key] as V?
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    public fun keys() = innerMap.keys

    public operator fun set(key: String, value: Any) {
        innerMap[key] = value
    }

    fun containsKey(key: String) = innerMap.containsKey(key)

}


suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}

