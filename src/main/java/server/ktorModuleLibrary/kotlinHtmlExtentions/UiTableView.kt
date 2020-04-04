package main.java.server.ktorModuleLibrary.kotlinHtmlExtentions

import kotlinx.html.*
import main.java.server.MyUiKitBundle


abstract class UiTableView<User>(
    internal val user: User,
    private val tableName: String,
    private val hideHead: Boolean = false,
    private val header: HtmlFragment?
) : HtmlView() {

    override fun getHTML(): HTML.() -> Unit =
        {
            head {
                include(MyUiKitBundle)
            }
            body {
                if (!hideHead && header!=null) include(header)


                div(classes = "my_table_Wrapper mx-2 my-3") {
                    table(classes = "uk-table uk-table-hover uk-table-striped") {
                        this.id = "table$tableName"
                        caption { +tableName }
                        thead(block = getTHEAD())
                        tbody(block = getTBODY())
                        tfoot(block = getTFOOT())
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
//                        raw(
//                            """${'$'}(function(){
//                                  ${'$'}("table").resizableColumns({
//                                    store: window.store
//                                  });
//                                });
//                            """.trimIndent()
//                        )
                    }
                    getScript()()
                }
            }
        }

    abstract fun getTHEAD(): THEAD.() -> Unit
    abstract fun getTBODY(): TBODY.() -> Unit
    open fun getTFOOT(): TFOOT.() -> Unit = {}
    open fun getScript(): SCRIPT.() -> Unit = {}

}