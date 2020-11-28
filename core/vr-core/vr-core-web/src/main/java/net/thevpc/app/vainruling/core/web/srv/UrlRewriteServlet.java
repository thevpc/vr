/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.srv;

import net.thevpc.app.vainruling.core.service.model.AppUrl;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.UPA;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "UrlRewriteServlet", urlPatterns = "/u/*")
public class UrlRewriteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = getPath(request);
        if(StringUtils.isBlank(path)){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String file = getPathRewritten(path);
        if (file != null) {
            if(file.startsWith("/")){
                //file=request.getContextPath()+file;
                RequestDispatcher dispatcher = request.getRequestDispatcher(file);
                dispatcher.forward(request, response);
            }else {
                response.sendRedirect(file);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

//    @Override
//    protected long getLastModified(HttpServletRequest request) {
//        VFile file = getFile(request);
//        if (file != null) {
//            return file.lastModified();
//        }
//        return -1;
//    }

//    @Override
//    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        VFile file = getFile(request);
//        if (file != null) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
//            response.setContentType(file.probeContentType());
//            response.setContentLengthLong(file.length());
//        } else {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }

    protected String getPathRewritten(String path) {
        if(StringUtils.isBlank(path)){
            return null;
        }
        AppUrl userUrl = UPA.getPersistenceUnit().findByField(AppUrl.class, "userUrl", path);
        if(userUrl!=null){
            return userUrl.getInternalURL();
        }
        return null;
    }

    protected String getPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (StringUtils.isBlank(pathInfo)) {
            return null;
        }
        String filename = null;
        try {
            filename = URLDecoder.decode(pathInfo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return filename;
    }
}
