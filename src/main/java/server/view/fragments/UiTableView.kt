package main.java.server.view.fragments

import kotlinx.html.*
import main.java.server.MyUiKitBundle
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.HtmlFragment
import main.java.server.ktorModuleLibrary.kotlinHtmlExtentions.include


class UiTableFragment(
    private val tableName: String,
    private val tHead: THEAD.() -> Unit = {},
    private val tBody: TBODY.() -> Unit = {},
    private val tFoot: TFOOT.() -> Unit = {}
) : HtmlFragment {

    override fun getFragment(): FlowContent.() -> Unit = {
        div(classes = "my_table_Wrapper mx-2 my-3") {
            table(classes = "uk-table uk-table-hover uk-table-striped") {
                this.id = "table${tableName.hashCode()}"
                caption { +tableName }
                thead(block = tHead)
                tbody(block = tBody)
                tfoot(block = tFoot,classes = "uk-text-bold")
            }
        }

        script {
            unsafe {
                raw(
                    """${'$'}(document).ready(function () {
                                
                                    var table= ${'$'}('#example').DataTable({orderCellsTop: true,
        fixedHeader: true, searching: true, paging: true, info: true});
                                ${'$'}('#example thead tr').clone(true).appendTo( '#example thead' );
    ${'$'}('#example thead tr:eq(1) th').each( function (i) {
        var title = ${'$'}(this).text();
        ${'$'}(this).html( '<input type="text" placeholder="Search '+title+'" />' );
 
        ${'$'}( 'input', this ).on( 'keyup change', function () {
            if ( table.column(i).search() !== this.value ) {
                table
                    .column(i)
                    .search( this.value )
                    .draw();
            }
        } );
    } );
    
                                });""".trimIndent()
                )
            }
        }
    }
}