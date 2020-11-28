package net.thevpc.app.vainruling.core.web.srv.session;

public class HttpSessionId_JSESSIONID implements HttpSessionId{
    String type;
    String sessionId;

    public HttpSessionId_JSESSIONID(String sessionId) {
        this.type = "JSESSIONID";
        this.sessionId = sessionId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}
