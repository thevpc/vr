/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service;

import java.io.IOException;

import net.thevpc.app.vainruling.VrImportFileAction;
import net.thevpc.app.vainruling.VrImportFileOptions;
import net.thevpc.app.vainruling.core.service.extensions.DefaultVrImportFileActionContext;
import net.thevpc.common.vfs.VFile;
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
            return _importFileAsFolder(file, o,d);
        }
        return _importFileAsFile(file,o);
    }

    private long _importFileAsFolder(VFile file,VrImportFileOptions options, int depth) throws IOException {
        long count = 0;
        for (VFile listFile : file.listFiles()) {
            if (file.isDirectory()) {
                if (depth > 0) {
                    count += _importFileAsFolder(listFile, options,depth - 1);
                }
            } else {
                count += _importFileAsFile(file,options);
            }
        }
        return count;
    }

    private long _importFileAsFile(VFile file,VrImportFileOptions options) throws IOException {
        for (VrImportFileAction a : VrApp.getBeansForType(VrImportFileAction.class)) {
            if (a.isAcceptFileName(file.getName(),options)) {
                return a.importFile(new DefaultVrImportFileActionContext(file));
            }
        }
        throw new IOException("Unsupported import " + file.getName());
    }

}
