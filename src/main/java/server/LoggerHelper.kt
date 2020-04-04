package main.java.server

object LoggerHelper {
    fun log(text : String){
        println(text)
    }

    fun log(clazz: String,throwable: Throwable){
        println("EXCEPTION IN $clazz")
        throwable.printStackTrace()
    }
}