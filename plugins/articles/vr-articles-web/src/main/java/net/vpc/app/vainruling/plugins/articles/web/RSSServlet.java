/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.common.vfs.VirtualFileSystem;

/**
 *
 * @author vpc
 */
@WebServlet(name = "RSSServlet", urlPatterns = "/services/rss/*")
public class RSSServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FileSystemPlugin core = VrApp.getBean(FileSystemPlugin.class);
        final VirtualFileSystem fs = core.getFileSystem();
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
        VrApp.getBean(ArticlesPlugin.class).generateRSS(null, filename, response.getOutputStream());
    }

}
