package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.strings.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidatorProgressHelper {
    private double expected;
    private double score;
    private List<String> errorMessages=new ArrayList<>();

    public void check(boolean val,String message){
        check(val,1,message);
    }

    public void checkNotDefault(Object value,String errorMessage){
        boolean bval=false;
        if(value==null){
            bval=false;
        }else if(value instanceof String){
            bval= !StringUtils.isEmpty(value.toString());
        }else if(value instanceof Number){
            bval= ((Number)value).doubleValue()!=0;
        }else if(value instanceof Boolean){
            bval= ((Boolean)value).booleanValue();
        }else{
            bval=true;
        }
        check(bval,errorMessage);
    }

    public void check(boolean val){
        check(val,1,null);
    }

    public void check(boolean val,double weight,String errorMessage){
        if(val){
            score+=weight;
        }else{
            if(errorMessage!=null){
                errorMessages.add(errorMessage);
            }
        }
        expected+=weight;
    }

    public double getCompletionPercent(){
        return getCompletion()*100;
    }

    public double getCompletion(){
        if(expected==0){
            return 1;
        }
        return score/expected;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
}
