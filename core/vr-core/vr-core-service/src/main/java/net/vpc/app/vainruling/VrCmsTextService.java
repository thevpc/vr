package net.vpc.app.vainruling;

import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.ContentText;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrCmsTextService extends VrContentTextService {

    void setSelectedContentTextById(String disposition, int id);

    ContentText getSelectedContentText(String name);

    void setContentDisposition(String name);

    CmsTextDisposition getContentDispositionByName(String name);

    CmsTextDisposition getContentDisposition();

    boolean isEnabledAction(String action, ContentText ctx);

    boolean isEnabledAction(String action, int id);

    /**
     * executes action and return true if the action wa
     *
     * @param action
     * @param id
     * @return
     */
    boolean onAction(String action, int id);

    public boolean onAction(String action, ContentText a);

    boolean isDispositionEnabled(String disposition);

    String getDispositionActionName(String disposition);

}
