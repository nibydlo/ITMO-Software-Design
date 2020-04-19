package controller

import exchange.model.Stocks
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import account.config.ExchangeClientConfig

class TestExchangeController(config: ExchangeClientConfig) {
    suspend fun addCompany(company: String, stocks: Stocks) {
        val url = urlBuilder()
            .addPath("add_company")
            .append("?")
            .addCompany(company)
            .append("&")
            .addCount(stocks.count)
            .append("&")
            .addPrice(stocks.price)
            .toString()
        sendCommand(url)
    }

    suspend fun changePrice(company: String, price: Long) {
        val url = urlBuilder()
            .addPath("change_price")
            .append("?")
            .addCompany(company)
            .append("&")
            .addPrice(price)
            .toString()
        sendCommand(url)
    }


    private val client = HttpClient { expectSuccess = false }
    private val base = "http://${config.host}:${config.port}"

    private suspend fun sendCommand(url: String) {
        val response = client.get<HttpResponse>(url)
        val content = response.readText()
        if (response.status != HttpStatusCode.OK) {
            throw ControllerException("Controller received error: $content")
        }
    }

    private fun urlBuilder(): StringBuilder = StringBuilder(base)

    private companion object {
        fun StringBuilder.addPath(path: String): StringBuilder = append("/$path")
        fun StringBuilder.addCompany(company: String): StringBuilder = append("company=$company")
        fun StringBuilder.addCount(count: Long): StringBuilder = append("count=$count")
        fun StringBuilder.addPrice(price: Long): StringBuilder = append("price=$price")
    }
}
