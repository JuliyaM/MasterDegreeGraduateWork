package main.java.server

import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HeadDependencies
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include
import kotlinx.css.*
import kotlinx.html.*
import ktorModuleLibrary.ktorHtmlExtentions.styleCss


object MySupportBundle : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            include(
                AjaxJsImpl,
                GoogleRaleway,
                JQueryImpl,
                ViewPortMetaImpl
            )
        }
}

object MyBootstrapBundle : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            include(
                MySupportBundle,
                BootstrapCssImpl,
                BootstrapJsImpl
            )
        }
}

object MyUiKitBundle : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            include(
                MySupportBundle,
                UiKitCSS,
                UiKitJsImpl,
                UiKitIconImpl
            )
        }
}

object MyMaterializeBundle : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            include(
                MaterializeCSS,
                MaterializeJS,
                MySupportBundle
            )
        }
}

object BootstrapCssImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            link(
                href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css",
                rel = "stylesheet"
            ) {
                attributes["crossorigin"] = "anonymous"
            }
        }
}

object ChartCssImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            link(
                href = "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.css",
                rel = "stylesheet"
            )
        }
}

object ViewPortMetaImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            meta {
                name = "viewport"
                content = "width=device-width, initial-scale=1, shrink-to-fit=no"
            }
        }
}

object DataTableImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            include(
                DataTableCssImpl,
                DataTableJsImpl
            )
        }
}

object JqueryResizableColumnsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "$JQUERY_JS_LINK.resizableColumns.js") {

            }
        }
}


const val JQUERY_CSS_LINK = "https://cdn.datatables.net/1.10.19/css/jquery"
const val JQUERY_JS_LINK = "https://cdn.datatables.net/1.10.19/js/jquery"

object DataTableCssImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            link(
                href = "$JQUERY_CSS_LINK.dataTables.css",
                rel = "stylesheet"
            )
        }
    }
}

object DataTableJsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "$JQUERY_JS_LINK.dataTables.js") {
                charset = "utf8"
            }
        }
}

object BootstrapJsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js") {
                integrity = "sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
                attributes["crossorigin"] = "anonymous"
            }
        }
}

object ChartJsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js") {

            }
        }
}

object JQueryImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "https://code.jquery.com/jquery-3.3.1.slim.min.js") {
                integrity = "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
                attributes["crossorigin"] = "anonymous"
            }
        }
}


object FormCSS : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            styleCss {
                body {
                    backgroundImage = Image("url(/static/container.png)")
                }

                form {
                    backgroundColor = rgba(255, 255, 255, 0.85)
                    borderRadius = 15.px
                    width = 66.pct
                    padding(3.pct)
//                    elevation: 20deg;
                }
            }
        }
}

object AjaxJsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit =
        {
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js") {
                integrity = "sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
                attributes["crossorigin"] = "anonymous"
            }
        }
}

object GoogleRoboto : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            link(
                href = "https://fonts.googleapis.com/css?family=Roboto&display=swap",
                rel = "stylesheet"
            )
            styleCss {
                unsafe {
                    raw(
                        """
                        *{
                        font-family: 'Roboto', sans-serif;
                        }
                    """.trimIndent()
                    )
                }
            }
        }
    }
}

object GoogleRaleway : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            link(
                href = "https://fonts.googleapis.com/css?family=Raleway&display=swap",
                rel = "stylesheet"
            )
            styleCss {
                unsafe {
                    raw(
                        """
                        *{
                        font-family: 'Raleway', sans-serif;
                        }
                    """.trimIndent()
                    )
                }
            }
        }
    }
}


class TitleImpl(private val text: String) : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            title {
                +text
            }
        }
    }
}

object MaterializeJS : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js") {
                charset = "utf8"
            }
        }
    }
}

object MaterializeCSS : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            link(
                href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css",
                rel = "stylesheet"
            )
        }
    }
}


object UiKitJsImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/uikit/3.1.9/js/uikit.min.js") {
                charset = "utf8"
            }
        }
    }
}

object UiKitIconImpl : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/uikit/3.1.9/js/uikit-icons.min.js") {
                charset = "utf8"
            }
        }
    }
}

object UiKitCSS : HeadDependencies {
    override fun implementation(): HEAD.() -> Unit {
        return {
            link(
                href = "https://cdnjs.cloudflare.com/ajax/libs/uikit/3.1.9/css/uikit.min.css",
                rel = "stylesheet"
            )
        }
    }
}