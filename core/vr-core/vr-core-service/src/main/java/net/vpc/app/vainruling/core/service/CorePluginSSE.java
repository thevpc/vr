/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import java.io.IOException;
import net.vpc.app.vainruling.core.service.extensions.DefaultVrImportFileActionContext;
import net.vpc.app.vainruling.VrImportFileAction;
import net.vpc.app.vainruling.VrImportFileOptions;
import net.vpc.common.vfs.VFile;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
//Serverside Extension
@Service
public class CorePluginSSE {

    public static CorePluginSSE get() {
        return VrApp.getBean(CorePluginSSE.class);
    }

    public static VFile getRootFile(VFile file){
        if (file == null) {
            return null;
        }
        VFile f = file.getBaseFile("vrfs");
        if (f == null) {
            return null;
        }
        return f;
    }
    public static String getRootPath(VFile file){
        if (file == null) {
            return null;
        }
        VFile f = file.getBaseFile("vrfs");
        if (f == null) {
            return null;
        }
        return f.getPath();
    }

    public long importFile(VFile file, VrImportFileOptions o) throws IOException {
        if (file.isDirectory()) {
            int d = o.getMaxDepth();
            if (d <= 0) {
                d = 3;
            }
            return _importFileAsFolder(file, d);
        }
        return _importFileAsFile(file);
    }

    private long _importFileAsFolder(VFile file, int depth) throws IOException {
        long count = 0;
        for (VFile listFile : file.listFiles()) {
            if (file.isDirectory()) {
                if (depth > 0) {
                    count += _importFileAsFolder(listFile, depth - 1);
                }
            } else {
                count += _importFileAsFile(file);
            }
        }
        return count;
    }

    private long _importFileAsFile(VFile file) throws IOException {
        for (VrImportFileAction a : VrApp.getBeansForType(VrImportFileAction.class)) {
            if (a.isAcceptFileName(file.getName())) {
                return a.importFile(new DefaultVrImportFileActionContext(file));
            }
        }
        throw new IOException("Unsupported import " + file.getName());
    }

}
