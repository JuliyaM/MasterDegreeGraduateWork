package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.*
import main.java.server.ktorModuleLibrary.ktorHtmlExtentions.produceForm


abstract class HtmlProduceView<User>(
    private val nameOfForm: String,
    private val action: String,
    private val objectID: String?,
    private val error: String?,
    private val hideHead: Boolean = false,
    private val formEncType: FormEncType,
    private val defaultObjectID: String,
    private val headBundle: Array<HeadDependencies>,
    private val header: HtmlFragment?
) : HtmlView() {

    final override fun getHTML(): HTML.() -> Unit =
        {
            head {
                include(*headBundle)
            }
            body {
                if (!hideHead && header != null) include(header)

                produceForm(
                    body = this,
                    nameOfForm = nameOfForm,
                    error = error,
                    objectID = objectID,
                    action = action,
                    formEncType = formEncType,
                    defaultObjectID = defaultObjectID
                ) {
                    formView()
                }

                script {
                    scriptText()
                }
            }
        }

    abstract fun SCRIPT.scriptText()

    abstract fun FORM.formView()
}


