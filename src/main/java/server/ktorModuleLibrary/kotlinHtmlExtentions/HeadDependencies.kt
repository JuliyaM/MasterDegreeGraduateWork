package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.HEAD

interface HeadDependencies {
    fun implementation(): HEAD.() -> Unit
}
