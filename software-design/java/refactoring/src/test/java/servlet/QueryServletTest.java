package servlet;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QueryServletTest {
    private final static String EXPECTED_MAX = "<html><body>\n" +
            "<h1>Product with max price: </h1>\n" +
            "Max\t777</br>\n" +
            "</body></html>\n";
    private final static String EXPECTED_MIN = "<html><body>\n" +
            "<h1>Product with min price: </h1>\n" +
            "Min\t111</br>\n" +
            "</body></html>\n";
    private final static String EXPECTED_SUM = "<html><body>\n" +
            "Summary price: \n" +
            "555\n" +
            "</body></html>\n";
    private final static String EXPECTED_QUANTITY = "<html><body>\n" +
            "Number of products: \n" +
            "222\n" +
            "</body></html>\n";
    private final static String EXPECTED_INVALID = "Unknown command: invalid_command\n";


    @Mock
    ProductDao productDao;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    QueryServlet queryServlet;


    @Test
    public void findMaxTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("command"))).thenReturn("max");
        Mockito.when(productDao.getMostExpensive()).thenReturn(Optional.of(new Product("Max", 777L)));

        queryTest(EXPECTED_MAX);
    }

    @Test
    public void findMinTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("command"))).thenReturn("min");
        Mockito.when(productDao.getCheapest()).thenReturn(Optional.of(new Product("Min", 111L)));

        queryTest(EXPECTED_MIN);
    }

    @Test
    public void sumTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("command"))).thenReturn("sum");
        Mockito.when(productDao.getTotalPrice()).thenReturn(555L);

        queryTest(EXPECTED_SUM);
    }

    @Test
    public void countTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("command"))).thenReturn("count");
        Mockito.when(productDao.getProductQuantity()).thenReturn(222);

        queryTest(EXPECTED_QUANTITY);
    }

    @Test
    public void invalidTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("command"))).thenReturn("invalid_command");

        queryTest(EXPECTED_INVALID);
    }

    private void queryTest(String expected) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);
        queryServlet.doGet(request, response);
        writer.flush();
        assertThat(
                stringWriter.toString(),
                Matchers.equalTo(expected)
        );
    }
}
