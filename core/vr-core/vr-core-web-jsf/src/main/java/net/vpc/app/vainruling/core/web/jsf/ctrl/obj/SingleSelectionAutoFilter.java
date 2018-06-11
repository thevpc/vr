package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.obj.AutoFilter;
import net.vpc.app.vainruling.core.service.obj.AutoFilterData;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.Entity;
import net.vpc.upa.KeyType;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.StringType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleSelectionAutoFilter extends AutoFilter {
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
                int id = Convert.toInt(selectedString,IntegerParserConfig.LENIENT_F);
                Entity entity = ((KeyType) dataType).getEntity();
                Object entityInstance = entity.findById(id);
                parameters.put(paramPrefix,entityInstance);
                //IsHierarchyDescendant(:p,a,Node)
                if(entity.isHierarchical()){
                    return  "IsHierarchyDescendant(:"+paramPrefix+" , "+ getData().getExpr() + "," +entity.getName()+")" ;
                }else {
                    return getData().getExpr() + "=:" + paramPrefix;
                }
            }else if(dataType instanceof EnumType) {
                if(selectedString.startsWith("\"") && selectedString.endsWith("\"") && selectedString.length()>=2){
                    selectedString=selectedString.substring(1,selectedString.length()-1);
                }
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
