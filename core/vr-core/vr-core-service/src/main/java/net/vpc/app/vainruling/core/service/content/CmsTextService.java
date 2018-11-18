package net.vpc.app.vainruling.core.service.content;

/**
 * Created by vpc on 9/5/16.
 */
public interface CmsTextService extends ContentTextService {

    void setSelectedContentTextById(String disposition,int id);

    ContentText getSelectedContentText(String name);

    void setContentDisposition(String name);

    CmsTextDisposition getContentDispositionByName(String name);

    CmsTextDisposition getContentDisposition();

    boolean isEnabledAction(String action, int id);

    /**
     * executes action and return true if the action wa
     * @param action
     * @param id
     * @return
     */
    boolean onAction(String action, int id);

    boolean isDispositionEnabled(String disposition);

    String getDispositionActionName(String disposition);

}
