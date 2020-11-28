package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import java.io.File;
import net.thevpc.app.vainruling.core.service.util.UploadedFileHandler;
import net.thevpc.common.vfs.VFile;
import org.primefaces.event.FileUploadEvent;

import java.io.IOException;
import java.io.InputStream;
import net.thevpc.common.io.IOUtils;

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
        try (InputStream is=event.getFile().getInputstream()){
            IOUtils.copy(is, new File(path));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean acceptOverride(VFile file) {
        return true;
    }
}
