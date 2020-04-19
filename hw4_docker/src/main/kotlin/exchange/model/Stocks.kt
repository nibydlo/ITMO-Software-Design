package exchange.model

import kotlinx.serialization.Serializable

@Serializable
data class Stocks(val count: Long, val price: Long)
