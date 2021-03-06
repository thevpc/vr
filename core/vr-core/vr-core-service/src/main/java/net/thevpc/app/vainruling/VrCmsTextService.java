package net.thevpc.app.vainruling;

import net.thevpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrCmsTextService extends VrContentTextService {

    void setSelectedContentTextById(String disposition, int id);

    VrContentText getSelectedContentText(String name);

    void setContentDisposition(String name);

    CmsTextDisposition getContentDispositionByName(String name);

    CmsTextDisposition getContentDisposition();

    boolean isEnabledAction(String action, VrContentText ctx);

    boolean isEnabledActionById(String action, int id);

    /**
     * executes action and return true if the action wa
     *
     * @param action
     * @param id
     * @return
     */
    boolean onActionById(String action, int id);

    public void runAction(String action, VrContentText a);
    
    public boolean onAction(String action, VrContentText a);

    boolean isDispositionEnabled(String disposition);

    String getDispositionActionName(String disposition);

}
