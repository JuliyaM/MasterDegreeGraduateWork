package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.*

interface BootstrapExtentions {
    fun NAV.collapseNavBar(idStr: String, function: UL.() -> Unit) {
        div {
            classes = setOf("collapse", "navbar-collapse")
            id = idStr
            ul {
                classes = setOf("navbar-nav", "mr-auto")
                function()
            }
        }
    }

    fun UL.addItem(name: String, vararg elements: Pair<String, String>) {
        li(classes = "nav-item dropdown") {
            a(href = "#", classes = "nav-link dropdown-toggle") {
                attributes["data-toggle"] = "dropdown"
                attributes["aria-haspopup"] = "true"
                attributes["aria-expanded"] = "false"
                id = "navbarDropdownMenuLink"
                role = "button"
                +name
            }
            div(classes = "dropdown-menu") {
                attributes["aria-labelledby"] = "navbarDropdownMenuLink"

                elements.forEach {
                    a(href = it.second, classes = "dropdown-item") {
                        +it.first
                    }
                }
            }
        }
    }

    fun NAV.addBrand(name: String) {
        a(classes = "navbar-brand") {
            href = "/"
            +name
        }
    }

}