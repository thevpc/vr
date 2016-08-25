package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.Entity;
import net.vpc.upa.KeyType;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.StringType;
import org.apache.commons.lang.StringUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleSelectionAutoFilter extends AutoFilter{
    private List<SelectItem> values=new ArrayList<>();
    private String selectedString;
    private DataType dataType;

    public SingleSelectionAutoFilter(DataType dataType,AutoFilterData autoFilterData) {
        super("single-selection",autoFilterData);
        this.dataType=dataType;
    }

    public DataType getDataType() {
        return dataType;
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

    public String createFilterExpression(Map<String, Object> parameters, String paramPrefix){
        if(!StringUtils.isEmpty(selectedString)){
            if(dataType instanceof KeyType){
                int id = Integer.parseInt(selectedString);
                Entity entity = ((KeyType) dataType).getEntity();
                Object entityInstance = entity.findById(id);
                parameters.put(paramPrefix,entityInstance);
                return getData().getExpr()+"=:"+ paramPrefix;
            }else if(dataType instanceof EnumType) {
                parameters.put(paramPrefix,((Enum)((EnumType)dataType).parse(selectedString)));
                return getData().getExpr()+"=:"+paramPrefix;
            }else if(dataType instanceof StringType) {
                parameters.put(paramPrefix,selectedString);
                return getData().getExpr()+"=:"+paramPrefix;
            }else{
                throw new IllegalArgumentException("Unsupported");
            }
        }
        return null;
    }
}
