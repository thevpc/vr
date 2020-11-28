/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.srv;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.security.UserSession;

/**
 *
 * @author vpc
 */
@WebServlet(urlPatterns = "/fbsrv")
public class FBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String account_linking_token = req.getParameter("account_linking_token");
        String redirect_uri = req.getParameter("redirect_uri");
        CorePlugin core = CorePlugin.get();
        UserSession currentSession = CorePlugin.get().getCurrentSession();
        if (core.getCurrentUser() == null) {
            currentSession.setPreConnexionURL(redirect_uri + "?authorization_code=" + currentSession.getToken().getSessionId());
            currentSession.setInvalidConnexionURL(redirect_uri);
            req.getSession().setAttribute("fbsrv_account_linking_token", account_linking_token);
            resp.sendRedirect("/p/loginCtrl");
        } else {
            String authorization_code =currentSession.getToken().getSessionId();
            if (authorization_code == null) {
                resp.sendRedirect(redirect_uri);
            } else {
                if (redirect_uri.contains("?")) {
                    resp.sendRedirect(redirect_uri + "&authorization_code=" + authorization_code);
                } else {
                    resp.sendRedirect(redirect_uri + "?authorization_code=" + authorization_code);
                }
            }
        }
    }

}
