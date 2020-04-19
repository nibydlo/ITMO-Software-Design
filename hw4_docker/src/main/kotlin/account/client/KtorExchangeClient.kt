package account.client

import exchange.model.Stocks
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import account.config.ExchangeClientConfig

class KtorExchangeClient(config: ExchangeClientConfig) : ExchangeClient {
    override suspend fun getStocksPrices(companies: List<String>): List<Long> {
        val futures = coroutineScope {
            companies.map {
                async {
                    val url = urlBuilder()
                        .addPath("get_stocks")
                        .append("?")
                        .addCompany(it)
                        .toString()
                    getString(url)
                }
            }
        }
        return futures.map { parser.parse(Stocks.serializer(), it.await()).price }
    }

    override suspend fun buyStocks(company: String, count: Long): Long {
        val url = urlBuilder()
            .addPath("buy_stocks")
            .append("?")
            .addCompany(company)
            .append("&")
            .addCount(count)
            .toString()
        return getString(url).toLong()
    }

    override suspend fun sellStocks(company: String, count: Long): Long {
        val url = urlBuilder()
            .addPath("sell_stocks")
            .append("?")
            .addCompany(company)
            .append("&")
            .addCount(count)
            .toString()
        return getString(url).toLong()
    }

    override fun close() {
        client.close()
    }

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 2000
        }
        expectSuccess = false
    }
    private val base = "http://${config.host}:${config.port}"

    private fun urlBuilder(): StringBuilder = StringBuilder(base)

    private suspend fun getString(url: String): String {
        val response = client.get<HttpResponse>(url)
        val content = response.readText()
        check(response.status == HttpStatusCode.OK) { "Error from server: $content" }
        return content
    }

    private companion object {
        fun StringBuilder.addPath(path: String): StringBuilder = append("/$path")
        fun StringBuilder.addCompany(company: String): StringBuilder = append("company=$company")
        fun StringBuilder.addCount(count: Long): StringBuilder = append("count=$count")

        val parser = Json(JsonConfiguration.Stable)
    }
}
