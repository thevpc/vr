package net.vpc.app.vainruling.core.web.srv.session;

public class HttpSessionId_XJSESSIONID implements HttpSessionId{
    String type;
    String sessionId;

    public HttpSessionId_XJSESSIONID(String sessionId) {
        this.type = "X-JSESSIONID";
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
