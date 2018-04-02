package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.util.UIConstants;

/**
 * Created by vpc on 6/23/17.
 */
public class SeparatorPropertyView extends PropertyView{
    private String value;
    public SeparatorPropertyView(String value) {
        super(null, null, null, UIConstants.Control.SEPARATOR, null);
        this.value=value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
