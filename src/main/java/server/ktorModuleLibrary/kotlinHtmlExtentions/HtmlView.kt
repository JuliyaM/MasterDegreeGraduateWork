package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.HTML

abstract class HtmlView() {
    abstract fun getHTML(): HTML.() -> Unit
}