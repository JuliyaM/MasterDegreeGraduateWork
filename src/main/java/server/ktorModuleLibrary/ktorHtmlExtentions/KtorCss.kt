package main.java.server.ktorModuleLibrary.ktorHtmlExtentions

import io.ktor.http.ContentType
import io.ktor.routing.Route
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.html.*
import ktorModuleLibrary.ktorHtmlExtentions.RoutingController
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlInputData
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.createInputFromEntry


fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.innerStyle(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString()
}


fun Route.include(routingController: RoutingController) {
    routingController.createFormRouting()()
}


@HtmlTagMarker
fun produceForm(
    body: BODY,
    nameOfForm: String,
    error: String?,
    objectID: String?,
    action: String,
    formEncType: FormEncType,
    defaultObjectID : String,
    formFunction: FORM.() -> Unit
) {
    body.form(
        action = action,
        classes = "text-center border border-light mx-auto my-5",
        method = FormMethod.post,
        encType = formEncType
    ) {
        attributes["accept-charset"] = "UTF-8"

        this.createInputFromEntry(
            HtmlInputData(
                name = defaultObjectID,
                id = defaultObjectID,
                label = null,
                small = null,
                inputType = InputType.hidden,
                value = objectID
            )
        )

        if (error != null) {
            this.p("h4 mb-4") {
                this.styleCss {
                    this.color = Color.red
                }
                +error
            }
        }

        this.p("h4 mb-4") {
            +nameOfForm
        }

        this.formFunction()

        this.button(classes = "btn btn-info my-4 btn-block", type = ButtonType.submit) {
            +"Submit"
        }

        this.hr()
        this.p {
            +"By clicking"
            this.em { +"Submit" }
            +"you agree to our terms of service"
        }
    }

}