package server;

import com.mongodb.rx.client.Success;
import dao.ReactiveMongoDriver;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Product;
import model.User;
import rx.Observable;

import java.util.*;
import java.util.stream.Collectors;

import static model.User.Currency.RUB;
import static model.User.Currency.EUR;
import static model.User.Currency.USD;

public class Server {

    private final static int PORT = 8080;

    private final static String ID_FIELD = "id";
    private final static String UID_FIELD = "user_id";
    private final static String NAME_FIELD = "name";
    private final static String CURRENCY_FIELD = "currency";
    private final static String RUB_FIELD = "rub";
    private final static String EUR_FIELD = "eur";
    private final static String USD_FIELD = "usd";

    private final static String ADD_USER_METHOD = "addUser";
    private final static String GET_USERS_METHOD = "getUsers";
    private final static String ADD_PRODUCT_METHOD = "addProduct";
    private final static String GET_PRODUCTS_METHOD = "getProducts";
    private final static String WRONG_METHOD_MESSAGE = "Wrong method, available methods: "
            + ADD_USER_METHOD + ", "
            + GET_USERS_METHOD + ", "
            + ADD_PRODUCT_METHOD + ", "
            + GET_PRODUCTS_METHOD;
    private final static String MISSING_PARAMS_MESSAGE = "Please add missing params: ";

    private final static List<String> ADD_USER_PARAMS = List.of(ID_FIELD, NAME_FIELD, CURRENCY_FIELD);
    private final static List<String> ADD_PRODUCT_PARAMS = List.of(ID_FIELD, NAME_FIELD, RUB_FIELD, EUR_FIELD, USD_FIELD);
    private final static List<String> GET_PRODUCTS_PARAMS = List.of(UID_FIELD);

    ReactiveMongoDriver mongoDriver;

    public Server(ReactiveMongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(PORT).start((request, response) -> {
            String method = request.getDecodedPath().substring(1);
            Map<String, List<String>> params = request.getQueryParameters();
            if (ADD_USER_METHOD.equals(method)) {
                return response.writeString(addUser(params));
            }
            if (GET_USERS_METHOD.equals(method)) {
                return response.writeString(getUsers(params));
            }
            if (ADD_PRODUCT_METHOD.equals(method)) {
                return response.writeString(addProduct(params));
            }
            if (GET_PRODUCTS_METHOD.equals(method)) {
                return response.writeString(getProducts(params));
            }

            return response.writeString(
                    Observable.just(WRONG_METHOD_MESSAGE)
            );
        }).awaitShutdown();
    }

    public Observable<String> addUser(Map<String, List<String>> params) {
        String validation = "";
        List<String> missingParams = ADD_USER_PARAMS
                .stream()
                .filter(param -> !params.containsKey(param))
                .collect(Collectors.toList());
        if (!missingParams.isEmpty()) {
            validation =  MISSING_PARAMS_MESSAGE + String.join(", ", missingParams);
        }
        if (validation.length() > 0) return Observable.just(validation);

        int id = Integer.parseInt(params.get(ID_FIELD).get(0));
        String name = params.get(NAME_FIELD).get(0);

        String currency = params.get(CURRENCY_FIELD).get(0);
        User user = new User(id, name, Enum.valueOf(User.Currency.class, currency.toUpperCase()));
        if (mongoDriver.addUser(user) == Success.SUCCESS) {
            return Observable.just("New user:\n" + user.toString());
        } else {
            return Observable.just("Error");
        }
    }

    public Observable<String> getUsers(Map<String, List<String>> params) {
        return mongoDriver.getUsers();
    }

    public Observable<String> addProduct(Map<String, List<String>> params) {
        String validation = "";
        List<String> missingParams = ADD_PRODUCT_PARAMS
                .stream()
                .filter(param -> !params.containsKey(param))
                .collect(Collectors.toList());
        if (!missingParams.isEmpty()) {
            validation = MISSING_PARAMS_MESSAGE + String.join(", ", missingParams);
        }
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(ID_FIELD).get(0));
        String name = params.get(NAME_FIELD).get(0);

        String rub = params.get(RUB_FIELD).get(0);
        String eur = params.get(EUR_FIELD).get(0);
        String usd = params.get(USD_FIELD).get(0);

        Product product = new Product(id, name,
                new HashMap<>() {{
                    put(RUB, rub);
                    put(EUR, eur);
                    put(USD, usd);
                }});
        if (mongoDriver.addProduct(product) == Success.SUCCESS) {
            return Observable.just("New product:\n" + product.toString());
        } else {
            return Observable.just("Error");
        }
    }

    public Observable<String> getProducts(Map<String, List<String>> params) {
        String validation = "";
        List<String> missingParams = GET_PRODUCTS_PARAMS
                .stream()
                .filter(param -> !params.containsKey(param))
                .collect(Collectors.toList());
        if (!missingParams.isEmpty()) {
            validation = MISSING_PARAMS_MESSAGE + String.join(", ", missingParams);
        }
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(UID_FIELD).get(0));
        return mongoDriver.getProducts(id);
    }

}
