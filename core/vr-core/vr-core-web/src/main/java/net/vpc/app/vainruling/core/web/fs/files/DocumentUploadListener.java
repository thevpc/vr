package net.vpc.app.vainruling.core.web.fs.files;

import org.primefaces.event.FileUploadEvent;

/**
 * Created by vpc on 7/19/17.
 */
public interface DocumentUploadListener {
    void onUpload(FileUploadEvent event);
}
