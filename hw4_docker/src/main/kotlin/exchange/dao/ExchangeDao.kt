package exchange.dao

import exchange.model.Stocks

interface ExchangeDao {
    fun addCompany(company: String, stocks: Stocks)
    fun addStocks(company: String, count: Long)
    fun getStocks(company: String): Stocks?
    fun buyStocks(company: String, count: Long): Long
    fun sellStocks(company: String, count: Long): Long
    fun changePrice(company: String, newPrice: Long)
}
