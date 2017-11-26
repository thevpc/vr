/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.WebScriptServiceInvoker;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.common.strings.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "WebScriptServlet", urlPatterns = "/ws/*")
public class WebScriptServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(WebScriptServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (StringUtils.isEmpty(pathInfo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (pathInfo.equals("/core/login") || pathInfo.equals("/login") || pathInfo.startsWith("/core/login/") || pathInfo.startsWith("/login")) {
            try {
                String login=request.getParameter("login");
                if(pathInfo.startsWith("/core/login/")){
                    login=pathInfo.substring(("/core/login/").length());
                }else if(pathInfo.startsWith("/login/")){
                    login=pathInfo.substring(("/login/").length());
                }
                VrWebHelper.prepareUserSession();
                UserSession userSession = UserSession.get();
                AppUser u = VrApp.getBean(CorePlugin.class).login(login, request.getParameter("password"),"WS/S",request.getParameter("app"));
                if (u != null) {
                    sendSimpleResult(response, new AppUserStrict(u));
                } else {
                    sendError(response, "SecurityException", "Invalid login or password");
                }
            } catch (Exception e) {
                sendResult(response, WebScriptServiceInvoker.buildError(e, null));
            }
        } else if (pathInfo.equals("/core/logout") || pathInfo.equals("/logout")) {
            try {
                VrApp.getBean(CorePlugin.class).logout();
                sendSimpleResult(response,"bye");
            } catch (Exception e) {
                sendError(response,e);
            }
        } else if (pathInfo.equals("/core/wscript") || pathInfo.equals("/wscript")) {
            WebScriptServiceInvoker e=new WebScriptServiceInvoker();
            String s=request.getParameter("s");
            if(s==null){
                s=request.getParameter("script");
                if(s==null){
                    s=VrUtils.toString(request.getReader());
                }
            }
            sendResult(response,e.invoke(s));

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void sendSimpleResult(HttpServletResponse response, Object simpleObj) throws IOException {
        sendResult(response,WebScriptServiceInvoker.buildSimpleResult(simpleObj,null));
    }

    private void sendError(HttpServletResponse response,Exception m) throws IOException {
        sendResult(response,WebScriptServiceInvoker.buildError(m,null));
    }

    private void sendError(HttpServletResponse response,String type,String m) throws IOException {
        sendResult(response,WebScriptServiceInvoker.buildError(type,m,null));
    }
    private void sendResult(HttpServletResponse response,Map m) throws IOException {
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","GET,POST");
        response.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Expose-Headers","Content-Length, JSESSIONID");
        response.setContentType("application/json");
        String s = "";
        try{
            s=VrUtils.formatJSONObject(m);
        }catch(Throwable ex){
            s=VrUtils.formatJSONObject(WebScriptServiceInvoker.buildError(ex,null));
        }
        response.getWriter().write(s);
    }


}
