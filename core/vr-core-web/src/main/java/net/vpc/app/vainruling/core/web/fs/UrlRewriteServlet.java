/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUrl;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.UPA;

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
        String file = getPathRewritten(request);
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

    protected String getPathRewritten(HttpServletRequest request) {
        String path = getPath(request);
        if(StringUtils.isEmpty(path)){
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
        if (StringUtils.isEmpty(pathInfo)) {
            return null;
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final VirtualFileSystem fs = core.getFileSystem();
        if(fs==null){
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
