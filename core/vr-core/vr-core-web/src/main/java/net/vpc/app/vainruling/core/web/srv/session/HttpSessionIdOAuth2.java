package net.vpc.app.vainruling.core.web.srv.session;

import com.google.gson.Gson;

import java.util.Map;

public class HttpSessionIdOAuth2 implements HttpSessionId{
    private String type;
    private String sessionId;
    private Map<String,Object> data;

    public HttpSessionIdOAuth2() {
        type="Auth2";
    }

    public HttpSessionIdOAuth2(String sessionId, Map<String,Object> data) {
        type="Auth2";
        this.sessionId = sessionId;
        this.data = data;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setType(String type) {
        //this.type = type;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpSessionIdOAuth2{" +
                "type='" + type + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpSessionIdOAuth2 that = (HttpSessionIdOAuth2) o;
        String s1 = new Gson().toJson(this);
        String s2 = new Gson().toJson(that);
        return s1.equals(s2);
    }

    @Override
    public int hashCode() {
        String s1 = new Gson().toJson(this);
        return s1.hashCode();
    }
}
