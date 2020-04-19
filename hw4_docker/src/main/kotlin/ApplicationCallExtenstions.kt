import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText


fun ApplicationCall.getString(name: String): String? = request.queryParameters[name]

fun ApplicationCall.getLong(name: String): Long? = getString(name)?.toLong()

fun ApplicationCall.getId(): Long? = getLong("id")

fun ApplicationCall.getName(): String? = getString("name")

fun ApplicationCall.getCompany(): String? = getString("company")

fun ApplicationCall.getCount(): Long? = getLong("count")

fun ApplicationCall.getPrice(): Long? = getLong("price")

suspend fun ApplicationCall.respondNotEnoughParams(): Unit =
    respondText("Not enough params are provided", status = HttpStatusCode.BadRequest)


suspend fun ApplicationCall.respondError(e: Exception): Unit =
    respondText("Exception: ${e.message}", status = HttpStatusCode.BadRequest)
