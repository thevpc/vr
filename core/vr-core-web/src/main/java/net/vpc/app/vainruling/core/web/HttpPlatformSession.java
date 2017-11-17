package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.PlatformSession;

import javax.servlet.http.HttpSession;

public class HttpPlatformSession implements PlatformSession {
    private HttpSession session;

    public HttpPlatformSession(HttpSession session) {
        this.session = session;
    }

    public boolean isValid(){
        try {
            session.getLastAccessedTime();
        } catch (IllegalStateException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean invalidate() {
        try {
            session.invalidate();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
