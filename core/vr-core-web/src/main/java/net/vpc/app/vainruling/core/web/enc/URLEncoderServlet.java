/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.enc;

import net.vpc.common.strings.StringUtils;
import org.apache.commons.codec.binary.Base32;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "URLEncoderServlet", urlPatterns = "/ue/*",loadOnStartup = 10)
public class URLEncoderServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(URLEncoderServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (StringUtils.isEmpty(pathInfo)) {
            response.sendRedirect("/");
            return;
        }
        if(pathInfo.startsWith("/")){
            pathInfo=pathInfo.substring(1);
        }
        try {
            Base32 b32 = new Base32();
            String s = new String(b32.decode(pathInfo));
            //force local url redirection!
            if (!s.startsWith("/")) {
                s = "/" + s;
            }
//            request.getRequestDispatcher(s).forward(request, response);
            response.sendRedirect(s);
        } catch (Exception ex) {
            response.sendRedirect("/");
        }
    }
}
