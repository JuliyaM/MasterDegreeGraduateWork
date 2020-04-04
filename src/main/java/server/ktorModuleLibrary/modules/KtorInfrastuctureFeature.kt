package main.java.server.ktorModuleLibrary.modules

import com.sun.xml.internal.ws.api.Cancelable


interface KtorInfrastructureFeature  : Cancelable {
    suspend fun initialize()
}
