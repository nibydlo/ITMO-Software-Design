package ru.akirakozov.sd.refactoring.dao;

import ru.akirakozov.sd.refactoring.domain.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao {
    private final static String CONNECTION_URL = "jdbc:sqlite:test.db";
    private final static String NAME_FIELD = "name";
    private final static String PRICE_FIELD = "price";
    private final static String INSERT_BLANK = "INSERT INTO PRODUCT (NAME, PRICE) VALUES (\"%s\",%s)";
    private final static String SELECT_ALL_QUERY = "SELECT * FROM PRODUCT";
    private final static String SELECT_MOST_EXPRENSIVE_QUERY = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
    private final static String SELECT_CHEAPEST_QUERY = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
    private final static String SELECT_TOTAL_PRICE_QUERY = "SELECT SUM(price) FROM PRODUCT";
    private final static String SELECT_PRODUCT_QUANTITY_QUERY = "SELECT COUNT(*) FROM PRODUCT";

    public void insert(Product product) {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            String sql = String.format(INSERT_BLANK, product.getName(), product.getPrice());
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> getAll() {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY);

            List<Product> productList = new ArrayList<>();
            while (rs.next()) {
                productList.add(resultSetToProduct(rs));
            }
            return productList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Product> getMostExpensive() {
        return getProduct(SELECT_MOST_EXPRENSIVE_QUERY);
    }

    public Optional<Product> getCheapest() {
        return getProduct(SELECT_CHEAPEST_QUERY);
    }

    private Optional<Product> getProduct(String query) {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return Optional.of(resultSetToProduct(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getTotalPrice() {
        return getStatistic(SELECT_TOTAL_PRICE_QUERY);
    }

    public int getProductQuantity() {
        return getStatistic(SELECT_PRODUCT_QUANTITY_QUERY);
    }

    private int getStatistic(String selectProductQuantityQuery) {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(selectProductQuantityQuery);

            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product resultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getString(NAME_FIELD), rs.getLong(PRICE_FIELD));
    }
}
