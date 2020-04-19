package account.client

interface ExchangeClient : AutoCloseable {
    suspend fun getStocksPrices(companies: List<String>): List<Long>
    suspend fun buyStocks(company: String, count: Long): Long
    suspend fun sellStocks(company: String, count: Long): Long
}
