package account.model

data class Account(val name: String, val balance: Long, val stocks: Map<String, Long>)
