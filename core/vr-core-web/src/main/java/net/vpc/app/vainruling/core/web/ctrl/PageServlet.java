/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.VrMenuManager;
import net.vpc.app.vainruling.api.web.util.JsfCtrl;

/**
 *
 * @author vpc
 */
@WebServlet(name = "PageServlet", urlPatterns = "/p/*")
public class PageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VrMenuManager core = VrApp.getBean(VrMenuManager.class);
        String filename = URLDecoder.decode(request.getPathInfo(), "UTF-8");
        if(filename.startsWith("/")){
            filename=filename.substring(1);
        }
        if(filename.endsWith("/")){
            filename=filename.substring(0,filename.length()-1);
        }
        String newPath = core.gotoPage(filename, request.getParameter("a"));
        int i = newPath.indexOf("?");
        newPath=newPath.replaceFirst("\\?", ".xhtml?");
//        newPath="/vr"+newPath;
        request.getRequestDispatcher(newPath).forward(request, response);
    }

}
