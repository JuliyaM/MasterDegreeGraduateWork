package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.*


fun String.processHtml(): String {
    return this.replace("\n", "<br>")
}


fun HEAD.include(vararg headDependencies: HeadDependencies) {
    headDependencies.forEach {
        it.implementation()()
    }
}

fun FlowContent.include(vararg headDependencies: HtmlFragment) {
    headDependencies.forEach {
        it.getFragment()()
    }
}


fun Tag.saveText(s: String?) {
    text(s ?: "")
}

fun INPUT.saveValue(s: String?) {
    value = s ?: ""
}

fun FORM.createInputFromEntry(htmlEntry: HtmlEntry) {
    bodyOfForm(htmlEntry)
}

private fun FlowContent.bodyOfForm(htmlEntry: HtmlEntry) {
    when (htmlEntry) {
        is HtmlInputData -> {
            if (htmlEntry.label != null) label {
                htmlFor = htmlEntry.name
                +htmlEntry.label
            }
            input(
                type = htmlEntry.inputType,
                name = htmlEntry.name,
                classes = "form-control"
            ) {
                saveValue(htmlEntry.value)
            }
        }
        is HtmlSelectData -> {
            if (htmlEntry.label != null) label {
                htmlFor = htmlEntry.name
                saveText(htmlEntry.label)
            }
            select(classes = "custom-select") {
                name = htmlEntry.name
                id = htmlEntry.id
                htmlEntry.enums.forEach { (valueName, printName) ->
                    option {
                        if (htmlEntry.value == valueName) {
                            selected = true
                        }
                        value = valueName
                        +printName
                    }
                }
            }
        }
        is HtmlCheckBoxData -> {
            div(classes = "form-check") {
                input(
                    type = htmlEntry.inputType,
                    name = htmlEntry.name,
                    classes = "form-check-input"
                ) {
                    checked = htmlEntry.isChecked
                    saveValue(htmlEntry.value)
                }
                if (htmlEntry.label != null) label(classes = "form-check-label") {
                    htmlFor = htmlEntry.name
                    saveText(htmlEntry.label)
                }
            }
        }
    }

    if (htmlEntry.small != null) small(classes = "form-text text-muted mb-4") {
        saveText(htmlEntry.small)
    }
}


