package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.util.UploadedFileHandler;
import net.vpc.app.vainruling.core.web.fs.files.VFileInfo;
import net.vpc.common.vfs.VFile;
import org.primefaces.event.FileUploadEvent;

import java.io.IOException;

/**
 * Created by vpc on 1/1/17.
 */
public class FileUploadEventHandler implements UploadedFileHandler{
    private FileUploadEvent event;

    public FileUploadEventHandler(FileUploadEvent event) {
        this.event = event;
    }

    @Override
    public String getFileName() throws IOException {
        return event.getFile().getFileName();
    }

    @Override
    public void write(String path) throws IOException {
        try {
            event.getFile().write(path);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean acceptOverride(VFile file) {
        return true;
    }
}
