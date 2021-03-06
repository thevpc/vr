package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.editor.AutoFilter;
import net.thevpc.app.vainruling.core.service.editor.AutoFilterData;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.upa.NamedId;

public class SingleSelectionAutoFilter extends AutoFilter {

    private List<SelectItem> values = new ArrayList<>();
    private String selectedString;

    public SingleSelectionAutoFilter(AutoFilterData autoFilterData, boolean autoSelect) {
        super("single-selection", autoFilterData);
        CorePlugin core = CorePlugin.get();
        for (NamedId namedId : core.getEntityAutoFilterValues(autoFilterData.getEntityName(), autoFilterData.getName())) {
            values.add(FacesUtils.createSelectItem(namedId.getStringId(), namedId.getStringName(), ""));
        }
        if (autoSelect) {
            NamedId defaultSelection = autoFilterData.getDefaultSelectedValue();
            if (defaultSelection != null) {
                this.selectedString = defaultSelection.getStringId();
            }
        }
    }

    public List<SelectItem> getValues() {
        return values;
    }

    public void setValues(List<SelectItem> values) {
        this.values = values;
    }

    public String getSelectedString() {
        return selectedString;
    }

    public void setSelectedString(String selectedString) {
        this.selectedString = selectedString;
    }

    @Override
    public String createFilterExpression(Map<String, Object> parameters, String paramPrefix) {
        return CorePlugin.get().createEntityAutoFilterExpression(getData().getEntityName(), getData().getName(), parameters, paramPrefix, selectedString);
    }
}
