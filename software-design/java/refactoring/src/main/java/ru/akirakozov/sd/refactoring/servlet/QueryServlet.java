package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

/**
 * @author akirakozov
 */
public class QueryServlet extends AbstractServlet {

    private final ProductDao productDao;

    public QueryServlet(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            List<String> data = new ArrayList<>();
            data.add("<h1>Product with max price: </h1>");
            Optional<Product> expensiveOptional = productDao.getMostExpensive();
            if (expensiveOptional.isPresent()) {
                data.add(expensiveOptional.get().getName() + "\t" + expensiveOptional.get().getPrice() + "</br>");
            } else {
                data.add("");
            }

            fillResponseWithBody(response, data);
        } else if ("min".equals(command)) {
            List<String> data = new ArrayList<>();
            data.add("<h1>Product with min price: </h1>");
            Optional<Product> cheapestOptional = productDao.getCheapest();
            if (cheapestOptional.isPresent()) {
                data.add(cheapestOptional.get().getName() + "\t" + cheapestOptional.get().getPrice() + "</br>");
            } else {
                data.add("");
            }

            fillResponseWithBody(response, data);
        } else if ("sum".equals(command)) {
            fillResponseWithBody(response, ImmutableList.of("Summary price: ", Long.toString(productDao.getTotalPrice())));
        } else if ("count".equals(command)) {
            fillResponseWithBody(response, ImmutableList.of("Number of products: ", Long.toString(productDao.getProductQuantity())));
        } else {
            response.getWriter().println("Unknown command: " + command);
        }
    }
}
