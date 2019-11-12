package servlet;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddProductServletTest {
    private final static String NAME = "name";
    private final static long PRICE = 1;
    @Mock
    ProductDao productDao;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    AddProductServlet addProductServlet;

    @Test
    public void addProductTest() throws Exception {
        Mockito.when(request.getParameter(Mockito.eq("name"))).thenReturn(NAME);
        Mockito.when(request.getParameter(Mockito.eq("price"))).thenReturn(Long.toString(PRICE));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        addProductServlet.doGet(request, response);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productDao).insert(productCaptor.capture());
        Mockito.verify(request, Mockito.atLeast(1)).getParameter("name");
        Mockito.verify(request, Mockito.atLeast(1)).getParameter("price");
        writer.flush();

        assertThat(stringWriter.toString(), Matchers.startsWith("OK"));
        assertThat(productCaptor.getValue(), Matchers.equalTo(new Product(NAME, PRICE)));
    }
}
