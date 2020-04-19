package account.dao

import account.client.ExchangeClient
import account.model.AccountStocks
import account.model.Account
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class InMemoryAccountDao(private val client: ExchangeClient) : AccountDao {

    private val accounts = ConcurrentHashMap<Long, Account>()

    override fun addAccount(name: String): Long {
        while (true) {
            val newId = accounts.size.toLong()
            val putRes = accounts.putIfAbsent(newId, Account(name, 0, HashMap()))
            @Suppress("FoldInitializerAndIfToElvis")
            if (putRes == null) {
                return newId
            }
        }
    }

    override fun topUpBalance(id: Long, count: Long) {
        require(count > 0) { "Count must be positive" }
        val newBalance = accounts.computeIfPresent(id) { _, account -> account.copy(balance = account.balance + count) }

        check(newBalance != null) { "Account with id $id doesn't exist" }
    }

    override suspend fun getDetailedStocks(id: Long): Set<AccountStocks> {
        val account = accounts[id]

        check(account != null) { "Account with id $id doesn't exist" }

        return getAccountDetailedStocks(account).toSet()
    }

    override fun getBalance(id: Long): Long {
        val account = accounts[id]

        check(account != null) { "Account with id $id doesn't exist" }

        return account.balance
    }

    override suspend fun getTotalBalance(id: Long): Long {
        val account = accounts[id]

        check(account != null) { "Account with id $id doesn't exist" }

        val detailedStocks = getAccountDetailedStocks(account)
        val stocksBalance = detailedStocks.map { it.count * it.price }.sum()
        return stocksBalance + account.balance
    }

    override suspend fun buyStocks(id: Long, company: String, count: Long): Long {

        check(accounts.containsKey(id)) { "Account with id $id doesn't exist" }

        val debt = client.buyStocks(company, count)
        accounts.computeIfPresent(id) { _, account ->
            val mutableStocks = account.stocks.toMutableMap()
            mutableStocks.compute(company) { _, cnt -> cnt?.plus(count) ?: count }
            account.copy(
                balance = account.balance - debt,
                stocks = mutableStocks
            )
        }
        return debt
    }

    override suspend fun sellStocks(id: Long, company: String, count: Long): Long {
        var success = false
        accounts.computeIfPresent(id) { _, account ->
            if (account.stocks[company]?.let { it < count } != false) {
                success = false
                account
            } else {
                success = true
                val mutableStocks = account.stocks.toMutableMap()
                mutableStocks.computeIfPresent(company) { _, curCount -> curCount - count }
                account.copy(stocks = mutableStocks)
            }
        }
        check(success) { "No such account or not enough stocks to sell" }
        try {
            val profit = client.sellStocks(company, count)
            accounts.computeIfPresent(id) { _, account -> account.copy(balance = account.balance + profit) }
            return profit
        } catch (e: Exception) {
            accounts.computeIfPresent(id) { _, account ->
                val mutableStocks = account.stocks.toMutableMap()
                mutableStocks.computeIfPresent(company) { _, curCount -> curCount + count }
                account.copy(stocks = mutableStocks)
            }
            throw e
        }
    }

    private suspend fun getAccountDetailedStocks(account: Account): List<AccountStocks> {
        val accountStocks = account.stocks.toList()
        val accountCompanies = accountStocks.map { it.first }
        val prices = client.getStocksPrices(accountCompanies)
        return accountStocks.zip(prices).map { AccountStocks(it.first.first, it.first.second, it.second) }
    }
}
