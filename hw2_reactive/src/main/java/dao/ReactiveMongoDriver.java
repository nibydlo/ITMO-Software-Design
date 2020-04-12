package dao;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import model.Product;
import model.User;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriver {

    private MongoClient mongoClient;

    private final static String DATABASE = "shop";
    private final static String USERS_COLLECTION = "users";
    private final static String PRODUCTS_COLLECTION = "products";
    private final static String ID_FIELD = "id";

    private final static Integer TIMEOUT = 15;

    public ReactiveMongoDriver() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
    }

    public Success addProduct(Product product) {
        return mongoClient
                .getDatabase(DATABASE)
                .getCollection(PRODUCTS_COLLECTION)
                .insertOne(product.toDocument())
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public Observable<String> getProducts(Integer id) {
        return mongoClient
                .getDatabase(DATABASE)
                .getCollection(PRODUCTS_COLLECTION)
                .find()
                .toObservable()
                .map(document -> new Product(document).toString(findUser(id).getCurrency()))
                .reduce((product1, product2) -> product1 + "\n" + product2);
    }

    public Success addUser(User user) {
        return mongoClient
                .getDatabase(DATABASE)
                .getCollection(USERS_COLLECTION)
                .insertOne(user.toDocument())
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public User findUser(Integer id) {
        return mongoClient
                .getDatabase(DATABASE)
                .getCollection(USERS_COLLECTION)
                .find(eq(ID_FIELD, id))
                .first()
                .map(User::new)
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public Observable<String> getUsers() {
        return mongoClient
                .getDatabase(DATABASE)
                .getCollection(USERS_COLLECTION)
                .find()
                .toObservable()
                .map(document -> new User(document).toString())
                .reduce((user1, user2) -> user1 + "\n" + user2);
    }
}
