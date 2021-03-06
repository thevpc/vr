/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.MessageFormat;

import net.thevpc.app.vainruling.core.web.jsf.Vr;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "PageServlet", urlPatterns = "/p/*", loadOnStartup = 10)
public class PageServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Vr mm = Vr.get();
        String filename = URLDecoder.decode(request.getPathInfo(), "UTF-8");
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }
        if (filename.endsWith("/")) {
            filename = filename.substring(0, filename.length() - 1);
        }
        String newPath = mm.gotoPage(filename, request.getParameter("a"));
        if (newPath == null) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            if (core.isLoggedIn()) {
                request.getRequestDispatcher("/p/welcome").forward(request, response);
            } else {
                request.getRequestDispatcher("/p/login").forward(request, response);
            }
            return;
        }
        int i = newPath.indexOf("?");
        newPath = newPath.replaceFirst("\\?", ".xhtml?");
//        newPath="/vr"+newPath;
        request.getRequestDispatcher(newPath).forward(request, response);
    }

}
