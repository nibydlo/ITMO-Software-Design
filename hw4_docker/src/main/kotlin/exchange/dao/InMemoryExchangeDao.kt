package exchange.dao

import exchange.model.Stocks
import java.util.concurrent.ConcurrentHashMap

class InMemoryExchangeDao : ExchangeDao {
    override fun addCompany(company: String, stocks: Stocks) {
        require((stocks.count > 0) || (stocks.price > 0)) { "Stocks count and price must be positive" }
        check(companies.putIfAbsent(company, stocks) == null) { "Company $company already exists" }
    }

    override fun addStocks(company: String, count: Long) {
        require(count > 0) { "Added stocks count must be positive" }
        val newStocks = companies.computeIfPresent(company) { _, stocks -> stocks.copy(count = stocks.count + count) }
        check(newStocks != null) { "Company $company doesn't exists" }
    }

    override fun getStocks(company: String): Stocks? = companies[company]

    override fun buyStocks(company: String, count: Long): Long {
        require(count > 0) { "Stocks count must be positive" }
        var debt: Long? = null
        val newStocks = companies.computeIfPresent(company) { _, stocks ->
            if (stocks.count < count) {
                stocks
            } else {
                debt = count * stocks.price
                stocks.copy(count = stocks.count - count)
            }
        }
        check(newStocks != null) { "Company $company doesn't exists" }
        check(debt != null) { "Doesn't have enough stocks" }
        return debt!!
    }

    override fun sellStocks(company: String, count: Long): Long {
        require(count > 0) { "Count must be positive" }
        var profit = 0L
        val newStocks = companies.computeIfPresent(company) { _, stocks ->
            profit = count * stocks.price
            stocks.copy(count = stocks.count + count)
        }
        check(newStocks != null) { "Company $company doesn't exists" }
        return profit
    }

    override fun changePrice(company: String, newPrice: Long) {
        require(newPrice > 0) { "Price must be positive" }
        val newStocks = companies.computeIfPresent(company) { _, stocks -> stocks.copy(price = newPrice) }
        check(newStocks != null)
    }

    private val companies = ConcurrentHashMap<String, Stocks>()
}
