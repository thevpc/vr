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
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.vfs.VirtualFileSystem;

/**
 *
 * @author vpc
 */
@WebServlet(name = "RSSServlet", urlPatterns = "/services/rss/*")
public class RSSServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FileSystemService core = VrApp.getBean(FileSystemService.class);
        final VirtualFileSystem fs = core.getFileSystem();
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
        VrApp.getBean(ArticlesPlugin.class).generateRSS(null, filename, response.getOutputStream());
    }

}
