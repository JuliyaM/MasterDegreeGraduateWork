package main.java.server.features

import main.java.server.ktorModuleLibrary.modules.KtorInfrastructureFeature
import java.io.File


class FileProviderFeature : KtorInfrastructureFeature {

    lateinit var pathToStaticFiles: File private set
    lateinit var pathToUserFiles: File private set

    override suspend fun initialize() {
        pathToStaticFiles = File("${System.getProperty("user.dir")}${File.separator}staticRes")
        pathToStaticFiles.mkdir()
        pathToUserFiles =
            File("${System.getProperty("user.dir")}${File.separator}staticRes${File.separator}img${File.separator}userFiles")
        pathToUserFiles.mkdir()
    }

    override fun cancel(mayInterruptIfRunning: Boolean) {

    }
}