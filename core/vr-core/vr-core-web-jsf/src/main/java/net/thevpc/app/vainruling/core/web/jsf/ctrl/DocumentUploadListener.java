package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import org.primefaces.event.FileUploadEvent;

/**
 * Created by vpc on 7/19/17.
 */
public interface DocumentUploadListener {
    void onUpload(FileUploadEvent event);
}
