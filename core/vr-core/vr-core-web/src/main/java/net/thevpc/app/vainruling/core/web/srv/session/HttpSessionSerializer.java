package net.thevpc.app.vainruling.core.web.srv.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface HttpSessionSerializer {
    void init(Map<String, String> config);

    void destroy();

    String getType();

    HttpSessionId read(HttpServletRequest request);

    void write(HttpServletResponse response, HttpSessionId id);
}
