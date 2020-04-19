package account

import com.typesafe.config.ConfigFactory
import getCompany
import getCount
import getId
import getName
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import account.config.ExchangeClientConfig
import account.dao.InMemoryAccountDao
import account.client.KtorExchangeClient
import java.nio.file.Paths
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.serialization.builtins.set
import respondError
import respondNotEnoughParams
import account.model.AccountStocks

fun main(): Unit = runBlocking {
    val configFile = Paths.get("src/main/resources/account.conf").toFile()
    val config = ConfigFactory.parseFile(configFile)
    val clientConfig = ExchangeClientConfig.fromConfig(config.getConfig("client"))
    val serverConfig = config.getConfig("server")
    val client = KtorExchangeClient(clientConfig)
    val dao = InMemoryAccountDao(client)
    val parser = Json(JsonConfiguration.Stable)
    embeddedServer(Netty, port = serverConfig.getInt("port")) {
        routing {
            get("/add_account") {
                val name = call.getName()
                if (name == null) {
                    call.respondNotEnoughParams()
                } else {
                    val id = dao.addAccount(name)
                    call.respondText("Added account with id = $id")
                }
            }
            get("/top_up_balance") {
                val id = call.getId()
                val count = call.getCount()
                if ((id == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.topUpBalance(id, count)
                    call.respondText("Successfully topped up")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_balance") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val balance = dao.getBalance(id)
                    call.respondText(balance.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_detailed_stocks") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val detailedStocks = dao.getDetailedStocks(id)
                    call.respondText(parser.stringify(AccountStocks.serializer().set, detailedStocks))
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_total_balance") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val balance = dao.getTotalBalance(id)
                    call.respondText(balance.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/buy_stocks") {
                val id = call.getId()
                val company = call.getCompany()
                val count = call.getCount()
                if ((id == null) || (company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val debt = dao.buyStocks(id, company, count)
                    call.respondText(debt.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/sell_stocks") {
                val id = call.getId()
                val company = call.getCompany()
                val count = call.getCount()
                if ((id == null) || (company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val profit = dao.sellStocks(id, company, count)
                    call.respondText(profit.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
        }
    }.start(wait = true)
    Unit
}
