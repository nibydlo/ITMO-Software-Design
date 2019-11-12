package servlet;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GetProductServletTest {
    private final static String NAME_1 = "name_1";
    private final static String NAME_2 = "name_2";
    private final static String NAME_3 = "name_3";
    private final static long PRICE_1 = 1;
    private final static long PRICE_2 = 2;
    private final static long PRICE_3 = 3;


    @Mock
    ProductDao productDao;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    GetProductsServlet getProductsServlet;

    @Test
    public void getProductsTest() throws Exception {
        Mockito.when(productDao.getAll())
                .thenReturn(List.of(
                        new Product(NAME_1, PRICE_1),
                        new Product(NAME_2, PRICE_2),
                        new Product(NAME_3, PRICE_3)
                ));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        getProductsServlet.doGet(request, response);
        writer.flush();
        assertThat(stringWriter.toString(), Matchers.equalTo("<html><body>\n" +
                NAME_1 + "\t" + PRICE_1 + "</br>\n" +
                NAME_2 + "\t" + PRICE_2 + "</br>\n" +
                NAME_3 + "\t" + PRICE_3 + "</br>\n" +
                "</body></html>\n")
        );
    }
}
