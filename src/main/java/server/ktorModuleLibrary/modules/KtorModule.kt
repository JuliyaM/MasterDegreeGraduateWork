package main.java.server.ktorModuleLibrary.modules

import com.sun.xml.internal.ws.api.Cancelable
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.server.engine.*
import io.ktor.util.pipeline.Pipeline
import kotlinx.coroutines.CoroutineScope
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.Delegates


abstract class KtorModule(
    private val gracePeriod: Long = 1,
    private val timeout: Long = 5,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS
) : Cancelable {


    var modulePort by Delegates.notNull<Int>()
    private val featureList: MutableList<KtorInfrastructureFeature> = mutableListOf()
    lateinit var engine: ApplicationEngine private set

    override fun cancel(p0: Boolean) {
        featureList.forEach {
            it.cancel(p0)
            engine.stop(gracePeriod = gracePeriod, timeout = timeout, timeUnit = timeUnit)
        }
        cancelModule(p0)
    }

    fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> initialize(
        coroutineScope: CoroutineScope,
        factory: ApplicationEngineFactory<TEngine, TConfiguration>,
        publicPort: Int,
        loggerName: String,
        watchPaths: List<String>,
        configure: TConfiguration.() -> Unit,
        envLogic: ApplicationEngineEnvironmentBuilder.() -> Unit,
        onException: (Throwable) -> Unit
    ): TEngine {
        modulePort = publicPort
        val environment = applicationEngineEnvironment {
            this.parentCoroutineContext = coroutineScope.coroutineContext
            this.log = LoggerFactory.getLogger(loggerName)
            this.watchPaths = watchPaths

            module(body = initializeModuleWithHandlingExceptions(coroutineScope, onException))
            envLogic(this)
        }

        val tEngine = embeddedServer(
            factory = factory,
            environment = environment,
            configure = configure
        )
        engine = tEngine
        return tEngine
    }

    fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> initialize(
        coroutineScope: CoroutineScope,
        factory: ApplicationEngineFactory<TEngine, TConfiguration>,
        port: Int,
        host: String,
        watchPaths: List<String>,
        configure: TConfiguration.() -> Unit,
        onException: (Throwable) -> Unit
    ): TEngine {
        modulePort = port
        val tEngine =
            coroutineScope.embeddedServer(
                factory = factory,
                port = port,
                host = host,
                watchPaths = watchPaths,
                parentCoroutineContext = EmptyCoroutineContext,
                configure = configure,
                module = initializeModuleWithHandlingExceptions(coroutineScope, onException)
            )
        engine = tEngine
        return tEngine
    }


    private fun initializeModuleWithHandlingExceptions(
        coroutineScope: CoroutineScope,
        onException: (Throwable) -> Unit
    ): Application.() -> Unit {
        return {
            initializeModule(coroutineScope)()
            install(StatusPages) {
                errorHandling()
                exception<Throwable> { cause ->
                    onException(cause)
                    call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
                }
            }
        }
    }

    internal abstract fun initializeModule(coroutineScope: CoroutineScope): Application.() -> Unit
    internal abstract fun errorHandling(): StatusPages.Configuration.() -> Unit
    internal abstract fun cancelModule(p0: Boolean): Application.() -> Unit

    internal suspend fun <P : Pipeline<*, ApplicationCall>> P.installFeature(
        ktorInfrastructureFeature: KtorInfrastructureFeature
    ) {
        featureList.add(ktorInfrastructureFeature)
        ktorInfrastructureFeature.initialize()
    }
}
