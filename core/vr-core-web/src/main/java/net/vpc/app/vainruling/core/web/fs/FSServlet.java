/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.streams.FileUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;

/**
 *
 * @author vpc
 */
@WebServlet(name = "FSServlet", urlPatterns = "/fs/*")
public class FSServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FileSystemService core = VrApp.getBean(FileSystemService.class);
        final VirtualFileSystem fs = core.getFileSystem();
        String filename = URLDecoder.decode(request.getPathInfo(), "UTF-8");
        VFile file = fs.get(filename);
        if (file.exists() && file.isFile()) {
            response.setHeader("Content-Type", file.probeContentType());
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            final InputStream in = (InputStream) file.getInputStream();
            FileUtils.copy(in, response.getOutputStream());
            in.close();
        } else {
            response.sendError(404);
        }
    }

}
