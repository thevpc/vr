/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author vpc
 */
@WebServlet(urlPatterns = "/doit/*")
public class MaServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, 
            HttpServletResponse resp) throws ServletException, IOException {
        
        
        resp.addHeader("accept-language", "FR");
        resp.sendError(200);
//        req.getSession().
        PrintWriter w = resp.getWriter();
        w.write("<html>\n");
        w.write("</html>\n");
        resp.flushBuffer();
        resp.setContentType("text/html");
//        resp.addHeader("Content-Type","text/html");
    }
    
}
