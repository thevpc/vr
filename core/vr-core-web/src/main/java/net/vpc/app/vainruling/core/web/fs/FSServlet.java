/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.common.streams.FileUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@WebServlet(name = "FSServlet", urlPatterns = "/fs/*")
public class FSServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(FSServlet.class.getName());
    final int DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VFile file = getFile(request);
        if (file != null) {
//            response.setHeader("Content-Type", file.probeContentType());
//            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            response.setContentType(file.probeContentType());
            long length = file.length();
            response.setContentLengthLong(length);
            InputStream in = null;
            try {
                in = file.getInputStream();
                int buffezSize = DEFAULT_BUFFER_SIZE;
                if (length > 0 && length < DEFAULT_BUFFER_SIZE) {
                    buffezSize = (int) length;
                }
                response.setBufferSize(buffezSize);
                try {
                    FileUtils.copy(in, response.getOutputStream(), buffezSize);
                }catch(java.io.IOException ex){
                    if(ex.getClass().getName().endsWith(".ClientAbortException")){
                        //this is a tomcat 'Broken pipe' handling i suppose
                        log.log(Level.SEVERE, "Error serving file "+file.getPath()+". "+ex.getMessage());
                    }else{
                        log.log(Level.SEVERE, "Error serving file "+file.getPath()+". "+ex.getMessage(),ex);
                    }
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected long getLastModified(HttpServletRequest request) {
        VFile file = getFile(request);
        if (file != null) {
            return file.lastModified();
        }
        return -1;
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VFile file = getFile(request);
        if (file != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            response.setContentType(file.probeContentType());
            response.setContentLengthLong(file.length());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected VFile getFile(HttpServletRequest request) {
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
        VFile file = fs.get(filename);
        if (file!=null && file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }
}
