package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.*


interface HtmlFragment {

    fun getFragment(): BODY.() -> Unit
}
