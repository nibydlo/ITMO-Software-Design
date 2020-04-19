import controller.ControllerException
import controller.TestExchangeController
import exchange.model.Stocks
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import account.config.ExchangeClientConfig
import account.dao.InMemoryAccountDao
import account.client.KtorExchangeClient
import account.model.AccountStocks
import container.KFixedHostPortGenericContainer

class InMemoryAccountDaoTest {
    private val testClientConfig = ExchangeClientConfig("localhost", 1514)

    private var exchange: KFixedHostPortGenericContainer? = null
    private val client = KtorExchangeClient(testClientConfig)
    private val controller = TestExchangeController(testClientConfig)
    private var dao = InMemoryAccountDao(client)

    private val accountName = "account_name"
    private val companyName = "company_name"
    private val anotherCompanyName = "another_company_name"
    private val balanceDelta = 200L
    private val stocksCount = 10L
    private val stocksCountDelta = 5L
    private val anotherStocksCountDelta = 2L
    private val stocksPrice = 20L
    private val anotherStocksPrice = 7L

    @Before
    fun startExchange() {
        dao = InMemoryAccountDao(client)
        exchange = KFixedHostPortGenericContainer("krylov/exchange:latest")
            .withFixedExposedPort(testClientConfig.port, 8080)
            .withExposedPorts(8080)
        exchange!!.start()
    }

    @After
    fun stopExchange() {
        exchange!!.stop()
    }

    @Test
    fun testAddAccount() {
        val id = dao.addAccount(accountName)
        assertEquals(0, dao.getBalance(id))
    }

    @Test
    fun testTopUpBalance() {
        val id = dao.addAccount(accountName)
        assertEquals(0, dao.getBalance(id))
        dao.topUpBalance(id, balanceDelta)
        assertEquals(balanceDelta, dao.getBalance(id))
    }

    @Test(expected = IllegalStateException::class)
    fun testSellingAbsentStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.sellStocks(id, companyName, stocksCount)
        Unit
    }

    @Test(expected = IllegalStateException::class)
    fun testBuyingNonExistingStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.buyStocks(id, companyName, stocksCount)
        Unit
    }

    @Test(expected = ControllerException::class)
    fun testChangingPriceForNonExistingCompany() = runBlocking {
        controller.changePrice(companyName, stocksPrice)
    }

    @Test
    fun testSimplePurchase() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        val debt = dao.buyStocks(id, companyName, stocksCountDelta)
        assertEquals(balanceDelta - stocksCountDelta * stocksPrice, dao.getBalance(id))
        assertEquals(stocksCountDelta * stocksPrice, debt)
    }

    @Test
    fun testDetailedStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        controller.addCompany(anotherCompanyName, Stocks(stocksCount, anotherStocksPrice))
        val detailedStocks = HashSet<AccountStocks>()
        assertEquals(detailedStocks, dao.getDetailedStocks(id))

        dao.buyStocks(id, companyName, stocksCountDelta)
        detailedStocks.add(AccountStocks(companyName, stocksCountDelta, stocksPrice))
        assertEquals(detailedStocks, dao.getDetailedStocks(id))

        dao.buyStocks(id, anotherCompanyName, anotherStocksCountDelta)
        detailedStocks.add(AccountStocks(anotherCompanyName, anotherStocksCountDelta, anotherStocksPrice))
        assertEquals(detailedStocks, dao.getDetailedStocks(id))
        Unit
    }

    @Test(expected = IllegalStateException::class)
    fun testBuyTooManyStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        dao.buyStocks(id, companyName, stocksCount + 1)
        Unit
    }

    @Test
    fun testSimpleSellStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        dao.buyStocks(id, companyName, stocksCountDelta)
        assertEquals(balanceDelta - stocksCountDelta * stocksPrice, dao.getBalance(id))

        val profit = dao.sellStocks(id, companyName, stocksCountDelta)
        assertEquals(stocksCountDelta * stocksPrice, profit)
        assertEquals(balanceDelta, dao.getTotalBalance(id))
    }

    @Test(expected = IllegalStateException::class)
    fun testSellTooMuchStocks() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        dao.buyStocks(id, companyName, stocksCountDelta)
        dao.sellStocks(id, companyName, stocksCountDelta + 1)
        Unit
    }

    @Test
    fun testChangingPrice() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        dao.buyStocks(id, companyName, stocksCountDelta)
        controller.changePrice(companyName, stocksPrice * 2)
        assertEquals(setOf(AccountStocks(companyName, stocksCountDelta, stocksPrice * 2)), dao.getDetailedStocks(id))
        assertEquals(balanceDelta + stocksCountDelta * stocksPrice, dao.getTotalBalance(id))
    }

    @Test
    fun testServerErrorDuringSelling() = runBlocking {
        val id = dao.addAccount(accountName)
        dao.topUpBalance(id, balanceDelta)
        controller.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        dao.buyStocks(id, companyName, stocksCountDelta)
        exchange!!.pause()
        try {
            dao.sellStocks(id, companyName, stocksCountDelta - 2)
            fail("O-ups")
        } catch (e: IllegalStateException) {
            exchange!!.unpause()
            assertEquals(balanceDelta - stocksCountDelta * stocksPrice, dao.getBalance(id))
            assertEquals(setOf(AccountStocks(companyName, stocksCountDelta, stocksPrice)), dao.getDetailedStocks(id))
        }
    }
}
