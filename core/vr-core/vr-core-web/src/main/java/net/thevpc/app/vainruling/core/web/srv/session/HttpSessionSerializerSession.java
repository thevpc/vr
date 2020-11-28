package net.thevpc.app.vainruling.core.web.srv.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpSessionSerializerSession implements HttpSessionSerializer {
    @Override
    public void init(Map<String, String> config) {

    }

    @Override
    public String getType() {
        return "JSESSIONID";
    }

    @Override
    public HttpSessionId read(HttpServletRequest request) {
        String s = request.getParameter("jsessionid");
        if (s != null) {
            return new HttpSessionId_JSESSIONID(s);
        }
        s = request.getRequestedSessionId();
        if (s != null) {
            return new HttpSessionId_JSESSIONID(s);
        }
        return null;
    }

    @Override
    public void write(HttpServletResponse response, HttpSessionId id) {
    }

    @Override
    public void destroy() {

    }

}
