package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.InputType


abstract class HtmlEntry(
    val name: String,
    val id: String,
    val label: String?,
    val small: String?,
    val value: String?
)

class HtmlInputData(
    name: String,
    id: String,
    label: String?,
    small: String?,
    val inputType: InputType,
    value: String?
) : HtmlEntry(name = name, id = id, label = label, small = small, value = value)

class HtmlCheckBoxData(
    name: String,
    id: String,
    label: String?,
    small: String?,
    val inputType: InputType,
    val isChecked: Boolean,
    value: String? = "checked"
) : HtmlEntry(name = name, id = id, label = label, small = small, value = value)

class HtmlSelectData(
    name: String,
    id: String,
    label: String,
    small: String?,
    val enums: Map<String, String>,
    curValue: String?
) : HtmlEntry(name, id, label, small, curValue)
