package hu.bme.aut

fun main() {

    val server = Server(
        apiKeys = setOf("demo-api-key"),
    )

    server.start()

}
