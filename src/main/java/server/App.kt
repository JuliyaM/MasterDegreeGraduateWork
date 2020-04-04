package main.java.server

import io.ktor.application.Application
import io.ktor.server.engine.connector
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import main.java.server.ktorModuleLibrary.modules.KtorModuleContainer

fun main() {
    val ktorModuleContainer = KtorModuleContainer.ktorModuleContainer {
        onShutdownFunc = {
            LoggerHelper.log("Shutdown")
        }
        lifeCheckFunc = {

        }
        shutdownStatus = 1

        includeModule(
            ktorModule = FrontEndKtorModule(),
            coroutineScope = GlobalScope,
            factory = Netty,
            publicPort = 8080,
            loggerName = "MainContainerKtorModule",
            configure = {
                //        this.parallelism
//        requestQueueLimit = 200
//        runningLimit = 100
//        responseWriteTimeoutSeconds = 15

                // Size of the event group for accepting connections
                connectionGroupSize = parallelism / 2 + 1
                // Size of the event group for processing connections,
                // parsing messages and doing engine's internal work
                workerGroupSize = parallelism / 2 + 1
                // Size of the event group for running application code
                callGroupSize = parallelism

            },
            envLogic = {
//                if (folderConfig.letsencryptJks.exists()) {
//                    try {
//                        val password = "123456kpi".toCharArray()
//                        val ks1 = KeyStore.getInstance("JKS")
//                        ks1.load(folderConfig.letsencryptJks.inputStream(), password)
//                        sslConnector(
//                            keyStore = ks1,
//                            keyAlias = "simple-cert",
//                            keyStorePassword = { password },
//                            privateKeyPassword = { password }) {
//                            port = 443
//                            keyStorePath = folderConfig.letsencryptJks.absoluteFile
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                    sslConnector(
//                        keyStore = ks2,
//                        keyAlias = "san-cert",
//                        keyStorePassword = { password },
//                        privateKeyPassword = { password }) {
//                        port = 444
//                        keyStorePath = folderConfig.letsencryptJks.absoluteFile
//                    }
                connector {
                    this.host = "0.0.0.0"
                    this.port = 80
                }
                // Public API
                connector {
                    this.host = "0.0.0.0"
                    this.port = 8080
                }
            },
            onException = {
                LoggerHelper.log("shutwdown")
                LoggerHelper.log(Application::javaClass.name, it)
//                shutdown()
            }
        )
    }
    runBlocking { ktorModuleContainer.start(GlobalScope) }
}