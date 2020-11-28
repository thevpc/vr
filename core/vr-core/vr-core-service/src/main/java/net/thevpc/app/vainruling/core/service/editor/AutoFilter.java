package net.thevpc.app.vainruling.core.service.editor;

import java.util.Map;

/**
 * Created by vpc on 8/20/16.
 */
public abstract class AutoFilter {
    private String type;
    private AutoFilterData data;

    public AutoFilter(String type,AutoFilterData data) {
        this.type = type;
        this.data = data;
    }

    public AutoFilterData getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public abstract String createFilterExpression(Map<String, Object> parameters, String paramPrefix);
}
