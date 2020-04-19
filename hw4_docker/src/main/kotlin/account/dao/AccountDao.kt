package account.dao

import account.model.AccountStocks

interface AccountDao {
    fun addAccount(name: String): Long
    fun topUpBalance(id: Long, count: Long)
    fun getBalance(id: Long): Long
    suspend fun getDetailedStocks(id: Long): Set<AccountStocks>
    suspend fun getTotalBalance(id: Long): Long
    suspend fun buyStocks(id: Long, company: String, count: Long): Long
    suspend fun sellStocks(id: Long, company: String, count: Long): Long
}
