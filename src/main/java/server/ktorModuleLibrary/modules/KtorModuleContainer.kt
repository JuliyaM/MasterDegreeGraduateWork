package main.java.server.ktorModuleLibrary.modules

import com.sun.xml.internal.ws.api.Cancelable
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.ApplicationEngineFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class KtorModuleContainer private constructor() : Cancelable {

    private val ktorModules: MutableList<KtorModule> = mutableListOf()

    var shutdownStatus = 0


    override fun cancel(p0: Boolean) {
        ktorModules.forEach {
            it.cancel(p0)
        }
    }

    suspend fun start(coroutineScope: CoroutineScope, lifeCheckDelayMS: Long = 60_000, wait: Boolean = true) {
        ktorModules.forEach {
            it.engine.start(false)
        }
        Runtime.getRuntime().addShutdownHook(thread(false) {
            shutdown()
        })
        val lifeCheckJob = coroutineScope.launch {
            lifeCheckFunc()
            delay(lifeCheckDelayMS)
        }
        if (wait) lifeCheckJob.join()
    }

    fun shutdown() {
        onShutdownFunc()
        cancel(true)
        exitProcess(shutdownStatus)
    }

    var lifeCheckFunc: () -> Unit = {}
    var onShutdownFunc: () -> Unit = {}

    fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> includeModule(
        ktorModule: KtorModule,
        coroutineScope: CoroutineScope,
        factory: ApplicationEngineFactory<TEngine, TConfiguration>,
        port: Int = 80,
        host: String = "0.0.0.0",
        watchPaths: List<String> = emptyList(),
        configure: TConfiguration.() -> Unit = {},
        onException: (Throwable) -> Unit
    ) {
        ktorModules.add(ktorModule)
        ktorModule.initialize(
            coroutineScope = coroutineScope,
            factory = factory,
            port = port,
            host = host,
            watchPaths = watchPaths,
            configure = configure,
            onException = onException
        )
    }


    fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> includeModule(
        ktorModule: KtorModule,
        coroutineScope: CoroutineScope,
        factory: ApplicationEngineFactory<TEngine, TConfiguration>,
        publicPort: Int = 80,
        loggerName: String = "ktor.application",
        watchPaths: List<String> = emptyList(),
        configure: TConfiguration.() -> Unit = {},
        envLogic: ApplicationEngineEnvironmentBuilder.() -> Unit,
        onException: (Throwable) -> Unit
    ) {
        ktorModules.add(ktorModule)
        ktorModule.initialize(
            coroutineScope = coroutineScope,
            factory = factory,
            publicPort = publicPort,
            loggerName = loggerName,
            watchPaths = watchPaths,
            configure = configure,
            envLogic = envLogic,
            onException = onException
        )
    }

    companion object {
        public fun ktorModuleContainer(block: KtorModuleContainer.() -> Unit) =
            KtorModuleContainer().apply(block = block)
    }
}


