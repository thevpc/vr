package net.vpc.app.vainruling.core.web.ws;

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
