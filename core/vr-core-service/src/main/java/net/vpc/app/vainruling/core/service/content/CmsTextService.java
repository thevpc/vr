package net.vpc.app.vainruling.core.service.content;

import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface CmsTextService extends ContentTextService {

    void setSelectedContentTextById(int id);

    ContentText getSelectedContentText();

    String getProperty(String name);

    String getProperty(String name, String defaultValue);

    void setContentDisposition(String name);

    CmsTextDisposition getContentDispositionByName(String name);

    String getContentDispositionName();

    CmsTextDisposition getContentDisposition();

    boolean isEnabledAction(String action,int id);

    /**
     * exectues action and return true if the action wa
     * @param action
     * @param id
     * @return
     */
    boolean onAction(String action, int id);

    boolean isDispositionEnabled(String disposition);

    String getDispositionActionName(String disposition);

}
