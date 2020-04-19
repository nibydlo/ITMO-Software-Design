package exchange

import exchange.dao.InMemoryExchangeDao
import exchange.model.Stocks
import getCompany
import getCount
import getPrice
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import respondError
import respondNotEnoughParams
import kotlin.Exception


fun main(): Unit = runBlocking {
    val dao = InMemoryExchangeDao()
    val parser = Json(JsonConfiguration.Stable)
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/add_company") {
                val company = call.getCompany()
                val count = call.getCount()
                val price = call.getPrice()
                if ((company == null) || (count == null) || (price == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.addCompany(company, Stocks(count, price))
                    call.respondText("Added new company")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/add_stocks") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.addStocks(company, count)
                    call.respondText("Added stocks")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_stocks") {
                val company = call.getCompany()
                if (company == null) {
                    call.respondNotEnoughParams()
                } else {
                    val stocks = dao.getStocks(company)
                    val response = stocks?.let { parser.stringify(Stocks.serializer(), stocks) } ?: ""
                    call.respondText(response)
                }
            }
            get("/buy_stocks") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val debt = dao.buyStocks(company, count)
                    call.respondText(debt.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/sell_stocks") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val profit = dao.sellStocks(company, count)
                    call.respondText(profit.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/change_price") {
                val company = call.getCompany()
                val price = call.getPrice()
                if ((company == null) || (price == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.changePrice(company, price)
                    call.respondText("Price changed")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
        }
    }.start(wait = true)
    Unit
}
