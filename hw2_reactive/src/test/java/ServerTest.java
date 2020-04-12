import com.mongodb.rx.client.Success;
import dao.ReactiveMongoDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.internal.util.ScalarSynchronousObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.Server;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class ServerTest {

    ReactiveMongoDriver mongoDriver;
    Server server;

    private final static String ID_FIELD = "id";
    private final static String NAME_FIELD = "name";
    private final static String CURRENCY_FIELD = "currency";
    private final static String RUB_FIELD = "rub";
    private final static String EUR_FIELD = "eur";
    private final static String USD_FIELD = "usd";

    private final static String NEW_PRODUCT_RESPONSE = "New product:\n" +
            "Product: {\n" +
            "\tid: 42,\n" +
            "\tname: Test,\n" +
            "\tRUB: 80,\n" +
            "\tEUR: 1,\n" +
            "\tUSD: 2\n" +
            "}";

    private final static String NEW_USER_RESPONSE = "New user:\n" +
            "User: {\n" +
            "\tid: 101,\n" +
            "\tname: Test,\n" +
            "\tcurrency: USD\n" +
            "}";


    @Before
    public void before() {
        mongoDriver = Mockito.mock(ReactiveMongoDriver.class);
        Mockito.when(mongoDriver.addProduct(Mockito.any())).thenReturn(Success.SUCCESS);
        Mockito.when(mongoDriver.addUser(Mockito.any())).thenReturn(Success.SUCCESS);
        server = new Server(mongoDriver);
    }


    @Test
    public void testAddProduct() {
        Map<String, List<String>> params = new HashMap<>() {{
            put(ID_FIELD, List.of("42"));
            put(NAME_FIELD, List.of("Test"));
            put(RUB_FIELD, List.of("80"));
            put(EUR_FIELD, List.of("1"));
            put(USD_FIELD, List.of("2"));
        }};
        rx.Observable<String> res = server.addProduct(params);
        Assert.assertThat(res, instanceOf(ScalarSynchronousObservable.class));
        Assert.assertEquals(NEW_PRODUCT_RESPONSE,
                ((ScalarSynchronousObservable) res).get().toString());
    }


    @Test
    public void testAddProductMissingParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put(ID_FIELD, List.of("1"));
        rx.Observable<String> res = server.addProduct(params);

        String expectedResponse = "Please add missing params: name, rub, eur, usd";
        String actualResponse = ((ScalarSynchronousObservable) res).get().toString();
        Assert.assertEquals(expectedResponse, actualResponse);
    }


    @Test
    public void testAddUser() {
        Map<String, List<String>> params = new HashMap<>() {{
            put(ID_FIELD, List.of("101"));
            put(NAME_FIELD, List.of("Test"));
            put(CURRENCY_FIELD, List.of("USD"));
        }};
        rx.Observable<String> res = server.addUser(params);
        Assert.assertEquals(NEW_USER_RESPONSE, ((ScalarSynchronousObservable) res).get().toString());
    }


    @Test
    public void testAddUserMissingParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put(ID_FIELD, List.of("1"));
        rx.Observable<String> res = server.addUser(params);

        String expectedResponse = "Please add missing params: name, currency";
        String actualResponse = ((ScalarSynchronousObservable) res).get().toString();
        Assert.assertEquals(expectedResponse, actualResponse);
    }
}
