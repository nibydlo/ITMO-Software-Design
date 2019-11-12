package ru.akirakozov.sd.refactoring.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServlet extends HttpServlet {

    void fillResponse(HttpServletResponse response, List<String> data) throws IOException {
        data.forEach(string -> {
            try {
                response.getWriter().println(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    void fillResponseWithBody(HttpServletResponse response, List<String> data) throws IOException {
        List<String> bodyData = new ArrayList<>();
        bodyData.add("<html><body>");
        bodyData.addAll(data);
        bodyData.add("</body></html>");
        fillResponse(response, bodyData);
    }
}
