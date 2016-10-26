package net.vpc.app.vainruling.core.service.content;

import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface CmsTextService extends ContentTextService {

    public void setSelectedContentTextById(int id);

    public ContentText getSelectedContentText();

    public String getProperty(String name);

    public String getProperty(String name, String defaultValue);

    public void setContentDisposition(String name);

    public CmsTextDisposition getContentDispositionByName(String name);

    public String getContentDispositionName();

    public CmsTextDisposition getContentDisposition();
}
