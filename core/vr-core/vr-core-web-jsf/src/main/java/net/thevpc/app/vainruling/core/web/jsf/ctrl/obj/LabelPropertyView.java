package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.util.UIConstants;

/**
 * Created by vpc on 6/23/17.
 */
public class LabelPropertyView extends PropertyView{
    private String value;
    public LabelPropertyView(String value) {
        super(null, null, null, UIConstants.Control.LABEL, null);
        this.value=value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
