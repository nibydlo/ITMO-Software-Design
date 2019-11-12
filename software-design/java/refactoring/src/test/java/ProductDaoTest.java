import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.runners.MockitoJUnitRunner;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.domain.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ProductDaoTest {
    private final static String NAME_1 = "name_1";
    private final static String NAME_2 = "name_2";
    private final static String NAME_3 = "name_3";
    private final static long PRICE_1 = 1;
    private final static long PRICE_2 = 2;
    private final static long PRICE_3 = 3;
    private final static String CONNECTION_URL = "jdbc:sqlite:test.db";

    private ProductDao productDao;

    @Before
    public void setup() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        }
        productDao = new ProductDao();
    }

    @After
    public void cleanDB() throws Exception {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate("delete from PRODUCT");
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertOneProductTest() {
        Product product = new Product(NAME_1, PRICE_1);
        productDao.insert(product);

        List<Product> products = productDao.getAll();
        assertThat(products, Matchers.contains(product));
    }

    @Test
    public void insertTwoProductsTest() {
        Product product1 = new Product(NAME_1, PRICE_1);
        Product product2 = new Product(NAME_2, PRICE_2);
        insertProducts(product1, product2);

        List<Product> products = productDao.getAll();
        assertThat(products, Matchers.contains(product1, product2));
    }

    @Test
    public void sumProductPriceTest() {
        insertProducts(
                new Product(NAME_1, PRICE_1),
                new Product(NAME_2, PRICE_2),
                new Product(NAME_3, PRICE_3)
        );

        long sum = productDao.getTotalPrice();
        assertThat(sum, Matchers.equalTo(6L));
    }

    @Test
    public void countProductsTest() {
        insertProducts(
                new Product(NAME_1, PRICE_1),
                new Product(NAME_2, PRICE_2),
                new Product(NAME_3, PRICE_3)
        );

        int count = productDao.getProductQuantity();
        assertThat(count, Matchers.equalTo(3));
    }

    @Test
    public void findMaxTest() {
        insertProducts(
                new Product(NAME_1, PRICE_1),
                new Product(NAME_2, PRICE_2),
                new Product(NAME_3, PRICE_3)
        );

        Optional<Product> actual = productDao.getMostExpensive();
        assertThat(actual.isPresent(), Matchers.equalTo(Boolean.TRUE));
        assertThat(actual.get(), Matchers.equalTo(new Product(NAME_3, PRICE_3)));
    }

    @Test
    public void findMaxEmptyDBTest() {
        Optional<Product> actual = productDao.getMostExpensive();
        assertThat(actual.isEmpty(), Matchers.equalTo(Boolean.TRUE));
    }

    @Test
    public void findMinTest() {
        insertProducts(
                new Product(NAME_1, PRICE_1),
                new Product(NAME_2, PRICE_2),
                new Product(NAME_3, PRICE_3)
        );

        Optional<Product> actual = productDao.getCheapest();
        assertThat(actual.isPresent(), Matchers.equalTo(Boolean.TRUE));
        assertThat(actual.get(), Matchers.equalTo(new Product(NAME_1, PRICE_1)));
    }

    @Test
    public void findMinEmptyDBTest() {
        Optional<Product> actual = productDao.getCheapest();
        assertThat(actual.isEmpty(), Matchers.equalTo(Boolean.TRUE));
    }

    private void insertProducts(Product... products) {
        Arrays.asList(products).forEach(product -> productDao.insert(product));
    }
}
