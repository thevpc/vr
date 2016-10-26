/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.content;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "RSSServlet", urlPatterns = "/services/rss/*")
public class RSSServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        //final VirtualFileSystem fs = core.getFileSystem();
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
        VrApp.getBean(CorePlugin.class).generateRSS(null, filename, response.getOutputStream());
    }

}
