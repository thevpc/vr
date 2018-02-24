package net.vpc.app.vainruling.core.web.ws;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpSessionSerializerXSession implements HttpSessionSerializer {
    @Override
    public void init(Map<String, String> config) {

    }

    @Override
    public String getType() {
        return "X-JSESSIONID";
    }

    @Override
    public HttpSessionId read(HttpServletRequest request) {
        String s = request.getHeader("X-JSESSIONID");
        if (s != null) {
            return new HttpSessionId_XJSESSIONID(s);
        }
        return null;
    }

    @Override
    public void write(HttpServletResponse response, HttpSessionId id) {
        response.setHeader("X-JSESSIONID", id.getSessionId());
    }

    @Override
    public void destroy() {

    }

}
