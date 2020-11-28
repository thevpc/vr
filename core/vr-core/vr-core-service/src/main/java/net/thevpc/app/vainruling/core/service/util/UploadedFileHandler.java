package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.common.vfs.VFile;

import java.io.IOException;

/**
 * Created by vpc on 1/1/17.
 */
public interface UploadedFileHandler {
    String getFileName() throws IOException;
    void write(String path) throws IOException;
    boolean acceptOverride(VFile file) throws IOException;
}
