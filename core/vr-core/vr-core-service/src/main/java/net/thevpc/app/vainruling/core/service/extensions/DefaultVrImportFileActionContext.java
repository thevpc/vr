/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions;

import net.thevpc.app.vainruling.VrImportFileActionContext;
import net.thevpc.common.vfs.VFile;

/**
 *
 * @author vpc
 */
public class DefaultVrImportFileActionContext implements VrImportFileActionContext {
    private VFile filePath;

    public DefaultVrImportFileActionContext(VFile filePath) {
        this.filePath = filePath;
    }

    public VFile getFilePath() {
        return filePath;
    }
    
}
